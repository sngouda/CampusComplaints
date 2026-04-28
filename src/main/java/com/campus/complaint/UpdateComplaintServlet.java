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
                // If status is Resolved, send email to student
                if ("Resolved".equalsIgnoreCase(newStatus)) {
                    // Fetch student email and name
                    String fetchSql = "SELECT s.email, s.name, c.title FROM complaints c JOIN students s ON c.student_id = s.id WHERE c.id = ?";
                    PreparedStatement fetchPs = conn.prepareStatement(fetchSql);
                    fetchPs.setInt(1, complaintId);
                    ResultSet rs = fetchPs.executeQuery();
                    if (rs.next()) {
                        String email = rs.getString("email");
                        String name = rs.getString("name");
                        String title = rs.getString("title");
                        
                        String subject = "Complaint Resolved: Campus Complaint System";
                        String body = "<h3>Hello " + name + ",</h3>"
                                    + "<p>Great news! Your complaint regarding <b>" + title + "</b> has been <b>RESOLVED</b>.</p>"
                                    + "<p>Thank you for helping us maintain the campus.</p>";
                        try {
                            EmailUtil.sendEmail(email, subject, body);
                        } catch(Exception e) {
                            System.out.println("Status updated, but email notification failed: " + e.getMessage());
                        }
                    }
                }
                response.getWriter().write("Success");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Failed to update status");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Server Error");
        }
    }
}
