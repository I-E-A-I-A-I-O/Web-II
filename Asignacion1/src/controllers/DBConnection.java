package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import helpers.PropertiesReader;

public class DBConnection {
	
	private Connection conn = null;
	private Statement st = null;
	private ResultSet rs = null;
	
	public void Connect() {
		PropertiesReader pReader = new PropertiesReader();
		var prop = pReader.GetProperties();
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(prop.get(0), prop.get(1), prop.get(2));
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void Disconnect() {
		if (conn == null) return;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String InsertUser(String username, String pass) {
		try {
			Hash h = new Hash();
			st = conn.createStatement();
			st.execute("INSERT INTO users_table VALUES('" + username + "','" + h.HashPassword(pass) + "')");
			return "Success";
		} catch (SQLException | NullPointerException e) {
			return "Error";
		}
	}
	
	public void DeleteUserFiles(String username) {
		try {
			st = conn.createStatement();
			st.execute("DELETE FROM user_files WHERE usernames='" + username + "'");
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void InsertUserVideo(String username, String filePath) {
		try {
			st = conn.createStatement();
			st.execute("INSERT INTO user_files VALUES('" + username + "', '" + filePath + "')");
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Boolean DeleteUserVideo(String username, String fileName) {
		try {
			st = conn.createStatement();
			st.execute("DELETE FROM user_files WHERE filepath LIKE '%" + fileName + "%' and usernames='" + username + "'");
			FileManager manager = new FileManager();
			manager.DeleteVideo(username, fileName);
			return true;
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String GetUserVideos(String username) {
		try {
			String result = "";
			FileManager manager = new FileManager();
			st = conn.createStatement();
			rs = st.executeQuery("SELECT filepath FROM user_files WHERE usernames='" + username + "'");
			while (rs.next()) {
				result += manager.getFileNameFromPath(rs.getString("filepath")) + ";";
			}
			return result;
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String CompareData(String username, String pass) {
		try {
			Hash h = new Hash();
			PreparedStatement pst = conn.prepareStatement("SELECT * FROM users_table WHERE usernames = ?");
			pst.setString(1, username);
			rs = pst.executeQuery();
			while(rs.next()) {
				if (username.equals(rs.getString("usernames"))) {
					if (h.HashPassword(pass).equals(rs.getString("passwords"))) {
						return "OK";
					}
					else {
						return "PasswordError";
					}
				}
			}
			return "UsernameError";
		} catch (SQLException e) {
			return e.toString();
		}
	}
	
	public String ChangePassword(String username, String currentPass, String newPass) {
		try {
			var result = CompareData(username, currentPass);
			
			if (result.equals("OK")) {
				PreparedStatement pst = conn.prepareStatement("UPDATE users_table SET passwords = ? WHERE usernames = ?");
				pst.setString(1, newPass);
				pst.setString(2, username);
				pst.executeUpdate();
				return "Ok-Pass-Change";
			}
			else if(result.equals("PasswordError")){
				return "Incorrect password.";
			}
			else if(result.equals("UsernameError")) {
				return "Incorrect username.";
			}
			else {
				return result;
			}
		}catch(SQLException e) {
			return e.getMessage();
		}
	}
	
	public String ChangeUsername(String username, String newUsername, String password) {
		try {
			var result = CompareData(username, password);
			if (result.equals("OK")) {
				PreparedStatement pst = conn.prepareStatement("UPDATE users_table SET usernames = ? WHERE usernames = ?");
				pst.setString(1, newUsername);
				pst.setString(2, username);
				pst.executeUpdate();
				return "Ok-Username-Change";
			}
			else if (result.equals("PasswordError")) {
				return "Incorrect password.";
			}
			else if (result.equals("UsernameError")) {
				return "Incorrect username.";
			}
			else {
				return result;
			}
		}catch(SQLException e) {
			return e.getMessage();
		}
	}
	
	public String DeleteProfile(String username) {
		try {
			DeleteUserFiles(username);
			PreparedStatement pst = conn.prepareStatement("DELETE FROM users_table WHERE usernames = ?");
			pst.setString(1, username);
			pst.executeUpdate();
			return "Ok-Delete";
		}catch(SQLException e) {
			return e.getMessage();
		}
	}
}
