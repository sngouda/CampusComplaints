package com.campus.complaint;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Test endpoint to verify email is working.
 * Access: https://campuscomplaints.onrender.com/TestEmailServlet
 * Remove this servlet after testing.
 */
@WebServlet("/TestEmailServlet")
public class TestEmailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String apiKey   = System.getenv("RESEND_API_KEY");
        String toEmail  = System.getenv("RESEND_TO_EMAIL");

        out.println("=== Email Config Test ===");
        out.println("RESEND_API_KEY set: " + (apiKey != null && !apiKey.isEmpty() ? "YES (length=" + apiKey.length() + ")" : "NO - NOT SET"));
        out.println("RESEND_TO_EMAIL: " + (toEmail != null ? toEmail : "NOT SET - will use recipient email"));
        out.println("");
        out.println("Sending test email now...");
        out.flush();

        // Capture all System.out during email send
        try {
            String apiKey2   = System.getenv("RESEND_API_KEY");
            String toEmail2  = System.getenv("RESEND_TO_EMAIL");
            if (toEmail2 == null || toEmail2.isEmpty()) {
                toEmail2 = "suhasgowda636227@gmail.com";
            }

            String jsonBody = "{"
                + "\"from\":\"CampusCare System <onboarding@resend.dev>\","
                + "\"to\":[\"" + toEmail2 + "\"],"
                + "\"subject\":\"CampusCare Test\","
                + "\"html\":\"<h2>Test</h2><p>Email working!</p>\""
                + "}";

            out.println("Sending to: " + toEmail2);
            out.println("API Key prefix: " + (apiKey2 != null ? apiKey2.substring(0, 10) + "..." : "NULL"));
            out.flush();

            java.net.URL url = new java.net.URL("https://api.resend.com/emails");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey2);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            out.println("HTTP Response Code: " + code);

            java.io.BufferedReader reader;
            if (code >= 200 && code < 300) {
                reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
            } else {
                reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line2;
            while ((line2 = reader.readLine()) != null) sb.append(line2);
            reader.close();

            out.println("Response Body: " + sb.toString());
            conn.disconnect();

            if (code == 200 || code == 201) {
                out.println("SUCCESS - Check your Gmail inbox and spam folder!");
            } else {
                out.println("FAILED - See response body above for reason");
            }

        } catch (Exception e) {
            out.println("EXCEPTION: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
