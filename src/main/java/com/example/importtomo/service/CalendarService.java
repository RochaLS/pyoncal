package com.example.importtomo.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Event;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;
import com.google.api.services.calendar.Calendar;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CalendarService {
    public List<Map<String, String>> getUserCalendars(AccessToken accessToken) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        // Create GoogleCredentials using the access token
        GoogleCredentials googleCredentials = GoogleCredentials.create(accessToken)
                .createScoped(CalendarScopes.CALENDAR);

        // Set up the Calendar API service
        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(googleCredentials))
                .setApplicationName("Tomo Import")
                .build();

        System.out.println("GETTING CALENDAR!!");
        CalendarList calendarList = service.calendarList().list().execute();

        // Convert calendar list into the desired format
        List<Map<String, String>> calendars = new ArrayList<>();
        calendarList.getItems().forEach(calendar -> {
            System.out.println(calendar.getSummary());
            calendars.add(Map.of(
                    "id", calendar.getId(),
                    "name", calendar.getSummary()
            ));
        });

        System.out.println(calendars.get(0));
        return calendars;
    }

    public void saveEventsToCalendar(String calendarId, List<Event> events, String accessToken) throws GeneralSecurityException, IOException {
        // Step 1: Set up transport and factory
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Step 2: Use access token to authenticate
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, null))
                .createScoped(CalendarScopes.CALENDAR);
        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName("Tomo Import")
                .build();

        // Save each event
        for (Event event: events) {
            Event savedEvent = service.events().insert(calendarId, event).execute();
            System.out.printf("Event created: %s\n", savedEvent.getHtmlLink());
        }
    }
}

