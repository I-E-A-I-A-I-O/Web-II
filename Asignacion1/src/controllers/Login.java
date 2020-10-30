package controllers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Login {
	public String DoLogin(HttpServletRequest request) {
		DBConnection db = new DBConnection();
		db.Connect();
		String result = db.CompareData(request.getParameter("userName"), request.getParameter("password"));
		String toSend = "{\"message\":\"%s\",\"redirectLocation\":\"%s\", \"status\":\"%s\"}";
		if (result.equals("OK")) {
			toSend = String.format(toSend, result, "welcome", "200");
			HttpSession session = request.getSession();
			session.setAttribute("username", request.getParameter("userName"));
		}
		else {
			toSend = String.format(toSend, result, "null", "500");
		}
		db.Disconnect();
		return toSend;
	}
}
