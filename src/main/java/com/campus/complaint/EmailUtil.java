package com.campus.complaint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Sends email via Brevo (Sendinblue) HTTP API.
 * Free tier: 300 emails/day, sends to ANY email, no domain needed.
 *
 * Required environment variables on Render:
 *   BREVO_API_KEY  — API key from app.brevo.com
 *   SENDER_EMAIL   — your verified sender email
 */
public class EmailUtil {

    private static final String API_URL     = "https://api.brevo.com/v3/smtp/email";
    private static final String SENDER_NAME = "CampusCare System";

    public static void sendEmail(String recipientEmail, String subject, String body) {

        String apiKey      = System.getenv("BREVO_API_KEY");
        String senderEmail = System.getenv("SENDER_EMAIL");

        if (senderEmail == null || senderEmail.trim().isEmpty()) {
            senderEmail = "suhasgowda636227@gmail.com";
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.out.println("[EMAIL] BREVO_API_KEY not set — skipping email to " + recipientEmail);
            return;
        }

        apiKey      = apiKey.trim();
        senderEmail = senderEmail.trim();

        System.out.println("[EMAIL] Sending to: " + recipientEmail + " | Subject: " + subject);

        try {
            String safeSubject   = subject.replace("\\", "").replace("\"", "'");
            String safeBody      = body.replace("\\", "").replace("\"", "'");
            String safeSender    = senderEmail.replace("\"", "");
            String safeRecipient = recipientEmail.replace("\"", "");

            String jsonBody = "{"
                + "\"sender\":{\"name\":\"" + SENDER_NAME + "\",\"email\":\"" + safeSender + "\"},"
                + "\"to\":[{\"email\":\"" + safeRecipient + "\"}],"
                + "\"subject\":\"" + safeSubject + "\","
                + "\"htmlContent\":\"" + safeBody + "\""
                + "}";

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("api-key", apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int code = conn.getResponseCode();
            System.out.println("[EMAIL] HTTP response: " + code);

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

            if (code == 201) {
                System.out.println("[EMAIL] SUCCESS - sent to: " + recipientEmail);
            } else {
                System.out.println("[EMAIL] FAILED - HTTP " + code);
            }

            conn.disconnect();

        } catch (Exception e) {
            System.err.println("[EMAIL] Exception: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
