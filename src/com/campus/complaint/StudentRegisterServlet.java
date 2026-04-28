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
            String sql = "INSERT INTO students (name, email, password) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password); // Note: In a real app, hash the password!
            
            int row = ps.executeUpdate();
            if (row > 0) {
                // Registration successful, send welcome email
                String subject = "Welcome to Campus Complaint System";
                String body = "<h3>Hello " + name + ",</h3>"
                            + "<p>You have successfully registered to the Campus Complaint Management System.</p>"
                            + "<p>Now you can login and lodge your complaints.</p>";
                try {
                    EmailUtil.sendEmail(email, subject, body);
                } catch(Exception e) {
                    System.out.println("Email notification failed: " + e.getMessage());
                }
                response.sendRedirect("student_login.html?msg=Registration Successful. Please Login.");
            } else {
                response.sendRedirect("student_register.html?error=Registration Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("student_register.html?error=An error occurred during registration");
        }
    }
}
