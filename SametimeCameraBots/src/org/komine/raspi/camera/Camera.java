package org.komine.raspi.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera {
	private boolean isDummy;
	
	public Camera(boolean isDummy) {
		this.isDummy = isDummy;
		if(this.isDummy) {
			System.out.println("Camera runs in dummy mode.");
		}
	}

	public InputStream takePicture() throws IOException, CameraException, InterruptedException, URISyntaxException {
		InputStream is;
		if (!isDummy) {
			File file = generateFile("images", "jpg");
			String output = file.getAbsolutePath();
			execCommand("raspistill -w 300 -h 200 -o " + output);
			is = new FileInputStream(checkFile(file));
		} else {
			is = getClass().getClassLoader().getResourceAsStream("org/komine/raspi/camera/images/dummy.jpg");
		}
		return is;
	}

	public InputStream takeVideo(long timeout) throws IOException, CameraException, InterruptedException, URISyntaxException {
		InputStream is;
		if (!isDummy) {
			File file = generateFile("images", "h264");
			String output = file.getAbsolutePath();
			execCommand("raspivid -o " + output + " -t " + timeout);
			is = new FileInputStream(checkFile(file));
		} else {
			is = getClass().getClassLoader().getResourceAsStream("org/komine/raspi/camera/images/dummy.h264");
		}
		return is;
	}

	private void execCommand(String command) throws IOException, InterruptedException {
		println("Executing : "+ command);
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(command);
		pr.waitFor();
	}
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
	
	private File generateFile(String subFolder, String extension) throws IOException, CameraException {
		 File currentFolder = new File(".");
		 File imageFolder = new File(currentFolder.getCanonicalPath() + File.separatorChar + subFolder);
		 if (!imageFolder.exists()) {
			 imageFolder.mkdirs();
		 } else if (!imageFolder.isDirectory()) {
			 throw new CameraException("Exisitng " + subFolder + " file is not directory.");
		 }
		 
		 if (imageFolder.exists()) {
			 String filename = dateFormat.format(new Date());
			 File newFile = new File(imageFolder.getAbsolutePath() + File.separatorChar + filename + "." + extension);
			 println(newFile.getAbsolutePath());
			 return newFile;
		 } else {
			 throw new CameraException("File name generation error.");
		 }
	}
	
	private File checkFile(File file) throws CameraException {
		if (file.exists() && !file.isDirectory()) {
			println("New file has been generated as " + file.getAbsolutePath() + " with size " + file.length());
			return file;
		} else if (!file.exists()) {
			throw new CameraException("File has NOT been generated.");
		} else if (file.isDirectory()) {
			throw new CameraException("File is directory.");
		} else {
			throw new CameraException("File check failed with unkown reason.");
		}
	}
	
	private void println(Object o) {
		if (o instanceof String) {
			System.out.println((String) o);
		} else if (o instanceof Throwable) {
			((Throwable) o).printStackTrace();
		} else {
			System.out.println(o.toString());
		}
	}
}
