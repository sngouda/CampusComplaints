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

@WebServlet("/AdminDashboardServlet")
public class AdminDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin_id") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT c.id, c.title, c.description, c.priority, c.category, c.status, c.created_at, s.name as student_name " + // ✅ NEW
                         "FROM complaints c JOIN students s ON c.student_id = s.id ORDER BY c.created_at DESC";
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) json.append(",");
                json.append("{");
                json.append("\"id\":").append(rs.getInt("id")).append(",");
                json.append("\"student_name\":\"").append(rs.getString("student_name").replace("\"", "\\\"")).append("\",");
                json.append("\"title\":\"").append(rs.getString("title").replace("\"", "\\\"")).append("\",");
                json.append("\"description\":\"").append(rs.getString("description").replace("\"", "\\\"")).append("\",");
                json.append("\"priority\":\"").append(rs.getString("priority")).append("\",");
                json.append("\"category\":\"").append(rs.getString("category")).append("\","); // ✅ NEW
                json.append("\"status\":\"").append(rs.getString("status")).append("\",");
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