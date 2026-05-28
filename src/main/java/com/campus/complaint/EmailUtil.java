package com.campus.complaint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmailUtil {

    private static final String API_URL = "https://api.resend.com/emails";

    public static void sendEmail(String recipientEmail, String subject, String body) {

        String apiKey = System.getenv("RESEND_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("[EMAIL] RESEND_API_KEY not set — skipping");
            return;
        }
        apiKey = apiKey.trim();

        // Use RESEND_TO_EMAIL override if set (Resend free plan restriction)
        String toEmail = System.getenv("RESEND_TO_EMAIL");
        if (toEmail == null || toEmail.trim().isEmpty()) {
            toEmail = recipientEmail;
        }
        toEmail = toEmail.trim();

        System.out.println("[EMAIL] Sending to: " + toEmail + " | Subject: " + subject);

        try {
            // Build JSON manually with safe escaping
            // Use simple plain text to avoid HTML escaping issues
            String safeSubject = subject.replace("\\", "").replace("\"", "'");
            String safeHtml    = body.replace("\\", "").replace("\"", "'");
            String safeFrom    = "CampusCare <onboarding@resend.dev>";
            String safeTo      = toEmail.replace("\"", "");

            String jsonBody = "{\"from\":\"" + safeFrom + "\","
                            + "\"to\":[\"" + safeTo + "\"],"
                            + "\"subject\":\"" + safeSubject + "\","
                            + "\"html\":\"" + safeHtml + "\"}";

            byte[] postData = jsonBody.getBytes(StandardCharsets.UTF_8);

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData);
                os.flush();
            }

            int code = conn.getResponseCode();
            System.out.println("[EMAIL] HTTP response: " + code);

            // Read response
            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(
                    code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8));
            } catch (Exception e) {
                System.out.println("[EMAIL] Could not read response: " + e.getMessage());
                conn.disconnect();
                return;
            }

            StringBuilder resp = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) resp.append(line);
            reader.close();
            System.out.println("[EMAIL] Response: " + resp);

            if (code == 200 || code == 201) {
                System.out.println("[EMAIL] SUCCESS - sent to: " + toEmail);
            } else {
                System.out.println("[EMAIL] FAILED - HTTP " + code);
            }

            conn.disconnect();

        } catch (Exception e) {
            System.err.println("[EMAIL] Exception: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
