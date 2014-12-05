package org.komine.raspi.camera;

import java.io.File;
import java.util.Random;

public class Pizza {

	private static String[] imageFiles = {
		"pizza_01.jpg",
		"pizza_02.jpg",
		"pizza_03.jpg",
		"pizza_04.jpg",
		"pizza_05.jpg",
		"pizza_06.jpg",
		"pizza_07.jpg",
		"pizza_08.jpg",
		"pizza_09.jpg",
		"pizza_10.jpg",
	};

	public static File getPizzaFile() {
		Random rnd = new Random();
      	int index = rnd.nextInt(10);
        
        File file = new File("images\\pizza\\" + imageFiles[index]);
        if (file.exists()) {
        	return file;
        } else {
        	return null;
        }
	}
}
