package com.project.chosim.data.repositories;

import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.WriteBatch;
import com.project.chosim.Constants;
import com.project.chosim.data.model.Done;
import com.project.chosim.data.model.Routine;
import com.project.chosim.utils.CombinedLiveData;
import com.project.chosim.utils.ParcelableUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoutineRemoteRepository {

    private static RoutineRemoteRepository instance;

    public static RoutineRemoteRepository getInstance() {
        if (instance == null) {
            instance = new RoutineRemoteRepository();
        }

        return instance;
    }


    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    private RoutineRemoteRepository() {
    }

    public void rateRoutines(@NonNull String uid, Calendar calendar) {
        String date = Constants.dateFormat.format(calendar.getTime());
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        DocumentReference myUserReference = db.collection(Constants.USERS).document(uid);

        myUserReference.collection(Constants.RATE)
                .document(date)
                .get(Source.SERVER)
                .continueWithTask(task -> {
                    if (task.getException() != null) throw task.getException();

                    if (task.getResult().exists()) return Tasks.forResult(null);

                    ArrayList<Task<QuerySnapshot>> tasks = new ArrayList<>();

                    tasks.add(db.collection(Constants.ROUTINES)
                            .whereArrayContains("uidList", uid)
                            .whereEqualTo("cycle." + week, true).get(Source.SERVER));

                    tasks.add(db.collection(Constants.DONE)
                            .whereEqualTo("uid", uid)
                            .whereEqualTo("date", date).get(Source.SERVER));

                    return Tasks.whenAllSuccess(tasks);
                })
                .continueWithTask(task -> {
                    if (task.getException() != null) throw task.getException();

                    if (task.getResult() == null) return Tasks.forResult(null);

                    QuerySnapshot routineQuerySnapshot = (QuerySnapshot) task.getResult().get(0);
                    QuerySnapshot doneQuerySnapshot = (QuerySnapshot) task.getResult().get(1);

                    double totalCount = routineQuerySnapshot.size();
                    double doneCount = 0;

                    ArrayList<Done> doneList = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : doneQuerySnapshot) {
                        doneList.add(queryDocumentSnapshot.toObject(Done.class));
                    }

                    for (DocumentSnapshot snapshot : routineQuerySnapshot) {
                        String documentId = snapshot.getReference().getId();

                        if (doneList.stream().anyMatch(done -> done.isDone(uid, documentId))) {
                            doneCount += 1;
                        }
                    }

                    int rate = 0;
                    if (totalCount > 0) {
                        rate = (int) (Math.round(doneCount / totalCount * 100));
                    }

                    WriteBatch batch = db.batch();

                    if (rate == 100) {
                        batch.update(myUserReference, "point", FieldValue.increment(10));
                    }

                    HashMap<String, Object> data = new HashMap<>();
                    data.put("rate", rate);
                    data.put("timestamp", FieldValue.serverTimestamp());

                    batch.set(myUserReference.collection(Constants.RATE).document(date), data);

                    return batch.commit();
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public ListenerRegistration getRoutines(@NonNull String uid, int week, @NonNull Consumer<List<Routine>> callback) {
        return db.collection(Constants.ROUTINES)
                .whereArrayContains("uidList", uid)
                .whereEqualTo("cycle." + week, true)
                // .orderBy("name")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }

                    ArrayList<Routine> routines = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Routine routine = doc.toObject(Routine.class);

                        for (int i = 0; i < routine.getUidList().size(); i++) {
                            String _uid = routine.getUidList().get(i);
                            String _name = routine.getUserNameList().get(i);

                            if (TextUtils.equals(_uid, uid)) {
                                routine.setMyUid(_uid);
                                routine.setMyName(_name);

                            } else {
                                routine.setYourUid(_uid);
                                routine.setYourName(_name);
                            }
                        }

                        if (routine.getMyUid() == null || routine.getMyName() == null) continue;
                        if (routine.isTogether() && (routine.getYourUid() == null || routine.getYourName() == null))
                            continue;

                        routines.add(routine);
                    }

                    callback.accept(routines);
                });
    }

    public ListenerRegistration getDoneList(@NonNull String uid, String date, @NonNull Consumer<List<Done>> callback) {
        return db.collection(Constants.DONE)
                .whereArrayContains("uidList", uid)
                .whereEqualTo("date", date)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }

                    ArrayList<Done> doneList = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Done done = doc.toObject(Done.class);
                        doneList.add(done);
                    }

                    callback.accept(doneList);
                });
    }

    public LiveData<List<Routine>> combine(LiveData<List<Routine>> _routines, LiveData<List<Done>> _doneList) {
        return new CombinedLiveData<List<Routine>, List<Done>, List<Routine>>(_routines, _doneList) {
            @Override
            public List<Routine> map(List<Routine> routines, List<Done> doneList) {
                if (routines == null) return null;

                ArrayList<Routine> clonedRoutines = new ArrayList<>();
                for (Routine routine : routines) {
                    clonedRoutines.add(ParcelableUtils.copy(routine));
                }

                for (Routine routine : clonedRoutines) {
                    String documentId = routine.getDocumentId();

                    if (doneList != null) {
                        routine.setMyDone(doneList.stream().anyMatch(done -> done.isDone(routine.getMyUid(), documentId)));

                        if (routine.isTogether()) {
                            routine.setYourDone(doneList.stream().anyMatch(done -> done.isDone(routine.getYourUid(), documentId)));
                        }
                    }
                }

                // 정렬
                // 개인 루틴 - 친구와의 루틴 순
                // 친구와의 루틴은 친구의 이름으로 정렬
                Collections.sort(clonedRoutines, new Comparator<Routine>() {
                    @Override
                    public int compare(Routine o1, Routine o2) {
                        if (o1.getUidList().size() > o2.getUidList().size()) {
                            return 1;

                        } else if (o1.getUidList().size() < o2.getUidList().size()) {
                            return -1;

                        } else {
                            if (o1.isTogether()) {
                                return o1.getYourName().compareTo(o2.getYourName());

                            } else {
                                return o1.getTitle().compareTo(o2.getTitle());
                            }
                        }
                    }
                });

                if (!clonedRoutines.isEmpty()) {
                    String yourName = clonedRoutines.get(0).getYourName();
                    clonedRoutines.get(0).setTag(yourName == null ? 0 : 1);

                    for (int i = 1; i < clonedRoutines.size(); i++) {
                        Routine routine = clonedRoutines.get(i);
                        routine.setTag(0);

                        if (yourName == null) {
                            if (routine.getYourName() != null) {
                                routine.setTag(1);
                                yourName = routine.getYourName();
                            }
                        } else {
                            if (!yourName.equals(routine.getYourName())) {
                                routine.setTag(1);
                                yourName = routine.getYourName();
                            }
                        }
                    }
                }

                return clonedRoutines;
            }
        };
    }

    public void done(String uid, Routine routine, String date, boolean selected) {
        if (selected) {
            Done done = new Done();
            done.setRoutineId(routine.getDocumentId());
            done.setUidList(routine.getUidList());
            done.setUid(uid);
            done.setDate(date);

            db.collection(Constants.DONE)
                    .document(date + "_" + routine.getDocumentId() + "_" + uid)
                    .set(done);

        } else {
            db.collection(Constants.DONE)
                    .document(date + "_" + routine.getDocumentId() + "_" + uid)
                    .delete();
        }
    }

    public void createRoutine(String uid,
                              String name,
                              String title,
                              boolean alarm,
                              String alarmTime,
                              Map<String, Boolean> cycle,
                              boolean repeat,
                              boolean privateRoutine,
                              Pair<String, String> friend) {
        Routine routine = new Routine();

        routine.getUidList().add(uid);
        routine.getUserNameList().add(name);

        if (friend != null) {
            routine.getUidList().add(friend.first);
            routine.getUserNameList().add(friend.second);
        }

        routine.setTitle(title);
        routine.setAlarm(alarm);
        routine.setAlarmTime(alarmTime);
        routine.setCycle(cycle);
        routine.setRepeat(repeat);
        routine.setPrivateRoutine(privateRoutine);

        db.collection(Constants.ROUTINES)
                .document()
                .set(routine);
    }

    public void updateRoutine(Routine routine,
                              String title,
                              boolean alarm,
                              String alarmTime,
                              Map<String, Boolean> cycle,
                              boolean repeat,
                              boolean privateRoutine) {
        db.collection(Constants.ROUTINES)
                .document(routine.getDocumentId())
                .update("title", title,
                        "alarm", alarm,
                        "alarmTime", alarmTime,
                        "cycle", cycle,
                        "repeat", repeat,
                        "privateRoutine", privateRoutine);
    }

    public void deleteRoutine(Routine routine) {
        db.collection(Constants.ROUTINES)
                .document(routine.getDocumentId())
                .delete();
    }

    public ListenerRegistration getRoutine(String documentId, Consumer<Routine> callback) {
        return db.collection(Constants.ROUTINES)
                .document(documentId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }

                    if (!value.exists()) {
                        callback.accept(null);

                    } else {
                        callback.accept(value.toObject(Routine.class));
                    }
                });
    }
}
