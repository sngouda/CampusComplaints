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

        try {
            EmailUtil.sendEmail(
                toEmail != null ? toEmail : "suhasgowda636227@gmail.com",
                "CampusCare Test Email",
                "<h2>Test Email</h2><p>If you receive this, email is working correctly!</p>"
            );
            out.println("Email method completed - check Render logs for [EMAIL] output");
        } catch (Exception e) {
            out.println("Exception: " + e.getMessage());
        }
    }
}
