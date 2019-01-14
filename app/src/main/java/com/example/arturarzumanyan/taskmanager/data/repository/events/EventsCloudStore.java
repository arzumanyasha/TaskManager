package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.networking.NetworkUtil;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.HashMap;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.BASE_GOOGLE_APIS_URL;

public class EventsCloudStore {
    private static final String BASE_EVENTS_URL = "calendar/v3/calendars/";

    private RepositoryLoadHelper mRepositoryLoadHelper;
    private FirebaseWebService mFirebaseWebService;
    private Context mContext;

    public EventsCloudStore(Context context) {
        mContext = context;
        mFirebaseWebService = new FirebaseWebService(context);
        mRepositoryLoadHelper = new RepositoryLoadHelper(context);
    }

    public ResponseDto getEventsFromServer(EventsSpecification eventsSpecification) {
        String eventsUrl;
        if (eventsSpecification.getStartDate().isEmpty() && eventsSpecification.getEndDate().isEmpty()) {
            eventsUrl = BASE_GOOGLE_APIS_URL + BASE_EVENTS_URL + mFirebaseWebService.getCurrentUser().getEmail() + "/events";
        } else {
            eventsUrl = BASE_EVENTS_URL + mFirebaseWebService.getCurrentUser().getEmail() + "/events?" +
                    "timeMax=" + DateUtils.decodeDate(eventsSpecification.getEndDate()) +
                    "&timeMin=" + DateUtils.decodeDate(eventsSpecification.getStartDate());
        }

        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.GET;
        RequestParameters requestParameters = new RequestParameters(mContext,
                eventsUrl,
                requestMethod,
                new HashMap<String, Object>()
        );
        requestParameters.setRequestHeaderParameters(new HashMap<String, String>());

        return NetworkUtil.getResultFromServer(requestParameters);
    }

    public ResponseDto addEventOnServer(Event event) {
        final String url = BASE_GOOGLE_APIS_URL + BASE_EVENTS_URL +
                mFirebaseWebService.getCurrentUser().getEmail() +
                "/events";
        RequestParameters requestParameters = mRepositoryLoadHelper.getEventCreateOrUpdateParameters(event, url, POST);

        return NetworkUtil.getResultFromServer(requestParameters);
    }

    public ResponseDto updateEventOnServer(Event event) {
        final String url = BASE_GOOGLE_APIS_URL + BASE_EVENTS_URL +
                mFirebaseWebService.getCurrentUser().getEmail() +
                "/events/" +
                event.getId();

        RequestParameters requestParameters = mRepositoryLoadHelper
                .getEventCreateOrUpdateParameters(event, url, PATCH);

        return NetworkUtil.getResultFromServer(requestParameters);
    }

    public ResponseDto deleteEventOnServer(Event event) {
        final String url = BASE_GOOGLE_APIS_URL + BASE_EVENTS_URL +
                mFirebaseWebService.getCurrentUser().getEmail() +
                "/events/" +
                event.getId();

        RequestParameters requestParameters = mRepositoryLoadHelper.getDeleteParameters(url);

        return NetworkUtil.getResultFromServer(requestParameters);
    }
}
