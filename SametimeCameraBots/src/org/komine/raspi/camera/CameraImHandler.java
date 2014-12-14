package org.komine.raspi.camera;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.komine.sametime.stjava.bots.AbstractImHandler;
import org.komine.sametime.stjava.bots.SametimeBot;

import com.lotus.sametime.im.ImEvent;

public class CameraImHandler extends AbstractImHandler {

	private static final String MSG_WELCOME = "Welcome to the Sametime Camera Bot.  Enter a request.";

	private static final String MSG_THANKS = "Thank you for your message: ";
	private static final String MSG_INTERNALFAILURE = "Internal failure.";
	private static final String MSG_HELP = (new StringBuffer("Help:\n"))
			.append(String.format("\t%s\t- %s\n", "camera","Take a picture and send it via chat."))
			.append(String.format("\t%s\t- %s\n", "image","Send a test image via chat."))
			.append(String.format("\t%s\t- %s\n", "pizza","Send a pizza image via chat and say something."))
			.append(String.format("\t%s\t- %s\n", "bold","Send a bold message."))
			.append(String.format("\t%s\t- %s\n", "mixed","Send a mixed text message."))
			.toString();
	
	boolean isDummy = false;
	
	public CameraImHandler(SametimeBot callback, boolean isDummy) {
		super(callback);
		this.isDummy = isDummy;
	}
	
	@Override
	public void showWelcomeMessage(ImEvent event) {
		event.getIm().sendText(false, MSG_WELCOME);
	}
	
	@Override
	public void responseImText(ImEvent event, String rawMessage, String simplifiedMessage) {
		String responseMessage;
		if (simplifiedMessage.equals("help")) {
			responseMessage = MSG_HELP;
			event.getIm().sendText(true, responseMessage);
		} else if (simplifiedMessage.equals("bold")) {
			responseBold(event.getIm(), "Hello Bold !");
		} else if (simplifiedMessage.equals("mixed")) {
			sendMixed(event.getIm(), new String[] {"First piece of text", "Second piece of text"});
		} else if (simplifiedMessage.equals("image")) {
			try {
				InputStream is = getClass().getClassLoader().getResourceAsStream("org/komine/raspi/camera/images/dummy.jpg");
				sendImage(event.getIm(), is);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (simplifiedMessage.equals("camera")) {
			try {
				Camera camera = new Camera(isDummy);
				sendImage(event.getIm(), camera.takePicture());
			} catch (IOException | CameraException | InterruptedException | URISyntaxException e) {
				e.printStackTrace();
				event.getIm().sendText(true, MSG_INTERNALFAILURE);
			}
		} else if (simplifiedMessage.equals("pizza")) {
			try {
				Pizza pizza = new Pizza();
				InputStream is = pizza.getPizzaFile();
				sendImage(event.getIm(), is);
				responseBold(event.getIm(), "Nice pizza !");
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
				event.getIm().sendText(true, MSG_INTERNALFAILURE);
			}
		} else {
			responseMessage = MSG_THANKS + simplifiedMessage;
			event.getIm().sendText(true, responseMessage);
		}
	}
}
