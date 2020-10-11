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
			Class.forName("com.mysql.cj.jdbc.Driver");
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
			st.execute("INSERT INTO passwords VALUES('" + username + "','" + h.HashPassword(pass) + "');");
			return "Success";
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
			return "Error";
		}
	}
	
	public String CompareData(String username, String pass) {
		try {
			Hash h = new Hash();
			PreparedStatement pst = conn.prepareStatement("SELECT * FROM passwords WHERE username = ?");
			pst.setString(1, username);
			rs = pst.executeQuery();
			while(rs.next()) {
				if (username.equals(rs.getString("username"))) {
					if (h.HashPassword(pass).equals(rs.getString("password"))) {
						return "OK";
					}
					else {
						return "PasswordError";
					}
				}
				else {
					return "UsernameError";
				}
			}
			return "No data found.";
		} catch (SQLException e) {
			e.printStackTrace();
			return e.toString();
		}
	}
}
