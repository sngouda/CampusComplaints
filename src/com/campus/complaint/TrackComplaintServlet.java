package com.campus.complaint;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/TrackComplaintServlet")
public class TrackComplaintServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("student_id") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        }
        
        int studentId = (int) session.getAttribute("student_id");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM complaints WHERE student_id = ? ORDER BY created_at DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            
            ResultSet rs = ps.executeQuery();
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            
            while (rs.next()) {
                if (!first) {
                    json.append(",");
                }
                json.append("{");
                json.append("\"id\":").append(rs.getInt("id")).append(",");
                json.append("\"title\":\"").append(rs.getString("title").replace("\"", "\\\"")).append("\",");
                json.append("\"description\":\"").append(rs.getString("description").replace("\"", "\\\"")).append("\",");
                json.append("\"status\":\"").append(rs.getString("status")).append("\",");
                json.append("\"priority\":\"").append(rs.getString("priority") != null ? rs.getString("priority") : "Medium").append("\",");
                json.append("\"date\":\"").append(rs.getTimestamp("created_at").toString()).append("\"");
                json.append("}");
                first = false;
            }
            json.append("]");
            
            out.print(json.toString());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"Server Error\"}");
        }
    }
}
