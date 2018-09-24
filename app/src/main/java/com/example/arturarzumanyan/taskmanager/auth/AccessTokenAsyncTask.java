package com.example.arturarzumanyan.taskmanager.auth;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.arturarzumanyan.taskmanager.R;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
                    .appendQueryParameter("redirect_uri","https://accounts.google.com/o/oauth2/v2/auth")
                    .appendQueryParameter("client_id", String.valueOf(R.string.server_client_id))
                    .appendQueryParameter("client_secret", String.valueOf(R.string.client_secret))
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
        }
        return "s";
    }
}
