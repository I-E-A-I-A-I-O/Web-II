package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.DBConnection;

/**
 * Servlet implementation class MyServlet
 */
@MultipartConfig()
@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegistrationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "https://guarded-escarpment-77007.herokuapp.com");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST");
		response.setHeader("Access-Control-Max-Age", "86400");
		response.setHeader("Set-Cookie", "SameSite=None;Secure");
		response.setContentType("application/json");
		DBConnection db = new DBConnection();
		db.Connect();
		PrintWriter out = response.getWriter();
		String toSend = "{\"message\":\"%s\", \"status\":200}";
		toSend = String.format(toSend, db.InsertUser(request.getParameter("userName"), request.getParameter("password")));
		db.Disconnect();
		out.write(toSend);
	}

}
