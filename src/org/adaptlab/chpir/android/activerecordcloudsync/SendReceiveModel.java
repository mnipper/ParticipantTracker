package org.adaptlab.chpir.android.activerecordcloudsync;

import org.json.JSONObject;

import com.activeandroid.Model;

public abstract class SendReceiveModel extends SendModel {
    public abstract void createObjectFromJSON(JSONObject jsonObject);
}