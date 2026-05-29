package com.campus.complaint;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TestDBServlet")
public class TestDBServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("DB_URL: " + System.getenv("DB_URL"));
        out.println("DB_USER: " + System.getenv("DB_USER"));
        out.println("DB_PASS: " + (System.getenv("DB_PASS") != null ? "SET" : "NOT SET"));
        out.println("Testing...");
        out.flush();
        try {
            Connection conn = DBConnection.getConnection();
            out.println("SUCCESS - Connected to: " + conn.getCatalog());
            conn.close();
        } catch (Exception e) {
            out.println("FAILED: " + e.getMessage());
            if (e.getCause() != null) out.println("Cause: " + e.getCause().getMessage());
        }
    }
}
