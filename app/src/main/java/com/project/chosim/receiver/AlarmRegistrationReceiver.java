package com.project.chosim.receiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.chosim.Constants;
import com.project.chosim.data.model.Routine;
import com.project.chosim.data.repositories.RoutineRemoteRepository;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class AlarmRegistrationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        Task asyncTask = new Task(context, pendingResult);
        asyncTask.execute();
    }

    private static class Task extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private final Context context;
        private final PendingResult pendingResult;
        private final AlarmManager alarmManager;

        private final FirebaseAuth auth = FirebaseAuth.getInstance();
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();


        public Task(Context context, PendingResult pendingResult) {
            this.context = context;
            this.pendingResult = pendingResult;
            this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Intent intent;
            PendingIntent alarmIntent;

            //region Midnight
            intent = new Intent(context, AlarmRegistrationReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmIntent = PendingIntent.getBroadcast(
                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                alarmIntent = PendingIntent.getBroadcast(
                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            Calendar today = Calendar.getInstance();
            today.set(Calendar.SECOND, 0);

            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DATE, 1);
            tomorrow.set(Calendar.HOUR_OF_DAY, 0);
            tomorrow.set(Calendar.MINUTE, 0);
            tomorrow.set(Calendar.SECOND, 0);

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);
            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    tomorrow.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    alarmIntent);
            //endregion

            //region Cancel all alarms
            intent = new Intent(context, AlarmReceiver.class);

            for (int i = 1; i <= 100; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmIntent = PendingIntent.getBroadcast(
                            context, 0, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
                } else {
                    alarmIntent = PendingIntent.getBroadcast(
                            context, 0, intent, PendingIntent.FLAG_NO_CREATE);
                }

                if (alarmIntent != null) {
                    alarmManager.cancel(alarmIntent);
                }
            }

            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                return null;
            }
            //endregion

            //region Rate yesterday routines
            RoutineRemoteRepository.getInstance().rateRoutines(user.getUid(), yesterday);
            //endregion

            //region Routine alarm
            int week = today.get(Calendar.DAY_OF_WEEK);
            int requestCode = 1;

            try {
                QuerySnapshot snapshot = Tasks.await(db.collection(Constants.ROUTINES)
                        .whereArrayContains("uidList", user.getUid())
                        .whereEqualTo("cycle." + week, true)
                        .get());

                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    Routine routine = doc.toObject(Routine.class);

                    if (!routine.isAlarm()) continue;
                    intent.putExtra("routine", routine);

                    int hour = Integer.parseInt(routine.getAlarmTime().split(":")[0]);
                    int minute = Integer.parseInt(routine.getAlarmTime().split(":")[1]);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);

                    int loop = routine.isRepeat() ? 3 : 1;

                    for (int i = 0; i < loop; i++) {
                        calendar.add(Calendar.MINUTE, i * 5);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            alarmIntent = PendingIntent.getBroadcast(
                                    context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                        } else {
                            alarmIntent = PendingIntent.getBroadcast(
                                    context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        }

                        requestCode += 1;

                        long triggerAtMills = calendar.getTimeInMillis() - today.getTimeInMillis();

                        // Midnight
                        if (today.get(Calendar.HOUR_OF_DAY) == 0 && today.get(Calendar.MINUTE) == 0) {
                            if (triggerAtMills <= 0) {
                                registerAlarm(0, alarmIntent);
                            } else {
                                registerAlarm(calendar.getTimeInMillis(), alarmIntent);
                            }
                        } else {
                            if (triggerAtMills < 0) continue;

                            registerAlarm(calendar.getTimeInMillis(), alarmIntent);
                        }
                    }
                }
            } catch (ExecutionException e) {
                e.printStackTrace();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //endregion

            return null;
        }

        private void registerAlarm(long triggerAtMillis, PendingIntent intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, intent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, intent);
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, intent);
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            pendingResult.finish();
        }
    }
}
