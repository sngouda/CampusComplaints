package com.campus.complaint;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/StudentRegisterServlet")
public class StudentRegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try (Connection conn = DBConnection.getConnection()) {
            // Check if email already exists
            String checkSql = "SELECT id FROM students WHERE email = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, email);
            if (checkPs.executeQuery().next()) {
                response.sendRedirect("student_register.html?error=Email already registered. Please login.");
                return;
            }

            String sql = "INSERT INTO students (name, email, password) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            
            int row = ps.executeUpdate();
            if (row > 0) {
                // Send welcome email in background — never blocks registration
                final String fName = name;
                final String fEmail = email;
                new Thread(() -> {
                    try {
                        String subject = "Welcome to CampusCare - Registration Successful";
                        String body = "<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto;'>"
                                + "<h2 style='color:#6366f1;'>🎓 Welcome to CampusCare</h2>"
                                + "<p>Hello <strong>" + fName + "</strong>,</p>"
                                + "<p>You have successfully registered to the Campus Complaint Management System.</p>"
                                + "<p>You can now login and lodge your complaints.</p>"
                                + "<br><p style='color:#94a3b8;font-size:0.85rem;'>— CampusCare System</p>"
                                + "</div>";
                        EmailUtil.sendEmail(fEmail, subject, body);
                    } catch (Exception emailEx) {
                        System.out.println("Welcome email failed (registration still succeeded): " + emailEx.getMessage());
                    }
                }).start();
                response.sendRedirect("student_login.html?msg=Registration Successful. Please Login.");
            } else {
                response.sendRedirect("student_register.html?error=Registration Failed. Please try again.");
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            response.sendRedirect("student_register.html?error=Email already registered. Please login.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("student_register.html?error=Server error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
