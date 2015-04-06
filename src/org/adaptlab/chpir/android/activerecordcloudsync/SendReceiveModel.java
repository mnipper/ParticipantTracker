package org.adaptlab.chpir.android.activerecordcloudsync;

import org.json.JSONObject;

import com.activeandroid.Model;

public abstract class SendReceiveModel extends Model { 
    public abstract JSONObject toJSON();
    public abstract boolean isSent();
    public abstract boolean readyToSend();
    public abstract void setAsSent();
    public abstract void createObjectFromJSON(JSONObject jsonObject);
	public abstract Long getRemoteId();
	public abstract boolean isPersistent();
}