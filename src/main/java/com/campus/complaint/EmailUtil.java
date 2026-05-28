package com.campus.complaint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmailUtil {

    private static final String SENDER_NAME = "CampusCare System";
    private static final String API_URL     = "https://api.resend.com/emails";

    public static void sendEmail(String recipientEmail, String subject, String body) {

        String apiKey = System.getenv("RESEND_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("[EMAIL] RESEND_API_KEY not set — skipping");
            return;
        }

        // Resend free plan: only sends to your own verified email
        // Set RESEND_TO_EMAIL in Render env to override recipient
        String toEmail = System.getenv("RESEND_TO_EMAIL");
        if (toEmail == null || toEmail.isEmpty()) {
            toEmail = recipientEmail;
        }

        System.out.println("[EMAIL] Attempting to send to: " + toEmail + " | Subject: " + subject);

        try {
            String jsonBody = "{"
                + "\"from\":\"CampusCare System <onboarding@resend.dev>\","
                + "\"to\":[\"" + escapeJson(toEmail) + "\"],"
                + "\"subject\":\"" + escapeJson(subject) + "\","
                + "\"html\":\"" + escapeJson(body) + "\""
                + "}";

            System.out.println("[EMAIL] Payload: " + jsonBody.substring(0, Math.min(200, jsonBody.length())));

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            System.out.println("[EMAIL] Resend HTTP response code: " + code);

            // Read response body for debugging
            BufferedReader reader;
            if (code >= 200 && code < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            reader.close();
            System.out.println("[EMAIL] Resend response body: " + responseBody.toString());

            if (code == 200 || code == 201) {
                System.out.println("[EMAIL] SUCCESS - sent to: " + toEmail);
            } else {
                System.out.println("[EMAIL] FAILED - HTTP " + code);
            }

            conn.disconnect();

        } catch (Exception e) {
            System.err.println("[EMAIL] Exception: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
