package com.example.arturarzumanyan.taskmanager.networking.base;

import android.net.Uri;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.networking.util.Converter;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class BaseHttpUrlConnection {
    public static final String JSON_CONTENT_TYPE_VALUE = "application/json";
    private static final String NO_CONTENT_KEY = "no content";
    private Boolean isJson;

    public ResponseDto getResult(String url,
                                 FirebaseWebService.RequestMethods requestMethod,
                                 Map<String, Object> requestBodyParameters,
                                 Map<String, String> requestHeaderParameters) {
        HttpURLConnection connection = null;

        isJson = false;

        String query;
        try {
            connection = getConnectionSettings(url, requestMethod, requestHeaderParameters);

            query = setRequestsDataTypeSettings(requestMethod, requestBodyParameters);

            setConnection(connection, query, requestMethod);

            return getResponseDto(connection);
        } catch (IOException e) {
            Log.v(e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
    }

    private String setRequestsDataTypeSettings(FirebaseWebService.RequestMethods requestMethod,
                                             Map<String, Object> requestBodyParameters ){
        if ((requestMethod == FirebaseWebService.RequestMethods.POST) && !isJson) {
            Uri.Builder uriBuilder = new Uri.Builder();
            for (Map.Entry<String, Object> map : requestBodyParameters.entrySet()) {
                uriBuilder.appendQueryParameter(map.getKey(), map.getValue().toString());
            }
            return uriBuilder.build().getEncodedQuery();
        } else if (((requestMethod == FirebaseWebService.RequestMethods.POST) ||
                (requestMethod == FirebaseWebService.RequestMethods.PATCH)) && isJson) {

            JSONObject jsonObject = Converter.fromMapToJson(requestBodyParameters);

            return jsonObject.toString();
        }

        return null;
    }

    private ResponseDto getResponseDto(HttpURLConnection connection)
            throws IOException {

        int responseCode = connection.getResponseCode();
        Log.v("NETWORKING RESPONSE CODE " + responseCode);
        switch (responseCode) {
            case HttpURLConnection.HTTP_OK: {
                String buffer = "";
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    buffer = getInputStream(reader);
                } catch (IOException e) {
                    Log.e(e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }

                Log.v("NETWORKING RESPONSE OK BUFFER " + buffer);
                return new ResponseDto(HttpURLConnection.HTTP_OK, buffer);
            }
            case HttpURLConnection.HTTP_UNAUTHORIZED: {
                return new ResponseDto(HttpURLConnection.HTTP_UNAUTHORIZED, "");
            }
            case HttpURLConnection.HTTP_BAD_REQUEST: {
                return new ResponseDto(HttpURLConnection.HTTP_BAD_REQUEST, "");
            }
            case HttpURLConnection.HTTP_NO_CONTENT: {
                return new ResponseDto(HttpURLConnection.HTTP_NO_CONTENT, NO_CONTENT_KEY);
            }
        }

        return null;
    }

    private HttpURLConnection getConnectionSettings(String url,
                                                    FirebaseWebService.RequestMethods requestMethod,
                                                    Map<String, String> requestHeaderParameters) throws IOException {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setReadTimeout(15000);
        connection.setConnectTimeout(15000);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod(requestMethod.toString());
        if ((requestMethod == FirebaseWebService.RequestMethods.POST) ||
                (requestMethod == FirebaseWebService.RequestMethods.PATCH)) {
            connection.setDoOutput(true);
        }
        connection.setDoInput(true);
        for (Map.Entry<String, String> map : requestHeaderParameters.entrySet()) {
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
        String line;
        while ((line = reader.readLine()) != null) {
            buf.append(line).append("\n");
        }
        return buf.toString();
    }
}
