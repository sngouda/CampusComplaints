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

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("admin_id", rs.getInt("id"));
                session.setAttribute("admin_username", rs.getString("username"));
                
                response.sendRedirect("admin_dashboard.html");
            } else {
                response.sendRedirect("admin_login.html?error=Invalid Credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("admin_login.html?error=Server Error");
        }
    }
}
