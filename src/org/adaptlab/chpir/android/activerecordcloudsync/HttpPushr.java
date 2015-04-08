package org.adaptlab.chpir.android.activerecordcloudsync;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.adaptlab.chpir.android.participanttracker.models.AdminSettings;
import org.apache.commons.codec.CharEncoding;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.activeandroid.query.Select;

public class HttpPushr {
    private static final String TAG = "HttpPushr";
    private Class<? extends SendModel> mSendTableClass;
    private String mRemoteTableName;

    public HttpPushr(String remoteTableName,
            Class<? extends SendModel> sendTableClass) {
        mSendTableClass = sendTableClass;
        mRemoteTableName = remoteTableName;
    }
    
    public void push() {
        if (ActiveRecordCloudSync.getEndPoint() == null) {
            Log.i(TAG, "ActiveRecordCloudSync end point is not set!");
            return;
        }

        List<? extends SendReceiveModel> allElements = getElements();

        try {
            if (isPersistent()) {
                for (SendReceiveModel element : allElements) {
                    sendData(element);
                }
            } else {
                sendData(mSendTableClass.newInstance());   
            }
        } catch (InstantiationException ie) {
            Log.e(TAG, "InstantiationException: " + ie);
        } catch (IllegalAccessException ie) {
            Log.e(TAG, "IllegalAccessException: " + ie);
        }
    }

    public void sendData(SendModel element) {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams
                    .setConnectionTimeout(client.getParams(), 10000); // Timeout limit

            HttpResponse response;

            if (!element.isSent() && element.readyToSend()) {
                try {
                	StringEntity se = new StringEntity(element.toJSON().toString(), CharEncoding.UTF_8);
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    if (element.getRemoteId() != null) {
                    	HttpPut put = new HttpPut(ActiveRecordCloudSync.getEndPoint() + mRemoteTableName + '/' + element.getRemoteId().toString() + '/' + ActiveRecordCloudSync.getParams());
                    	put.setEntity(se);
                    	Log.i(TAG, "Sending put request: " + element.toJSON().toString());
	                    response = client.execute(put);
                    } else {
	                	HttpPost post = new HttpPost(ActiveRecordCloudSync.getEndPoint() + mRemoteTableName + ActiveRecordCloudSync.getParams());
	                    post.setEntity(se);
	                    Log.i(TAG, "Sending post request: " + element.toJSON().toString());
	                    response = client.execute(post);
                    }
                    /* Checking for successful response */
                    if (response.getStatusLine().getStatusCode() >= 200
                            && response.getStatusLine().getStatusCode() < 300) {
                        Log.i(TAG,
                                "Received OK HTTP status for "
                                        + element.toJSON());
                        InputStream in = response.getEntity().getContent();
                        element.setAsSent();
                        AdminSettings.getInstance().setLastUpdateTime(new Date().toString());
                    } else {
                        Log.e(TAG, "Received BAD HTTP status code " + response.getStatusLine().getStatusCode()
                                + " for " + element.toJSON());
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Cannot establish connection", e);
                }

            }

    }

	public List<? extends SendReceiveModel> getElements() {
		return new Select().from(mSendTableClass).orderBy("Id ASC").execute();
	}
	
	private boolean isPersistent() throws InstantiationException, IllegalAccessException {
	    SendModel sendModel = mSendTableClass.newInstance();
        return sendModel.isPersistent();
	}
}
