package com.campus.complaint;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UpdateComplaintServlet")
public class UpdateComplaintServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin_id") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return;
        }

        int complaintId = Integer.parseInt(request.getParameter("id"));
        String newStatus = request.getParameter("status");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE complaints SET status = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, complaintId);

            int row = ps.executeUpdate();
            if (row > 0) {
                // Send email in background thread — never blocks the HTTP response
                if ("Resolved".equalsIgnoreCase(newStatus)) {
                    String fetchSql = "SELECT s.email, s.name, c.title FROM complaints c "
                                    + "JOIN students s ON c.student_id = s.id WHERE c.id = ?";
                    PreparedStatement fetchPs = conn.prepareStatement(fetchSql);
                    fetchPs.setInt(1, complaintId);
                    ResultSet rs = fetchPs.executeQuery();
                    if (rs.next()) {
                        final String email = rs.getString("email");
                        final String name  = rs.getString("name");
                        final String title = rs.getString("title");

                        // Fire-and-forget email thread
                        new Thread(() -> {
                            try {
                                String subject = "Your Complaint Has Been Resolved - CampusCare";
                                String body = "<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto;'>"
                                        + "<h2 style='color:#10b981;'>✅ Complaint Resolved</h2>"
                                        + "<p>Hello <strong>" + name + "</strong>,</p>"
                                        + "<p>Your complaint titled <strong>\"" + title + "\"</strong> has been <strong style='color:#10b981;'>RESOLVED</strong> by the administration.</p>"
                                        + "<p>Thank you for helping us maintain a better campus.</p>"
                                        + "<br><p style='color:#94a3b8;font-size:0.85rem;'>— CampusCare System</p>"
                                        + "</div>";
                                EmailUtil.sendEmail(email, subject, body);
                            } catch (Exception e) {
                                System.out.println("Email failed (status still updated): " + e.getMessage());
                            }
                        }).start();
                    }
                }

                // Respond immediately — don't wait for email
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":true,\"message\":\"Status updated to " + newStatus + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\":false,\"message\":\"No record updated\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false,\"message\":\"Server error: " + e.getMessage() + "\"}");
        }
    }
}
