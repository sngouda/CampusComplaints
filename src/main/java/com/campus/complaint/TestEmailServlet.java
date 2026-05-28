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

        String apiKey  = System.getenv("RESEND_API_KEY");
        String toEmail = System.getenv("RESEND_TO_EMAIL");

        out.println("=== Email Config Test ===");
        out.println("RESEND_API_KEY: " + (apiKey != null && !apiKey.isEmpty() ? "SET (length=" + apiKey.trim().length() + ")" : "NOT SET"));
        out.println("RESEND_TO_EMAIL: " + (toEmail != null ? toEmail : "NOT SET"));
        out.println("");

        if (apiKey == null || apiKey.isEmpty()) {
            out.println("ERROR: RESEND_API_KEY is not set in Render environment!");
            return;
        }

        String cleanKey = apiKey.trim();
        String recipient = (toEmail != null && !toEmail.isEmpty()) ? toEmail.trim() : "suhasgowda636227@gmail.com";

        out.println("Sending to: " + recipient);
        out.flush();

        try {
            String jsonBody = "{\"from\":\"CampusCare <onboarding@resend.dev>\","
                            + "\"to\":[\"" + recipient + "\"],"
                            + "\"subject\":\"CampusCare Test Email\","
                            + "\"html\":\"<h2>Test</h2><p>Email is working!</p>\"}";

            out.println("JSON: " + jsonBody);
            out.flush();

            URL url = new URL("https://api.resend.com/emails");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + cleanKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            byte[] data = jsonBody.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data);
            }

            int code = conn.getResponseCode();
            out.println("HTTP Code: " + code);

            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(
                    code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8));
            } catch (Exception e) {
                out.println("Could not read response stream: " + e.getMessage());
                return;
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            out.println("Response: " + sb.toString());
            conn.disconnect();

            if (code == 200 || code == 201) {
                out.println("");
                out.println("SUCCESS! Check your Gmail inbox (and spam folder).");
            } else {
                out.println("FAILED - see response above.");
            }

        } catch (Exception e) {
            out.println("EXCEPTION: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
