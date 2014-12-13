package org.komine.raspi.camera;

import java.io.InputStream;
import java.net.URISyntaxException;
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
	
	public InputStream getPizzaFile() throws URISyntaxException {
		Random rnd = new Random();
      	int index = rnd.nextInt(10);

      	InputStream is = getClass().getClassLoader().getResourceAsStream("org/komine/raspi/camera/images/pizza/" + imageFiles[index]);
      	return is;
	}
}
