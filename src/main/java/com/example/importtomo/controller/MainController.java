package com.example.importtomo.controller;

import com.example.importtomo.service.CalendarService;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.oauth2.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    private final CalendarService calendarService;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    public MainController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/")
    public String home() {
        return "connect";
    }

    @GetMapping("/dashboard")
    public String loggedIn(Authentication authentication, Model model) throws GeneralSecurityException, IOException {
        if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
            model.addAttribute("name", oidcUser.getGivenName());

            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    "google", authentication.getName());

            if (authorizedClient != null) {
                String accessTokenString = authorizedClient.getAccessToken().getTokenValue();
                model.addAttribute("accessToken", accessTokenString);
                Date expirationTime = Date.from(authorizedClient.getAccessToken().getExpiresAt());

                // Create AccessToken with expiration
                AccessToken accessToken = new AccessToken(accessTokenString, expirationTime);

                // Pass the token to CalendarService
                List<Map<String, String>> calendars = calendarService.getUserCalendars(accessToken);

                model.addAttribute("calendars", calendars);

                Event event = new Event()
                        .setSummary("My shift")
                        .setDescription("");

                String startDateTime = "2025-01-20T10:00:00-07:00";  // January 20, 2025, at 10 AM in PST
                String endDateTime = "2025-01-20T22:00:00-07:00";    // January 20, 2025, at 11 AM in PST
                String timeZone = "America/Los_Angeles";             // Time zone: Los Angeles (PST)

                // Hardcoded Event DateTime setup
                EventDateTime start = new EventDateTime()
                        .setDateTime(new DateTime(startDateTime))
                        .setTimeZone(timeZone);

                EventDateTime end = new EventDateTime()
                        .setDateTime(new DateTime(endDateTime))
                        .setTimeZone(timeZone);

                event.setStart(start);
                event.setEnd(end);

                List<Event> events = new ArrayList<>();
                events.add(event);

//                calendarService.saveEventsToCalendar(calendars.get(0).get("id"), events, accessTokenString);
            }
        }

        return "dashboard";
    }

    @GetMapping("/login-error")
    public String loginError() {
        return "loginError"; // You can create a loginError.html template to show an error message.
    }


    @GetMapping("/connect-google")
    public String connectGoogle() {
        // Logic for Google OAuth2 connection
        return "redirect:/google-auth";
    }

    @GetMapping("/user")
    public String getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        // Retrieve user details
        String userName = oidcUser.getFullName(); // User's full name
        String userEmail = oidcUser.getEmail();   // User's email address

        System.out.println(userEmail);
        return "User Name: " + userName + ", Email: " + userEmail;
    }

}

