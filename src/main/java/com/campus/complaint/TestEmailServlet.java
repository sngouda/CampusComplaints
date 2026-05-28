package com.campus.complaint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TestEmailServlet")
public class TestEmailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String brevoKey    = System.getenv("BREVO_API_KEY");
        String senderEmail = System.getenv("SENDER_EMAIL");
        String resendKey   = System.getenv("RESEND_API_KEY");

        out.println("=== Email Config ===");
        out.println("BREVO_API_KEY:  " + (brevoKey   != null ? "SET (len=" + brevoKey.trim().length()   + ")" : "NOT SET"));
        out.println("RESEND_API_KEY: " + (resendKey  != null ? "SET (len=" + resendKey.trim().length()  + ")" : "NOT SET"));
        out.println("SENDER_EMAIL:   " + (senderEmail != null ? senderEmail : "NOT SET"));
        out.println("");

        if (brevoKey == null || brevoKey.trim().isEmpty()) {
            out.println("ERROR: BREVO_API_KEY not set in Render environment!");
            return;
        }

        String cleanKey = brevoKey.trim();
        String sender   = (senderEmail != null && !senderEmail.trim().isEmpty())
                          ? senderEmail.trim() : "suhasgowda636227@gmail.com";
        String recipient = "suhasgowda636227@gmail.com";

        out.println("Sending test email...");
        out.println("From: " + sender);
        out.println("To:   " + recipient);
        out.flush();

        try {
            String jsonBody = "{"
                + "\"sender\":{\"name\":\"CampusCare\",\"email\":\"" + sender + "\"},"
                + "\"to\":[{\"email\":\"" + recipient + "\"}],"
                + "\"subject\":\"CampusCare Test Email\","
                + "\"htmlContent\":\"<h2>Test</h2><p>Brevo email is working!</p>\""
                + "}";

            out.println("JSON: " + jsonBody);
            out.flush();

            URL url = new URL("https://api.brevo.com/v3/smtp/email");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("api-key", cleanKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            out.println("HTTP Code: " + code);

            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(
                    code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8));
            } catch (Exception e) {
                out.println("Cannot read response: " + e.getMessage());
                return;
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            out.println("Response: " + sb.toString());
            conn.disconnect();

            if (code == 201) {
                out.println("");
                out.println("SUCCESS! Check Gmail inbox and spam folder.");
            } else {
                out.println("FAILED - see response above.");
            }

        } catch (Exception e) {
            out.println("EXCEPTION: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
