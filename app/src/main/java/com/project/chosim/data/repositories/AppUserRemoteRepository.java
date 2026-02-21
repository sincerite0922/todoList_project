package com.project.chosim.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.chosim.Constants;
import com.project.chosim.data.model.AppUser;
import com.project.chosim.data.model.Routine;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppUserRemoteRepository {

    private static AppUserRemoteRepository instance;
    private static final String TAG = "AppUserRemoteRepository";

    public static AppUserRemoteRepository getInstance() {
        if (instance == null) {
            instance = new AppUserRemoteRepository();
        }

        return instance;
    }


    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    private AppUserRemoteRepository() {
    }

    public void signIn(String idToken, Consumer<FirebaseUser> callback) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .continueWithTask(task -> {
                    if (task.getException() != null) throw task.getException();

                    FirebaseUser user = auth.getCurrentUser();

                    assert user != null;

                    return db.runTransaction(transaction -> {
                        DocumentSnapshot snapshot = transaction.get(db.collection(Constants.USERS).document(user.getUid()));
                        if (snapshot.exists()) return (Void) null;

                        transaction.set(snapshot.getReference(), new AppUser(user));
                        return (Void) null;
                    });
                })
                .addOnCompleteListener((OnCompleteListener<Void>) task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;

                        callback.accept(user);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        auth.signOut();
                        callback.accept(null);
                    }
                });
    }

    public ListenerRegistration getAppUser(@NonNull String uid, @NonNull Consumer<AppUser> callback) {
        return db.collection(Constants.USERS)
                .document(uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }

                    if (!value.exists()) {
                        if (!value.getMetadata().isFromCache()) {
                            callback.accept(null);
                        }
                    } else {
                        callback.accept(value.toObject(AppUser.class));
                    }
                });
    }

    public void addFriend(String uid, String email, Consumer<Boolean> callback) {
        db.collection(Constants.USERS)
                .whereEqualTo("email", email)
                .get()
                .continueWithTask(task -> {
                    if (task.getException() != null) throw task.getException();

                    QuerySnapshot snapshot = task.getResult();
                    if (snapshot.getDocuments().isEmpty()) {
                        return Tasks.forResult(false);
                    }

                    AppUser friend = snapshot.getDocuments().get(0).toObject(AppUser.class);

                    return db.collection(Constants.USERS)
                            .document(uid)
                            .update("friends." + friend.getDocumentId(), friend.getName())
                            .continueWithTask(task1 -> {
                                if (task.getException() != null) throw task.getException();

                                return Tasks.forResult(true);
                            });
                })
                .addOnCompleteListener(task -> {
                    if (task.getException() != null) {
                        task.getException().printStackTrace();
                        callback.accept(null);

                    } else {
                        callback.accept(task.getResult());
                    }
                });
    }

    public void removeFriend(@NonNull String uid, @NonNull String friendUid, @NonNull Consumer<Boolean> callback) {
        db.collection(Constants.ROUTINES)
                .whereArrayContains("uidList", uid)
                .get()
                .continueWithTask(task -> {
                    if (task.getException() != null) throw task.getException();

                    return db.runBatch(batch -> {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            Routine routine = snapshot.toObject(Routine.class);

                            if (routine.getUidList().contains(friendUid)) {
                                batch.delete(snapshot.getReference());
                            }
                        }

                        HashMap<String, Object> data = new HashMap<>();
                        data.put("friends." + friendUid, FieldValue.delete());

                        batch.update(db.collection(Constants.USERS).document(uid), data);
                    });
                })
                .addOnCompleteListener(task -> {
                    if (task.getException() != null) {
                        task.getException().printStackTrace();
                        callback.accept(false);

                    } else {
                        callback.accept(true);
                    }
                });
    }

    public void changeDisplayName(@NonNull FirebaseUser user, String displayName, Consumer<Boolean> callback) {
        ArrayList<Task<Void>> tasks = new ArrayList<Task<Void>>();
        tasks.add(user.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
        ));
        tasks.add(db.collection(Constants.USERS).document(user.getUid()).update("name", displayName));

        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
    }

    public ListenerRegistration getRates(String uid, int year, int month, Consumer<Map<Integer, Integer>> callback) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        String start = Constants.dateFormat.format(calendar.getTime());

        calendar.add(Calendar.MONTH, 1);

        String end = Constants.dateFormat.format(calendar.getTime());

        return db.collection(Constants.USERS)
                .document(uid)
                .collection(Constants.RATE)
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), start)
                .whereLessThan(FieldPath.documentId(), end)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            error.printStackTrace();
                            return;
                        }

                        HashMap<Integer, Integer> rates = new HashMap<>();

                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Long rate = snapshot.getLong("rate");
                            if (rate == null) {
                                rate = 0L;
                            }

                            try {
                                Date date = Constants.dateFormat.parse(snapshot.getId());
                                Calendar c = Calendar.getInstance();
                                c.setTime(date);

                                rates.put(c.get(Calendar.DAY_OF_MONTH), Math.toIntExact(rate));

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        callback.accept(rates);
                    }
                });
    }
}
