package org.komine.raspi.camera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.komine.sametime.stjava.bots.SametimeBot;

import com.lotus.sametime.core.comparch.DuplicateObjectException;

public class SametimeCameraBot {

	public static void main(String[] args) {
		if (args.length < 3) {
			printUsage();
			return;
		}

		String server = args[0];
		String username = args[1];
		char[] password = args[2].toCharArray();
		
		boolean isDummy = false;
		if (args.length > 3 && 0 == args[3].compareTo("dummy")) {
			isDummy = true;
		}
		
		String sessionName = "SametimeBot";
		try {
			SametimeBot service = new SametimeBot(sessionName);
			service.setImHandler(new CameraImHandler(isDummy));
			service.start(server, username, password);
			System.out.println("Sametime Bot is Running. Press any key to terminate.");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			br.readLine();

			System.out.println("Termination request received.");
			service.stop();
			System.out.println("Sametime Bot has terminated");
		} catch (DuplicateObjectException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private static void printUsage() {
		System.out.println("Invalid parameters.");
		System.out.println("");
		System.out.println("\tUsage:");
		System.out.println("\t\t" + SametimeCameraBot.class.getSimpleName() + " [Sametime server] [username] [password]");
		System.out.println("");
	}
}
