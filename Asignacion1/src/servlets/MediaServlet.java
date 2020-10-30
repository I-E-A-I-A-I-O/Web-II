package servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.DBConnection;
import controllers.FileManager;
/**
 * Servlet implementation class MediaServlet
 */
@MultipartConfig
@WebServlet("/MediaServlet")
public class MediaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MediaServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, Option");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST");
		response.setHeader("Access-Control-Max-Age", "86400");
		if (request.getHeader("Option").equals("ProfilePic")) {
			FileManager file = new FileManager();
			var userPic = file.getImage((String) request.getSession().getAttribute("username"));
			if (userPic != null) {
				var out = response.getOutputStream();
				response.setContentType("image/" + (String) userPic[1]);
				byte[] buffer = new byte[1024];
				int bytesRead;
				var inputStream = (FileInputStream) userPic[0];
				while((bytesRead = inputStream.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			}
		}
		else if (request.getHeader("Option").equals("Video-List")) {
			response.setHeader("Access-Control-Allow-Origin", "*");
			FileManager manager = new FileManager();
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			String toSend = "{\"list\":\"%s\", \"status\":\"200\"}";
			toSend = String.format(toSend, manager.getVideoFiles());
			out.write(toSend);
		}
		else if (request.getHeader("Option").equals("User-Videos")) {
			DBConnection db =  new DBConnection();
			PrintWriter out = response.getWriter();
			String toSend = "{\"message\": \"%s\", \"videos\":\"%s\", \"status\":\"200\"}";
			db.Connect();
			String videoList = db.GetUserVideos((String) request.getSession().getAttribute("username"));
			db.Disconnect();
			if (videoList != null) {
				if (videoList.length() > 0) {
					toSend = String.format(toSend, "OK", videoList);
				}
				else {
					toSend = String.format(toSend, "Empty", "");
				}
			}
			else {
				toSend = String.format(toSend, "null", "null");
			}
			out.write(toSend);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, Option");
		response.setHeader("Access-Control-Expose-Headers", "Content-Length");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST");
		response.setHeader("Access-Control-Max-Age", "86400");
		if (request.getHeader("Option").equals("Get-Video")) {
			FileManager manager = new FileManager();
			var searchResults = manager.GetVideo(request.getParameter("videoName"));
			if (searchResults != null) {
				var out = response.getOutputStream();
				response.setContentType("video/" + (String) searchResults[1]);
				byte[] buffer = new byte[1024];
				int bytesRead;
				var inputStream = (FileInputStream) searchResults[0];
				response.setHeader("Content-Length", "" + (long) searchResults[2]);
				while((bytesRead = inputStream.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			}
		}
		else if (request.getHeader("Option").equals("Delete-Video")) {
			PrintWriter out = response.getWriter();
			String toSend = "{\"message\": \"%s\", \"status\":\"200\"}";
			String fileName = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
			DBConnection db = new DBConnection();
			db.Connect();
			if (db.DeleteUserVideo((String) request.getSession().getAttribute("username"), fileName)) {
				toSend = String.format(toSend, "OK");
			}
			else {
				toSend = String.format(toSend, "ERROR");
			}
			db.Disconnect();
			out.write(toSend);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, Option, Old-User");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST");
		response.setHeader("Access-Control-Max-Age", "86400");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String toSend = "{\"message\":\"%s\", \"status\":\"200\"}";
		if (request.getHeader("Option").equals("Image")) {
			var part = request.getPart("file");
			FileManager uploader = new FileManager();
			uploader.UploadImage(part, (String) request.getSession().getAttribute("username"));
			toSend = String.format(toSend, "OK");
			out.write(toSend);
		}
		else if (request.getHeader("Option").equals("Video")) {
			var part = request.getPart("file");
			FileManager uploader = new FileManager();
			uploader.UploadVideo(part, (String) request.getSession().getAttribute("username"));
			toSend = String.format(toSend, "OK");
			out.write(toSend);
		}
		else if (request.getHeader("Option").equals("Rename-folder")) {
			FileManager manager = new FileManager();
			manager.RenameFolder((String) request.getHeader("Old-User"), (String) request.getSession().getAttribute("username"));
		}
		else if (request.getHeader("Option").equals("Delete-folder")){
			FileManager manager = new FileManager();
			manager.DeleteFolder((String) request.getSession().getAttribute("username"));
		}
	}

}
