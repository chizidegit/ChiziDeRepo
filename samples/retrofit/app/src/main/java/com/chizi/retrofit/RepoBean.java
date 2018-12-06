package com.chizi.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chenll on 2018/12/6.
 */

public class RepoBean {
    /**
     * id : 160470879
     * name : ChiziDeRepo
     * description : Chizi's Repo
     */

    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("description")
    private String mDescription;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @Override
    public String toString() {
        return mName;
    }
}
