package com.example.arturarzumanyan.taskmanager.networking;

import android.net.Uri;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class BaseHttpUrlConnection {

    public String getResult(String url,
                                FirebaseWebService.RequestMethods requestMethod,
                                HashMap<String, String> requestBodyParameters,
                                HashMap<String, String> requestHeaderParameters){
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        Uri.Builder uriBuilder;
        String query = "";
        try {
            connection = getConnectionSettings(connection, url, requestMethod, requestHeaderParameters);
            uriBuilder = new Uri.Builder();
            for(HashMap.Entry<String, String> map : requestBodyParameters.entrySet()){
                uriBuilder.appendQueryParameter(map.getKey(), map.getValue());
            }

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

    private HttpURLConnection getConnectionSettings(HttpURLConnection connection,
                                                    String url,
                                                    FirebaseWebService.RequestMethods requestMethod,
                                                    HashMap<String, String> requestHeaderParameters) throws IOException {
        URL requestUrl = new URL(url);
        connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setReadTimeout(15000);
        connection.setConnectTimeout(15000);
        connection.setInstanceFollowRedirects( true );
        connection.setRequestMethod(requestMethod.toString());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        for(HashMap.Entry<String, String> map : requestHeaderParameters.entrySet()){
            connection.setRequestProperty(map.getKey(), map.getValue());
        }

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
