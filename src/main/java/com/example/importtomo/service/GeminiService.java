package com.example.importtomo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class GeminiService {
    private final RestTemplate restTemplate;
    private final String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
    }

    public String processImage(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Read the image file and encode it in Base64
        byte[] imageBytes = file.getBytes();
        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

        // Create the request body
        String requestBody = String.format(
                "{" +
                        "\"contents\": [{" +
                        "\"parts\": [" +
                        "{\"text\": \"Please extract the date, start time, and end time for each shift from the provided work schedule screenshot. Format the output into a JSON object as follows: \\n\\n" +
                        "- Use the ISO 8601 format (e.g., 2025-01-20T10:00:00-07:00) for start and end times, including the timezone offset.\\n" +
                        "- Provide the date, start time, end time, and month for each shift.\\n" +
                        "- If the image is not a schedule or calendar, return 0.\\n\\n" +
                        "The JSON structure should be: \\n\\n" +
                        "{\\n" +
                        "  \\\"shifts\\\": [\\n" +
                        "    {\\n" +
                        "      \\\"date\\\": \\\"YYYY-MM-DD\\\",\\n" +
                        "      \\\"start_time\\\": \\\"YYYY-MM-DDTHH:MM:SS-00:00\\\",\\n" +
                        "      \\\"end_time\\\": \\\"YYYY-MM-DDTHH:MM:SS-00:00\\\",\\n" +
                        "      \\\"month\\\": M\\n" +
                        "    },\\n" +
                        "    ...\\n" +
                        "  ]\\n" +
                        "}\\n\\n" +
                        "Ensure that the JSON object contains only the list of shifts with their date, start time, end time, and month.\"}," +
                        "{\"inline_data\": {" +
                        "\"mime_type\": \"image/png\"," +
                        "\"data\": \"%s\"" +
                        "}}" +
                        "]" +
                        "}]" +
                        "}",
                encodedImage
        );

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String urlWithKey = geminiApiUrl + "?key=" + apiKey;

        ResponseEntity<String> response = restTemplate.postForEntity(urlWithKey, requestEntity, String.class);

        // Initialize ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string
        JsonNode rootNode = objectMapper.readTree(response.getBody());

        // Navigate to the `text` attribute
        String text = rootNode
                .path("candidates") // Navigate to "candidates"
                .get(0)             // Access the first candidate object
                .path("content")    // Navigate to "content"
                .path("parts")      // Navigate to "parts"
                .get(0)             // Access the first part
                .path("text")       // Get the "text" field
                .asText();          // Extract its value as a string

        // Print the extracted text
        System.out.println("Extracted Text: " + text);
        return text;
    }
}
