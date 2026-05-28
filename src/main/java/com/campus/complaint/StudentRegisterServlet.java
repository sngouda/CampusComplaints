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
                // Send welcome email (non-blocking — failure won't affect registration)
                try {
                    String subject = "Welcome to Campus Complaint System";
                    String body = "<h3>Hello " + name + ",</h3>"
                                + "<p>You have successfully registered to the Campus Complaint Management System.</p>"
                                + "<p>Now you can login and lodge your complaints.</p>";
                    EmailUtil.sendEmail(email, subject, body);
                } catch (Exception emailEx) {
                    System.out.println("Welcome email failed (registration still succeeded): " + emailEx.getMessage());
                }
                response.sendRedirect("student_login.html?msg=Registration Successful. Please Login.");
            } else {
                response.sendRedirect("student_register.html?error=Registration Failed. Please try again.");
            }
        } catch (com.mysql.cj.jdbc.exceptions.MysqlDataTruncation e) {
            response.sendRedirect("student_register.html?error=Input too long. Please shorten your details.");
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            response.sendRedirect("student_register.html?error=Email already registered. Please login.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("student_register.html?error=Server error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
