package servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import controllers.DBConnection;
import controllers.Hash;
import controllers.FileManager;
/**
 * Servlet implementation class SessionServlet
 */
@MultipartConfig
@WebServlet("/SessionServlet")
public class SessionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SessionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE");
		response.setHeader("Access-Control-Max-Age", "86400");
		response.setContentType("application/json");
		var cookies = request.getCookies();
		String toSend = "{\"message\":\"%s\", \"username\":\"%s\", \"status\":\"%s\"}";
		PrintWriter out = response.getWriter();
		var session = request.getSession(true);
		if (cookies.length > 1) {
			if (session.getId().equals(cookies[1].getValue())) {
				if (session.getAttributeNames().hasMoreElements()) {
					toSend = String.format(toSend, "OK", session.getAttribute("username"), "200");
				}
				else {
					toSend = String.format(toSend, "ERROR", "null", "500");
				}
			}
			else {
				toSend = String.format(toSend, "ERROR", "null", "500");
			}
		}
		else {
			toSend = String.format(toSend, "ERROR", "null", "500");
		}
		out.write(toSend);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, Option");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE");
		response.setHeader("Access-Control-Max-Age", "86400");
		response.setContentType("application/json");
		if (request.getHeader("Option").equals("logout")) {
			if (request.getReader().lines().collect(Collectors.joining(System.lineSeparator())).equals("logout")) {
				String toSend = "{\"message\":\"Ok-Redirect\", \"status\":\"200\"}";
				var cookies = request.getCookies();
				cookies[0].setMaxAge(0);
				PrintWriter out = response.getWriter();
				out.write(toSend);
			}
		}
		else if(request.getHeader("Option").equals("newPass")) {
			String toSend = "{\"message\":\"%s\", \"status\":\"200\"}";
			DBConnection db = new DBConnection();
			db.Connect();
			Hash h = new Hash();
			toSend = String.format(toSend, db.ChangePassword(request.getParameter("CurrentUsername"), request.getParameter("CurrentPassword"), h.HashPassword(request.getParameter("NewPassword"))));
			PrintWriter out = response.getWriter();
			db.Disconnect();
			out.write(toSend);
		}
		else if(request.getHeader("Option").equals("newUser")) {
			String toSend = "{\"message\":\"%s\", \"status\":\"200\"}";
			Hash h = new Hash();
			DBConnection db = new DBConnection();
			db.Connect();
			var result = db.ChangeUsername((String) request.getSession().getAttribute("username"), request.getParameter("newUsername"), request.getParameter("Password"));
			toSend = String.format(toSend, result);
			if (result.contains("Ok-Username-Change")) {
				request.getSession().setAttribute("username", request.getParameter("newUsername"));
			}
			toSend = String.format(toSend, result);
			PrintWriter out = response.getWriter();
			db.Disconnect();
			out.write(toSend);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE");
		response.setHeader("Access-Control-Max-Age", "86400");
		response.setContentType("application/json");
		DBConnection db = new DBConnection();
		var session = request.getSession(false);
		db.Connect();
		var result = db.DeleteProfile((String) session.getAttribute("username"));
		String toSend = "{\"message\":\"%s\", \"status\":\"200\"}";
		toSend = String.format(toSend, result);
		PrintWriter out = response.getWriter();
		db.Disconnect();
		session.invalidate();
		out.write(toSend);
	}
}
