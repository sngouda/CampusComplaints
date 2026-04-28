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

@WebServlet("/StudentLoginServlet")
public class StudentLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM students WHERE email = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("student_id", rs.getInt("id"));
                session.setAttribute("student_name", rs.getString("name"));
                session.setAttribute("student_email", rs.getString("email"));
                
                response.sendRedirect("student_dashboard.html");
            } else {
                response.sendRedirect("student_login.html?error=Invalid Credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("student_login.html?error=Server Error");
        }
    }
}
