package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SessionServlet
 */
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
		var cookies = request.getCookies();
		String toSend = "{\"message\":\"%s\", \"username\":\"%s\", \"status\":\"%s\"}";
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		var session = request.getSession(true);
		if (session.getId().equals(cookies[0].getValue())) {
			toSend = String.format(toSend, "OK", session.getAttribute("username"), "200");
			out.write(toSend);
		}
		else {
			String.format(toSend, "ERROR", "null", "500");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
