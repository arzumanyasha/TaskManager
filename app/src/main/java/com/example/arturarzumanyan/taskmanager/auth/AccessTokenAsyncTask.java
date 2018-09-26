package com.example.arturarzumanyan.taskmanager.auth;

import android.net.Uri;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.Constants;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class AccessTokenAsyncTask extends AsyncTask<String, Void, String> {

    private TokenAsyncTaskEvents mTokenAsyncTaskEvents;
    private String mBuffer;

    public AccessTokenAsyncTask(TokenAsyncTaskEvents tokenAsyncTaskEvents) {
        mTokenAsyncTaskEvents = tokenAsyncTaskEvents;
    }

    @Override
    protected String doInBackground(String... strings) {
        String authCode = strings[0];
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            TokenHttpUrlConnection tokenHttpUrlConnection = new TokenHttpUrlConnection();
            connection = tokenHttpUrlConnection.getConnectionSettings(connection);

            Uri.Builder uriBuilder = new Uri.Builder()
                    .appendQueryParameter("code", authCode)
                    .appendQueryParameter("client_id", Constants.CLIENT_ID)
                    .appendQueryParameter("client_secret", Constants.CLIENT_SECRET)
                    .appendQueryParameter("grant_type", "authorization_code");
            String query = uriBuilder.build().getEncodedQuery();

            tokenHttpUrlConnection.getConnection(connection, query);

            int responseCode=connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK){
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                mBuffer = tokenHttpUrlConnection.getInputStream(reader, connection);
                return mBuffer;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mTokenAsyncTaskEvents != null) {
            try {
                mTokenAsyncTaskEvents.onPostExecute(mBuffer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
