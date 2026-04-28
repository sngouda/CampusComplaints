package com.campus.complaint;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AddComplaintServlet")
public class AddComplaintServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("student_id") == null) {
            response.sendRedirect("student_login.html?error=Please Login First");
            return;
        }

        int studentId = (int) session.getAttribute("student_id");
        String studentName = (String) session.getAttribute("student_name");
        String studentEmail = (String) session.getAttribute("student_email");

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String priority = request.getParameter("priority");
        String category = request.getParameter("category"); // ✅ NEW

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO complaints (student_id, title, description, priority, category, status) VALUES (?, ?, ?, ?, ?, 'Pending')"; // ✅ NEW
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, priority);
            ps.setString(5, category); // ✅ NEW

            int row = ps.executeUpdate();
            if (row > 0) {
                String subject = "Complaint Registered Successfully";
                String body = "<h3>Hello " + studentName + ",</h3>"
                            + "<p>Your complaint regarding <b>" + title + "</b> has been successfully registered.</p>"
                            + "<p>You can track its status from your dashboard.</p>";
                try {
                    EmailUtil.sendEmail(studentEmail, subject, body);
                } catch (Exception e) {
                    System.out.println("Email notification failed: " + e.getMessage());
                }
                response.sendRedirect("student_dashboard.html?msg=Complaint Added Successfully");
            } else {
                response.sendRedirect("student_dashboard.html?error=Failed to Add Complaint");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("student_dashboard.html?error=Server Error");
        }
    }
}