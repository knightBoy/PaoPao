package com.example.knightboy.doudou.bean;

import java.io.Serializable;

/**
 * Created by knightBoy on 2015/9/2.
 */
public class Comment implements Serializable {
    private int id;
    private int stateId;
    private int userId;
    private String contents;

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
