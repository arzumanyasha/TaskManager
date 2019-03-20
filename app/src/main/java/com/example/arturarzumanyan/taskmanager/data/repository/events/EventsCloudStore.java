package com.example.arturarzumanyan.taskmanager.data.repository.events;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.GoogleSuiteApiFactory;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.AUTHORIZATION_KEY;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.CONTENT_TYPE_KEY;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.JSON_CONTENT_TYPE_VALUE;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.TOKEN_TYPE;

public class EventsCloudStore {
    private static final String BASE_EVENTS_URL = "calendar/v3/calendars/";

    private GoogleCalendarApi mGoogleCalendarApi;
    private RepositoryLoadHelper mRepositoryLoadHelper;

    EventsCloudStore() {
        mGoogleCalendarApi = GoogleSuiteApiFactory.getRetrofitInstance().create(GoogleCalendarApi.class);
        mRepositoryLoadHelper = new RepositoryLoadHelper();
    }

    public Single<ResponseBody> getEventsFromServer(EventsSpecification eventsSpecification) {
        String eventsUrl;
        if (eventsSpecification.getStartDate().isEmpty() && eventsSpecification.getEndDate().isEmpty()) {
            eventsUrl = BASE_EVENTS_URL +
                    FirebaseWebService.getFirebaseWebServiceInstance().getUserEmail() + "/events";
        } else {
            eventsUrl = BASE_EVENTS_URL +
                    FirebaseWebService.getFirebaseWebServiceInstance().getUserEmail() + "/events?" +
                    "timeMax=" + DateUtils.decodeDate(eventsSpecification.getEndDate()) +
                    "&timeMin=" + DateUtils.decodeDate(eventsSpecification.getStartDate());
        }

        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());
        return mGoogleCalendarApi.getEvents(eventsUrl, requestHeaderParameters);
    }

    public Single<ResponseBody> addEventOnServer(Event event) {
        String url = BASE_EVENTS_URL +
                FirebaseWebService.getFirebaseWebServiceInstance().getUserEmail() +
                "/events";
        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());
        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);

        Map<String, Object> requestBodyParameters = mRepositoryLoadHelper.getEventBodyParameters(event);

        return mGoogleCalendarApi.addEvent(url, requestHeaderParameters, requestBodyParameters);
    }

    public Single<ResponseBody> updateEventOnServer(Event event) {
        String url = BASE_EVENTS_URL +
                FirebaseWebService.getFirebaseWebServiceInstance().getUserEmail() +
                "/events/" +
                event.getEventId();

        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());
        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);

        Map<String, Object> requestBodyParameters = mRepositoryLoadHelper.getEventBodyParameters(event);

        return mGoogleCalendarApi.updateEvent(url, requestHeaderParameters, requestBodyParameters);
    }

    public Single<Response<ResponseBody>> deleteEventOnServer(Event event) {
        String url = BASE_EVENTS_URL +
                FirebaseWebService.getFirebaseWebServiceInstance().getUserEmail() +
                "/events/" +
                event.getEventId();

        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());

        return mGoogleCalendarApi.deleteEvent(url, requestHeaderParameters);
    }
}
