package com.campus.complaint;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Sends email via SendGrid HTTP API (port 443).
 * Works on Render free tier — no SMTP port blocking.
 *
 * Requires environment variable: SENDGRID_API_KEY
 * Requires a verified sender email in SendGrid dashboard.
 */
public class EmailUtil {

    private static final String SENDER_EMAIL = "suhasgowda636227@gmail.com";
    private static final String SENDER_NAME  = "CampusCare System";
    private static final String API_URL      = "https://api.sendgrid.com/v3/mail/send";

    public static void sendEmail(String recipientEmail, String subject, String body) {
        String apiKey = System.getenv("SENDGRID_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("SENDGRID_API_KEY not set — skipping email to " + recipientEmail);
            return;
        }

        try {
            // Build JSON payload
            String jsonBody = "{"
                + "\"personalizations\":[{\"to\":[{\"email\":\"" + escapeJson(recipientEmail) + "\"}]}],"
                + "\"from\":{\"email\":\"" + escapeJson(SENDER_EMAIL) + "\",\"name\":\"" + escapeJson(SENDER_NAME) + "\"},"
                + "\"subject\":\"" + escapeJson(subject) + "\","
                + "\"content\":[{\"type\":\"text/html\",\"value\":\"" + escapeJson(body) + "\"}]"
                + "}";

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 202) {
                System.out.println("Email sent successfully to: " + recipientEmail);
            } else {
                System.out.println("SendGrid returned HTTP " + responseCode + " for: " + recipientEmail);
            }
            conn.disconnect();

        } catch (Exception e) {
            System.err.println("Email failed for " + recipientEmail + ": " + e.getMessage());
        }
    }

    // Escape special characters for JSON string
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
