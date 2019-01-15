package com.example.arturarzumanyan.taskmanager.data.repository;

import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;

import java.net.HttpURLConnection;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.GET;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;

public abstract class BaseDataLoadingAsyncTask<T> extends AsyncTask<FirebaseWebService.RequestMethods, Void, List<T>> {
    public BaseDataLoadingAsyncTask() {

    }

    @Override
    protected List<T> doInBackground(FirebaseWebService.RequestMethods... requestMethods) {
        if (RepositoryLoadHelper.isOnline()) {
            ResponseDto responseDto = getResponseFromServer(requestMethods[0]);

            int responseCode;
            if (responseDto != null) {
                responseCode = responseDto.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    retryGetResultFromServer(requestMethods[0]);
                } else {
                    if (responseCode == HttpURLConnection.HTTP_OK ||
                            responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                        if (requestMethods[0] == POST) {
                            doInsertQuery(responseDto);
                        } else if (requestMethods[0] == PATCH) {
                            doUpdateQuery();
                        } else if (requestMethods[0] == GET) {
                            refreshDbQuery(responseDto);
                        } else {
                            doDeleteQuery();
                        }
                    }
                }
            } else {
                onServerError();
            }
        } else {
            if (requestMethods[0] == POST) {
                doInsertQuery(null);
            } else if (requestMethods[0] == PATCH) {
                doUpdateQuery();
            } else if (requestMethods[0] == GET) {
                return doSelectQuery();
            } else {
                doDeleteQuery();
            }
        }

        return doSelectQuery();
    }

    private ResponseDto getResponseFromServer(FirebaseWebService.RequestMethods requestMethod) {
        switch (requestMethod) {
            case GET: {
                return doGetRequest();
            }
            case POST: {
                return doPostRequest();
            }
            case PATCH: {
                return doPatchRequest();
            }
            case DELETE: {
                return doDeleteRequest();
            }
            default: {
                return null;
            }
        }
    }

    @Override
    protected void onPostExecute(List<T> data) {
        super.onPostExecute(data);
        userDataLoadingListener.onSuccess(data);
    }

    protected abstract ResponseDto doGetRequest();

    protected abstract ResponseDto doPostRequest();

    protected abstract ResponseDto doPatchRequest();

    protected abstract ResponseDto doDeleteRequest();

    protected abstract List<T> doSelectQuery();

    protected abstract void refreshDbQuery(ResponseDto responseDto);

    protected abstract void doInsertQuery(ResponseDto responseDto);

    protected abstract void doUpdateQuery();

    protected abstract void doDeleteQuery();

    protected abstract void retryGetResultFromServer(FirebaseWebService.RequestMethods requestMethod);

    protected abstract void onServerError();

    public interface UserDataLoadingListener<T> {
        void onSuccess(List<T> list);

        void onFail(String message);
    }

    public void setDataInfoLoadingListener(UserDataLoadingListener<T> listener) {
        this.userDataLoadingListener = listener;
    }

    private UserDataLoadingListener<T> userDataLoadingListener;
}
