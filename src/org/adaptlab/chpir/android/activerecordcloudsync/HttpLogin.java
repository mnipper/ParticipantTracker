package org.adaptlab.chpir.android.activerecordcloudsync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.util.Log;

public class HttpLogin {
	private static final String TAG = "HttpLogin";
	private String mSessions;
	
	public HttpLogin() {
		mSessions = "sessions";
	}
	
	public void login(JSONObject user) {
		if (ActiveRecordCloudSync.getEndPoint() == null) {
            Log.i(TAG, "ActiveRecordCloudSync end point is not set!");
            return;
        }
		HttpClient client = setUpHttpClient();         
        makePostRequest(user, client);
	}

	private HttpClient setUpHttpClient() {
		HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
		return client;
	}
	
	public void logout() {
		if (ActiveRecordCloudSync.getEndPoint() == null) {
            Log.i(TAG, "ActiveRecordCloudSync end point is not set!");
            return;
        }
		HttpClient client = setUpHttpClient();
		try {
			HttpResponse response;
			String url = ActiveRecordCloudSync.getEndPoint() + mSessions + "/" + "?auth_token=" + ActiveRecordCloudSync.getAuthToken();
			HttpDelete delete = new HttpDelete(url);
			delete.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			response = client.execute(delete);
			if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
                Log.i(TAG, "Received OK HTTP status for delete");
                ActiveRecordCloudSync.setAuthToken(null);
                ActiveRecordCloudSync.setUserEmail(null);                
            } else {
                Log.e(TAG, "Received BAD HTTP status code " + response.getStatusLine().getStatusCode() + " for  delete request");
            }			
		} catch(Exception e) {
			Log.e(TAG, "Cannot establish connection", e);
		}
	}

	private void makePostRequest(JSONObject user, HttpClient client) {
		HttpResponse response;
		try {
        	StringEntity se = new StringEntity(user.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            HttpPost post = new HttpPost(ActiveRecordCloudSync.getEndPoint() + mSessions);
            post.setEntity(se);
            Log.i(TAG, "Sending post request for user");
            response = client.execute(post);
            
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
                Log.i(TAG, "Received OK HTTP status for user");
                String in = inputStreamToString(response.getEntity().getContent()).toString();
                JSONObject json = new JSONObject(in);
                JSONObject data = json.getJSONObject("data");
                String authToken = data.getString("auth_token");
                ActiveRecordCloudSync.setAuthToken(authToken);
                
            } else {
                Log.e(TAG, "Received BAD HTTP status code " + response.getStatusLine().getStatusCode() + " for user");
            }
           
        } catch(Exception e) {
            Log.e(TAG, "Cannot establish connection", e);
        }
	}
	
	private StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return total;
	}

}
