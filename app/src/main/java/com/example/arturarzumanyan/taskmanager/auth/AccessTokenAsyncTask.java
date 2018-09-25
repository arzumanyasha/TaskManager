package com.example.arturarzumanyan.taskmanager.auth;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.arturarzumanyan.taskmanager.R;

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
import java.net.URLEncoder;
import java.util.Iterator;

public class AccessTokenAsyncTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        String authCode = strings[0];
        try {
            URL url = new URL("https://www.googleapis.com/oauth2/v4/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setInstanceFollowRedirects( true );
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            Uri.Builder uriBuilder = new Uri.Builder()
                    .appendQueryParameter("code", authCode)
                    .appendQueryParameter("client_id", "685238908043-obre149i2k2gh9a71g2it0emsa97glma.apps.googleusercontent.com")
                    .appendQueryParameter("client_secret", "6ygf5qYHRMx3AnIwXGbLhWuz")
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
                
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "s";
    }
}
