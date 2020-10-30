package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Part;

public class FileManager {
	
	public void UploadVideo(Part part, String username) {
		try {
			var inputStream = part.getInputStream();
			var file = createVideoFile(username, getFileName(part));
			if (file != null) {
				var fileOutput = new FileOutputStream(file);
				int read = 0;
				final byte[] bytes = new byte[1024];
				try {
					while((read = inputStream.read(bytes)) != -1) {
						fileOutput.write(bytes, 0, read);
					}
					DBConnection db = new DBConnection();
					db.Connect();
					db.InsertUserVideo(username, file.getAbsolutePath());
					db.Disconnect();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void DeleteVideo(String username, String fileName) {
		File file = new File("WebContent/Assets/UserFiles/" + username + "/Videos/" + fileName);
		try {
			file.delete();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void UploadImage(Part part, String username) {
		File file = checkImageFolder(getFileName(part), username);
		try {
			var inputStream = part.getInputStream();
			if (file != null) {
				var fileOutput = new FileOutputStream(file);
				int read = 0;
				final byte[] bytes = new byte[1024];
				try {
					while((read = inputStream.read(bytes)) != -1) {
						fileOutput.write(bytes, 0, read);
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private File checkImageFolder(String fileName, String username) {
		File userImageFolder = new File("WebContent/Assets/UserFiles/" + username + "/Images");
		if (userImageFolder.mkdirs()) {
			File newImage = new File("WebContent/Assets/UserFiles/" + username + "/Images/" + fileName);
			return newImage;
		}
		else {
			String[] imageFolderContent = userImageFolder.list();
			if (imageFolderContent.length > 0) {
				File image = new File("WebContent/Assets/UserFiles/" + username + "/Images/" + imageFolderContent[0]);
				image.delete();
				File newImage = new File("WebContent/Assets/UserFiles/" + username + "/Images/" + fileName);
				return newImage;
			}
			else {
				File newImage = new File("WebContent/Assets/UserFiles/" + username + "/Images/" + fileName);
				return newImage;
			}
		}
	}
	
	private File createVideoFile(String username, String filename) {
		File userFolders = new File("WebContent/Assets/UserFiles/" + username + "/Videos");
		userFolders.mkdirs();
		File file = new File("WebContent/Assets/UserFiles/" + username + "/Videos/" + filename);
		if (file.exists()) {
			return null;
		}
		else {
			try {
				file.createNewFile();
				return file;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	private static String getFileName(Part part) {
		for (String cd : part.getHeader("Content-Disposition").split(";")){
			if (cd.trim().startsWith("filename")) {
				String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);
			}
		}
		return null;
	}
	
	protected String getFileNameFromPath(String path) {
		return path.split("/")[path.split("/").length - 1];
	}
	
	public String getVideoFiles() {
		String files = "";
		var usersDir = new File ("WebContent/Assets/UserFiles");
		var Userscontents = usersDir.list();
		for (int i = 0; i < Userscontents.length; i++) {
			var userDir = new File ("WebContent/Assets/UserFiles/" + Userscontents[i] + "/Videos");
			var userContent = userDir.list();
			if (userContent.length < 1) continue;
			for (int n = 0; n < userContent.length; n++) {
				files += userContent[n] + ":" + Userscontents[i] + ";";
			}
		}
		return files;
	}
	
	public Object[] getImage(String username) {
		File userImageFolder = new File("WebContent/Assets/UserFiles/" + username + "/Images");
		Object[] data = new Object[2];
		if (userImageFolder.exists()) {
			String[] imageFolderContentList = userImageFolder.list();
			if (imageFolderContentList.length < 1) {
				return null;
			}
			else {
				FileInputStream userImage;
				try {
					userImage = new FileInputStream("WebContent/Assets/UserFiles/" + username + "/Images/" + imageFolderContentList[0]);
					data[0] = userImage;
					data[1] = imageFolderContentList[0].split("\\.")[1];
					return data;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		else {
			return null;
		}
	}
	
	public void RenameFolder(String oldUsername, String newUsername) {
		File userFolder = new File("WebContent/Assets/UserFiles/" + oldUsername);
		File newUserFolder = new File("WebContent/Assets/UserFiles/" + newUsername);
		userFolder.renameTo(newUserFolder);
	}
	
	public void DeleteFolder(String username) {
		File userFolder = new File("WebContent/Assets/UserFiles/" + username);
		if (userFolder.exists()) {
			DeleteDirContent(userFolder);
		}
	}
	
	public void DeleteDirContent(File dir) {
		if (!IsDirEmpty(dir)) {
			String[] dirContent = dir.list();
			List<File> dirContentObjects = new ArrayList<File>();
			for(int i = 0; i < dirContent.length; i++) {
				File temp = new File(dir.getPath() + "/" + dirContent[i]);
				dirContentObjects.add(temp);
			}
			for(File file : dirContentObjects) {
				if (file.isDirectory()) {
					if(IsDirEmpty(file)) {
						file.delete();
					}
					else {
						DeleteDirContent(file);
					}
				}
				else {
					file.delete();
				}
			}
			dir.delete();
		}
		else {
			dir.delete();
		}
	}
	
	public Boolean IsDirEmpty(File dir) {
		if (dir.list().length > 0) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public Object[] GetVideo(String videoSearchString) {
		videoSearchString = videoSearchString.replace(" ", "");
		String videoName = videoSearchString.split("by")[0];
		String videoOwner = videoSearchString.split("by")[1];
		Object[] objects = new Object[3];
		try {
			File videoFile = new File("WebContent/Assets/UserFiles/" + videoOwner + "/Videos/" + videoName);
			FileInputStream videoFileStream = new FileInputStream(videoFile);
			objects[0] = videoFileStream;
			objects[1] = videoFile.getName().split("\\.")[1];
			objects[2] = videoFile.length();
			return objects;
		}catch(FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
