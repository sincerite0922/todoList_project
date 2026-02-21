package com.project.chosim.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Routine implements Parcelable {
    public static final Creator<Routine> CREATOR = new Creator<Routine>() {
        @Override
        public Routine createFromParcel(Parcel in) {
            return new Routine(in);
        }

        @Override
        public Routine[] newArray(int size) {
            return new Routine[size];
        }
    };
    @DocumentId
    private String documentId = null;
    private List<String> uidList = new ArrayList<>();
    private List<String> userNameList = new ArrayList<>();
    private String title = null;
    private boolean alarm = false;
    private String alarmTime = null;
    private Map<String, Boolean> cycle = new HashMap<>();
    private boolean repeat = false;
    private boolean privateRoutine = false;
    @Exclude
    private boolean myDone = false;
    @Exclude
    private boolean yourDone = false;
    @Exclude
    private String myUid = null;
    @Exclude
    private String yourUid = null;
    @Exclude
    private String myName = null;
    @Exclude
    private String yourName = null;
    @Exclude
    private Object tag = null;

    public Routine() {
    }

    public Routine(List<String> uidList, List<String> userNameList, String title, boolean alarm, String alarmTime, HashMap<String, Boolean> cycle, boolean repeat, boolean privateRoutine) {
        this.uidList = uidList;
        this.userNameList = userNameList;
        this.title = title;
        this.alarm = alarm;
        this.alarmTime = alarmTime;
        this.cycle = cycle;
        this.repeat = repeat;
        this.privateRoutine = privateRoutine;
    }

    protected Routine(Parcel in) {
        documentId = in.readString();
        uidList = in.createStringArrayList();
        userNameList = in.createStringArrayList();
        title = in.readString();
        alarm = in.readByte() != 0;
        alarmTime = in.readString();
        repeat = in.readByte() != 0;
        privateRoutine = in.readByte() != 0;
        myDone = in.readByte() != 0;
        yourDone = in.readByte() != 0;
        myUid = in.readString();
        yourUid = in.readString();
        myName = in.readString();
        yourName = in.readString();

        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            boolean value = in.readByte() != 0;

            cycle.put(key, value);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeStringList(uidList);
        dest.writeStringList(userNameList);
        dest.writeString(title);
        dest.writeByte((byte) (alarm ? 1 : 0));
        dest.writeString(alarmTime);
        dest.writeByte((byte) (repeat ? 1 : 0));
        dest.writeByte((byte) (privateRoutine ? 1 : 0));
        dest.writeByte((byte) (myDone ? 1 : 0));
        dest.writeByte((byte) (yourDone ? 1 : 0));
        dest.writeString(myUid);
        dest.writeString(yourUid);
        dest.writeString(myName);
        dest.writeString(yourName);

        dest.writeInt(cycle.size());
        for (Map.Entry<String, Boolean> entry : cycle.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeByte((byte) (entry.getValue() ? 1 : 0));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public List<String> getUidList() {
        return uidList;
    }

    public void setUidList(List<String> uidList) {
        this.uidList = uidList;
    }

    public List<String> getUserNameList() {
        return userNameList;
    }

    public void setUserNameList(List<String> userNameList) {
        this.userNameList = userNameList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Map<String, Boolean> getCycle() {
        return cycle;
    }

    public void setCycle(Map<String, Boolean> cycle) {
        this.cycle = cycle;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isPrivateRoutine() {
        return privateRoutine;
    }

    public void setPrivateRoutine(boolean privateRoutine) {
        this.privateRoutine = privateRoutine;
    }

    @Exclude
    public boolean isMyDone() {
        return myDone;
    }

    @Exclude
    public void setMyDone(boolean myDone) {
        this.myDone = myDone;
    }

    @Exclude
    public boolean isYourDone() {
        return yourDone;
    }

    @Exclude
    public void setYourDone(boolean yourDone) {
        this.yourDone = yourDone;
    }

    @Exclude
    public String getMyUid() {
        return myUid;
    }

    @Exclude
    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    @Exclude
    public String getYourUid() {
        return yourUid;
    }

    @Exclude
    public void setYourUid(String yourUid) {
        this.yourUid = yourUid;
    }

    @Exclude
    public String getMyName() {
        return myName;
    }

    @Exclude
    public void setMyName(String myName) {
        this.myName = myName;
    }

    @Exclude
    public String getYourName() {
        return yourName;
    }

    @Exclude
    public void setYourName(String yourName) {
        this.yourName = yourName;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Exclude
    public boolean checkValidation(String myUid, Map<String, String> friends) {
        if (isTogether()) {
            boolean validation = false;

            for (String uid : getUidList()) {
                if (TextUtils.equals(uid, myUid)) continue;
                if (friends.containsKey(uid)) {
                    validation = true;
                    break;
                }
            }

            return validation;
        }

        return getUidList().contains(myUid);
    }

    @Exclude
    public boolean isTogether() {
        return getUidList().size() == 2;
    }
}
