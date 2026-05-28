package com.campus.complaint;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Sends email via Resend.com HTTP API (HTTPS port 443).
 * Works on Render free tier instantly — no SMTP, no account suspension.
 *
 * Required environment variables on Render:
 *   RESEND_API_KEY  — API key from resend.com (starts with re_)
 *   SENDER_EMAIL    — verified sender email
 */
public class EmailUtil {

    private static final String SENDER_NAME = "CampusCare System";
    private static final String API_URL     = "https://api.resend.com/emails";

    public static void sendEmail(String recipientEmail, String subject, String body) {

        String apiKey      = System.getenv("RESEND_API_KEY");
        String senderEmail = System.getenv("SENDER_EMAIL");

        if (senderEmail == null || senderEmail.isEmpty()) {
            // Resend allows this test sender without domain verification
            senderEmail = "onboarding@resend.dev";
        }

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("RESEND_API_KEY not set — skipping email to " + recipientEmail);
            return;
        }

        try {
            String jsonBody = "{"
                + "\"from\":\"" + escapeJson(SENDER_NAME) + " <" + escapeJson(senderEmail) + ">\","
                + "\"to\":[\"" + escapeJson(recipientEmail) + "\"],"
                + "\"subject\":\"" + escapeJson(subject) + "\","
                + "\"html\":\"" + escapeJson(body) + "\""
                + "}";

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
            if (code == 200 || code == 201) {
                System.out.println("Email sent via Resend to: " + recipientEmail);
            } else {
                System.out.println("Resend returned HTTP " + code + " for: " + recipientEmail);
            }
            conn.disconnect();

        } catch (Exception e) {
            System.err.println("Email failed for " + recipientEmail + ": " + e.getMessage());
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
