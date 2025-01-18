package com.example.importtomo.service;

import com.example.importtomo.dto.Shift;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;
import com.google.api.services.calendar.Calendar;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
            System.out.println("START: " + event.getStart());
            System.out.println("END: " + event.getEnd());
            Event savedEvent = service.events().insert(calendarId, event).execute();
            System.out.println("Saved Event Start Time: " + savedEvent.getStart().getDateTime());
            System.out.println("Saved Event Time Zone: " + savedEvent.getStart().getTimeZone());
            System.out.printf("Event created: %s\n", savedEvent.getHtmlLink());
        }
    }


    public List<Event> mapShiftsToEvents(List<Shift> shifts, String calendarSummary, String calendarId, String accessToken) {
        List<Event> events = new ArrayList<>();
        try {
            for (Shift shift : shifts) {
                // Convert start and end times to ZonedDateTime objects
                ZonedDateTime startTime = ZonedDateTime.parse(shift.getStartTime());
                ZonedDateTime endTime = ZonedDateTime.parse(shift.getEndTime());


                Event event = new Event().setSummary(calendarSummary);

                    // Format start time
                EventDateTime start = new EventDateTime()
                            .setDateTime(new DateTime(formatZonedDateTimeForGoogle(startTime)))
                            .setTimeZone("UTC");
                    event.setStart(start);

                    // Format end time
                EventDateTime end = new EventDateTime()
                            .setDateTime(new DateTime(formatZonedDateTimeForGoogle(endTime)))
                            .setTimeZone("UTC");
                    event.setEnd(end);
                    // Check if its duplicate and add the event to the list
                if (!doesEventExist(calendarId, event, accessToken)) {
                    events.add(event);
                }

            }

            return events;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to format ZonedDateTime in proper RFC 3339 format for Google API
    private String formatZonedDateTimeForGoogle(ZonedDateTime zonedDateTime) {
        // Use the formatter to ensure correct RFC 3339 format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        return zonedDateTime.format(formatter);
    }

    private boolean doesEventExist(String calendarId, Event newEvent, String accessToken) throws Exception {
        // Step 1: Set up transport and factory
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Step 2: Use access token to authenticate
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, null))
                .createScoped(CalendarScopes.CALENDAR);
        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName("Tomo Import")
                .build();
        // Get the start and end time of the event to check for overlap
        DateTime startTime = newEvent.getStart().getDateTime();
        DateTime endTime = newEvent.getEnd().getDateTime();

        // Get events between the start and end time
        Events events = service.events().list(calendarId)
                .setTimeMin(startTime)
                .setTimeMax(endTime)
                .execute();

        // Step 1: Check if the event with the same summary(name)
        for (Event event : events.getItems()) {
            // Check by event name
            if (event.getSummary().equals(newEvent.getSummary())) {
                // Event exists, return true
                return true;
            }
        }

        // Step 2: No matching event found, return false
        return false;
    }
}

