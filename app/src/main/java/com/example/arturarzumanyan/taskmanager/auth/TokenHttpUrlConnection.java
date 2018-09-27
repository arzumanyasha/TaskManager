package com.example.arturarzumanyan.taskmanager.auth;

import android.net.Uri;

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
import java.net.URL;

public class TokenHttpUrlConnection {

    public String getAccessToken(String authCredential, String authCodeKey, String grantType){
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        Uri.Builder uriBuilder;
        String query = "";
        try {
            connection = getConnectionSettings(connection);
            uriBuilder = new Uri.Builder()
                    .appendQueryParameter(authCodeKey, authCredential)
                    .appendQueryParameter("client_id", Constants.CLIENT_ID)
                    .appendQueryParameter("client_secret", Constants.CLIENT_SECRET)
                    .appendQueryParameter("grant_type", grantType);
            query = uriBuilder.build().getEncodedQuery();

            setConnection(connection, query);

            int responseCode=connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK){
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String buffer;
                buffer = getInputStream(reader);
                return buffer;
            }
        } catch (IOException e) {
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

    private HttpURLConnection getConnectionSettings(HttpURLConnection connection) throws IOException {
        URL url = new URL(Constants.BASE_URL);
        connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(15000);
        connection.setConnectTimeout(15000);
        connection.setInstanceFollowRedirects( true );
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        return connection;
    }

    private void setConnection(HttpURLConnection connection, String query) throws IOException {
        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(query);
        writer.flush();
        writer.close();
        os.close();

        connection.connect();
    }

    private String getInputStream(BufferedReader reader) throws IOException {
        StringBuilder buf=new StringBuilder();
        String line=null;
        while ((line=reader.readLine()) != null) {
            buf.append(line + "\n");
        }
        return buf.toString();
    }
}
