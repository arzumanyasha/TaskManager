package com.example.arturarzumanyan.taskmanager.networking.base;

import android.net.Uri;

import com.example.arturarzumanyan.taskmanager.auth.AccessTokenAsyncTask;
import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.networking.util.Converter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseHttpUrlConnection {
    public static final String JSON_CONTENT_TYPE_VALUE = "application/json";
    private Boolean isJson;

    public String getResult(String url,
                            FirebaseWebService.RequestMethods requestMethod,
                            HashMap<String, Object> requestBodyParameters,
                            HashMap<String, String> requestHeaderParameters) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        Uri.Builder uriBuilder;
        isJson = false;

        String query = "";
        try {
            connection = getConnectionSettings(connection, url, requestMethod, requestHeaderParameters);
            uriBuilder = new Uri.Builder();
            if ((requestMethod == FirebaseWebService.RequestMethods.POST) && !isJson) {
                for (HashMap.Entry<String, Object> map : requestBodyParameters.entrySet()) {
                    uriBuilder.appendQueryParameter(map.getKey(), map.getValue().toString());
                }

                query = uriBuilder.build().getEncodedQuery();
            } else if (((requestMethod == FirebaseWebService.RequestMethods.POST) ||
                    (requestMethod == FirebaseWebService.RequestMethods.PATCH)) && isJson) {

                JSONObject jsonObject = Converter.fromMapToJson(requestBodyParameters);

                query = jsonObject.toString();
            }

            setConnection(connection, query, requestMethod);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String buffer;
                buffer = getInputStream(reader);
                return buffer;
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                return "";
            } else if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                return "ok";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod(requestMethod.toString());
        if ((requestMethod == FirebaseWebService.RequestMethods.POST) ||
                (requestMethod == FirebaseWebService.RequestMethods.PATCH)) {
            connection.setDoOutput(true);
        }
        connection.setDoInput(true);
        for (HashMap.Entry<String, String> map : requestHeaderParameters.entrySet()) {
            connection.setRequestProperty(map.getKey(), map.getValue());
            if (map.getValue().equals(JSON_CONTENT_TYPE_VALUE)) {
                isJson = true;
            }
        }

        return connection;
    }

    private void setConnection(HttpURLConnection connection,
                               String query,
                               FirebaseWebService.RequestMethods requestMethod) throws IOException {
        if ((requestMethod == FirebaseWebService.RequestMethods.POST) ||
                (requestMethod == FirebaseWebService.RequestMethods.PATCH)) {
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
        }
        connection.connect();
    }

    private String getInputStream(BufferedReader reader) throws IOException {
        StringBuilder buf = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            buf.append(line + "\n");
        }
        return buf.toString();
    }
}
