package org.komine.sametime.stjava.bots;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lotus.sametime.community.CommunityService;
import com.lotus.sametime.community.LoginEvent;
import com.lotus.sametime.community.LoginListener;
import com.lotus.sametime.community.ServiceEvent;
import com.lotus.sametime.community.ServiceListener;
import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.constants.ImTypes;
import com.lotus.sametime.core.constants.STError;
import com.lotus.sametime.im.ImEvent;
import com.lotus.sametime.im.ImListener;
import com.lotus.sametime.im.ImServiceListener;
import com.lotus.sametime.im.InstantMessagingService;

public class SametimeBot implements ServiceListener, LoginListener, ImServiceListener, ImListener {
	private STSession session;
	private CommunityService service;
	private InstantMessagingService imService;
	
	private ImHandler imHandler;
	
	public void setImHandler(ImHandler imHandler) {
		this.imHandler = imHandler;
	}

	public SametimeBot(String sessionName) throws DuplicateObjectException {
		session= new STSession(sessionName); 
		session.loadSemanticComponents();
		session.start();
	}
	
	public void start(String server, String username, char[] password) {
		service = (CommunityService) session.getCompApi(CommunityService.COMP_NAME);
		service.addServiceListener(this);
		login(server, username, password);
	}

	public void stop() {
    	service.logout();
	}

	private void login(String server, String username, char[] password) {
		service.addLoginListener(this);
		service.loginByPassword(server, username, password);
	}

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.community.LoginListener#loggedIn(com.lotus.sametime.community.LoginEvent)
	 */
	@Override
	public void loggedIn(LoginEvent event) {
		imService = (InstantMessagingService) session.getCompApi(InstantMessagingService.COMP_NAME);
		imService.registerImType(ImTypes.IM_TYPE_CHAT);
		imService.addImServiceListener(this);
		
	    printConsole("Logged In.");
	}

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.community.LoginListener#loggedOut(com.lotus.sametime.community.LoginEvent)
	 */
	@Override
	public void loggedOut(LoginEvent event) {
	    int reason = event.getReason();
	    if (reason == 0)
	    	printConsole("Successfully logged out.");
	    else 
	    	printConsole("Failed to login.  Return Code = " + reason + ", message = " + STError.getMessageString(reason));
	    session.stop();
	    session.unloadSession();
	}

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.community.ServiceListener#serviceAvailable(com.lotus.sametime.community.ServiceEvent)
	 */
	@Override
	public void serviceAvailable(ServiceEvent arg0) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.im.ImServiceListener#imReceived(com.lotus.sametime.im.ImEvent)
	 */
	@Override
	public void imReceived(ImEvent event) {
		printConsole("IM received.");
		
		debugImEvent(event);

		event.getIm().addImListener(this);
		if (null != imHandler) {
			imHandler.showWelcomeMessage(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * com.lotus.sametime.im.ImListener
	 */
	@Override
	public void dataReceived(ImEvent event) {
		// Do nothing.
		printConsole("Data received.");
		
		debugImEvent(event);
	}

	/*
	 * (non-Javadoc)
	 * com.lotus.sametime.im.ImListener
	 */
	@Override
	public void imClosed(ImEvent event) {
		printConsole("IM Closed.");
		event.getIm().removeImListener(this);
	}

	/*
	 * (non-Javadoc)
	 * com.lotus.sametime.im.ImListener
	 */
	@Override
	public void imOpened(ImEvent event) {
		// Do nothing.
		printConsole("IM opened.");
	}

	/*
	 * (non-Javadoc)
	 * com.lotus.sametime.im.ImListener
	 */
	@Override
	public void openImFailed(ImEvent event) {
		printConsole("IM open failed.");
	}

	/*
	 * (non-Javadoc)
	 * com.lotus.sametime.im.ImListener
	 */
	@Override
	public void textReceived(ImEvent event) {
		String rawMessage = event.getText();
		printConsole("Text received. raw message = " + rawMessage);
		
		debugImEvent(event);
		
		String simplifiedMessage = simplifyText(rawMessage);
		printConsole("Simplified message = " + simplifiedMessage);
		
		if (null != imHandler) {
			imHandler.responseImText(event, rawMessage, simplifiedMessage);
		}
	}
	
	private class ImDataParser {
		String messageType = null;
		String messageSubType = null;
		byte[] messageBytes = null;
		
		public ImDataParser(byte[] bytes) throws IOException {
			if (null != bytes) {
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				DataInputStream dataStream = new DataInputStream(bais);
				
				messageType = dataStream.readUTF();				
				messageSubType = dataStream.readUTF();
				
				int count = dataStream.available();
				messageBytes = new byte[count];
				dataStream.read(messageBytes);
			}
		}
		
		public String getMessageType() {
			return messageType;
		}
		
		public String getMessageSubType() {
			return messageSubType;
		}
		
		public byte[] getMessagebytes() {
			return messageBytes;
		}
	}
	/*
	 * http://stackoverflow.com/questions/3607965/how-to-convert-html-text-to-plain-text
	 */
	private String simplifyText(String htmlString) {
		return htmlString.replaceAll("\\<.*?\\>", "");
	}
	
	private void debugImEvent(ImEvent event) {
		printConsole("DataType is " + event.getDataType());
		printConsole("DataSubType is " + event.getDataSubType());
		printConsole("Text is " + event.getText());
		
		byte[] bytes = event.getData();
		if (null != bytes) {
			try {
				ImDataParser parser = new ImDataParser(bytes);
				printConsole("messageType is " + parser.getMessageType());
				printConsole("messageSubType is " + parser.getMessageSubType());
				StringBuffer buf = new StringBuffer("messageBytes is ");
				for (byte b : parser.getMessagebytes()) {
					buf.append(String.format(" %X", b));
				}
				printConsole(buf.toString());
			} catch (IOException e) {
				printConsole(e);
			}
		} else {
			printConsole("Data is null.");
		}
	}
	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	
	private static void printConsole(String message) {
		System.out.println(dateFormatter.format(new Date()) + " " + message);
	}

	private static void printConsole(Exception e) {
		e.printStackTrace();
	}
}
