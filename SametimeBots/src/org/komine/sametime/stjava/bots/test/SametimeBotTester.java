package org.komine.sametime.stjava.bots.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.komine.sametime.stjava.bots.AbstractImHandler;
import org.komine.sametime.stjava.bots.AbstractMyselfHandler;
import org.komine.sametime.stjava.bots.SametimeBot;

import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.im.ImEvent;
import com.lotus.sametime.places.MyselfEvent;
import com.lotus.sametime.places.MyselfInPlace;

public class SametimeBotTester {

	public static void main(String[] args) {
		if (args.length < 3) {
			printUsage();
			return;
		}

		String server = args[0];
		String username = args[1];
		char[] password = args[2].toCharArray();
		
		String sessionName = "SametimeBot";
		try {
			SametimeBot service = new SametimeBot(sessionName);
			service.setImHandler(new AbstractImHandler(service) {

				@Override
				public void showWelcomeMessage(ImEvent event) {
					event.getIm().sendText(false, "Welcome to SametimeBotTester !");
				}

				@Override
				public void responseImText(ImEvent event, String rawMessage, String simplifiedMessage) {
					event.getIm().sendText(true, "Received: " + simplifiedMessage);
				}
			});
			service.setMyselfHandler(new AbstractMyselfHandler(service) {
				
				@Override
				public void responseImText(MyselfInPlace myselfPlace, MyselfEvent event,
						String receivedMessage) {
					myselfPlace.getPlace().sendText("Received in place: " + receivedMessage);
				}
			});
			
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
		System.out.println("\t\t" + SametimeBotTester.class.getSimpleName() + " [Sametime server] [username] [password]");
		System.out.println("");
	}
}
