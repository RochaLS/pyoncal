package com.example.importtomo.controller;

import com.example.importtomo.service.CalendarService;
import com.example.importtomo.service.GeminiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class GeminiController {

    private final GeminiService geminiService;
    private final CalendarService calendarService;

    public GeminiController(GeminiService geminiService, CalendarService calendarService) {
        this.geminiService = geminiService;
        this.calendarService = calendarService;
    }

    @PostMapping("/process-image")
    public String processImage(@RequestParam("file") MultipartFile file, @RequestParam("access_token") String accessToken, @RequestParam("calendars") String selectedCalendarId) {
        try {
            String response = geminiService.processImage(file);

            if (response != null && !response.contains("error") && !response.isEmpty()) {
               //TODO: Map events to Google event class and then create access token and save events to calendar
                return "success";
            }

            return "error";

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
