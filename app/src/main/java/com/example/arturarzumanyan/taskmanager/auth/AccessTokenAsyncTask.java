package com.example.arturarzumanyan.taskmanager.auth;

import android.net.Uri;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AccessTokenAsyncTask extends AsyncTask<String, Void, String> {

    private TokenAsyncTaskEvents mTokenAsyncTaskEvents;
    private String mBuffer;

    public AccessTokenAsyncTask(TokenAsyncTaskEvents tokenAsyncTaskEvents) {
        mTokenAsyncTaskEvents = tokenAsyncTaskEvents;
    }

    @Override
    protected String doInBackground(String... strings) {
        String authCode = strings[0];
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(Constants.BASE_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setInstanceFollowRedirects( true );
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            Uri.Builder uriBuilder = new Uri.Builder()
                    .appendQueryParameter("code", authCode)
                    .appendQueryParameter("client_id", Constants.CLIENT_ID)
                    .appendQueryParameter("client_secret", Constants.CLIENT_SECRET)
                    .appendQueryParameter("grant_type", "authorization_code");
            String query = uriBuilder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK){
                reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder buf=new StringBuilder();
                String line=null;
                while ((line=reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
                mBuffer = buf.toString();
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
            if (conn != null) {
                conn.disconnect();
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
