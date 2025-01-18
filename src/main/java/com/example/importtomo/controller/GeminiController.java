package com.example.importtomo.controller;

import com.example.importtomo.dto.Shift;
import com.example.importtomo.service.CalendarService;
import com.example.importtomo.service.GeminiService;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZonedDateTime;
import java.util.List;

@Controller
public class GeminiController {

    private final GeminiService geminiService;
    private final CalendarService calendarService;

    public GeminiController(GeminiService geminiService, CalendarService calendarService) {
        this.geminiService = geminiService;
        this.calendarService = calendarService;
    }

    @PostMapping("/process-image")
    public String processImage(@RequestParam("file") MultipartFile file, @RequestParam("access_token") String accessToken, @RequestParam("calendars") String selectedCalendarId, @RequestParam("event-title") String eventTitle, Model model) {
        try {
            // Sends image to gemini
            List<Shift> shifts = geminiService.processImage(file);

            if (shifts != null && !shifts.isEmpty()) {
                // Maps shifts to Google calendar events, then save events to google calendar.
                // Finally pass event count to success page.
                List<Event> events = calendarService.mapShiftsToEvents(shifts, eventTitle, selectedCalendarId, accessToken);
                calendarService.saveEventsToCalendar(selectedCalendarId, events, accessToken);
                model.addAttribute("eventCount", events.size());
                System.out.println("EVENT COUNT " + events.size());
                return "success";
            }

            return "error";

        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }


}
