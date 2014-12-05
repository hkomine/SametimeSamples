package org.komine.sametime.stjava.bots;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.lotus.sametime.im.Im;
import com.lotus.sametime.im.ImEvent;

public abstract class AbstractImHandler implements ImHandler {
	abstract public void showWelcomeMessage(ImEvent event);
	abstract public void responseImText(ImEvent event, String rawMessage, String simplifiedMessage);

	public void responseBold(Im imSession, String message) {
		// Send rich text acknowledgement
		sendDataMessage(imSession, "data", "richtext", new byte[] { (byte) 0xEE} );
		
		// Format your rich text message as desired
		String richTextStart = "<span style=\"font-size:8pt;font-family:Tahoma;color:#000000;font-style:normal;\" class=\"left\">";
		String richTextEnd = "</span>";
		String myRichTextMessage = "<b>" + message + "</b>";
		String messageToSend = richTextStart + myRichTextMessage + richTextEnd;
		
		imSession.sendText(true, messageToSend);
	}
	
	public void sendMixed(Im im, String[] messages) {
		// Send mixed content start indicator
		sendDataMessage(im, "data", "command", new byte[] { (byte) 0xDE} );
		// Send text
		for (int i=0; i<messages.length; i++) {
			im.sendText(false, messages[i]);
		}
		// Send mixed content stop indicator
		sendDataMessage(im, "data", "command", new byte[] { (byte) 0x82} );
	}

	public void sendImage(Im im, File image) throws IOException {
		// Send mixed content start indicator
		sendDataMessage(im, "data", "command", new byte[] { (byte) 0xDE} );
		
		// First load the image
		FileInputStream imageStream = new FileInputStream(image);
		byte[] imageBytes = new byte[imageStream.available()];
		imageStream.read(imageBytes);
		// Now send the HTML image tag and the image data
		im.sendText(false, "<img src=\"\"/>");
		sendDataMessage(im, "data", "image", imageBytes);
		// Close the image
		imageStream.close();

		// Send mixed content stop indicator
		sendDataMessage(im, "data", "command", new byte[] { (byte) 0x82} );
	}

	
	public void sendDataMessage(Im im, String messageType, String messageSubType, byte[] messageBytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(baos);
		try {
			dataStream.writeUTF(messageType);
			dataStream.writeUTF(messageSubType);
			dataStream.write(messageBytes);
		} catch (IOException e) {
			throw new AssertionError("sendDataMessage failed");
		}
		im.sendData(true, 27191, 0, baos.toByteArray());
	}
}
