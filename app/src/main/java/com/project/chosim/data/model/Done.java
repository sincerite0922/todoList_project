package com.project.chosim.data.model;

import android.text.TextUtils;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Done {
    @DocumentId
    private String documentId;

    // 루틴 ID
    private String routineId;

    // 자신의 화면에서 상대방의 루틴 수행 여부도 확인할 수 있어야 하기 때문에 routine 의 uidList 도 필요하다.
    private List<String> uidList = new ArrayList<>();

    private String uid;
    private String date;

    @ServerTimestamp
    private Date timestamp;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRoutineId() {
        return routineId;
    }

    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Exclude
    public boolean isDone(String uid, String documentId) {
        return TextUtils.equals(uid, this.uid) && TextUtils.equals(routineId, documentId);
    }
}
