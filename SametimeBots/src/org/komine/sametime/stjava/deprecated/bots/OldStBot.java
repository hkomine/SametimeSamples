package org.komine.sametime.stjava.deprecated.bots;

import java.util.*;

import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.community.*;
import com.lotus.sametime.im.*;
import com.lotus.sametime.core.constants.*;
import com.lotus.sametime.chatui.*;
import com.lotus.sametime.places.*;

public class OldStBot implements Runnable, LoginListener, ImServiceListener,
		ImListener, JoinMeetingListener, PlacesServiceListener, PlaceListener,
		MyMsgListener {
	private STSession m_session;
	private CommunityService m_comm;
	private InstantMessagingService m_imService;
	private PlacesService m_placesService;
	private ChatUI m_chatUI;
	private MyselfInPlace m_myself;

	private Vector m_ImOpened = new Vector();
	private String m_stServerName;
	private String m_loginName;
	private String m_password;

	public OldStBot(String stServerName, String loginName, String password) {
		m_stServerName = stServerName;
		m_loginName = loginName;
		m_password = password;
	}

	public void run() {
		try {
			m_session = new STSession("StBot" + this);
			m_session.loadAllComponents();
			m_session.start();
			login();
		} catch (DuplicateObjectException e) {
			e.printStackTrace();
			return;
		}
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage:");
			System.out.println("\t[Sametime server name] [login name] [password]\n");
			System.exit(0);
		}
		String stServerName = args[0];
		String loginName = args[1];
		String password = args[2];
		
		OldStBot stBot = new OldStBot(stServerName, loginName, password);
		Thread thread= new Thread(stBot);
		thread.start();
	}

	private void login() {
		m_comm = (CommunityService) m_session.getCompApi(CommunityService.COMP_NAME);
		m_comm.addLoginListener(this);
		m_comm.loginByPassword(m_stServerName, m_loginName, m_password);
	}

	public void enterPlace(String placeName) {
		Place place = m_placesService.createPlace(placeName, placeName + "'s Place", EncLevel.ENC_LEVEL_RC2_40, 0);
		place.addPlaceListener(this);
		//	place.enter();
	}

	public void loggedIn(LoginEvent evt) {
		m_imService = (InstantMessagingService) m_session.getCompApi(InstantMessagingService.COMP_NAME);
		m_imService.registerImType(ImTypes.IM_TYPE_CHAT);
		m_imService.addImServiceListener(this);

		m_chatUI = (ChatUI) m_session.getCompApi(ChatUI.COMP_NAME);
		m_chatUI.setChatFactory(new OldStBotChatFactory(this, m_session));

		m_placesService = (PlacesService) m_session.getCompApi(PlacesService.COMP_NAME);
		m_placesService.addPlacesServiceListener(this);

		System.out.println("StBot is now ready");

	}

	public void loggedOut(LoginEvent evt) {
	}

	public void imReceived(ImEvent event) {
		Im im = event.getIm();
		boolean imExsit = false;
		Im currentIm = null;
		for (int i = 0; i < m_ImOpened.size(); i++) {
			currentIm = (Im) m_ImOpened.elementAt(i);
			if (currentIm.equals(im)) {
				imExsit = true;
				im = currentIm;
				break;
			}
		}
		if (!imExsit) {
			m_ImOpened.addElement(im);
			im.addImListener(this);
		}
		im.sendText(true, "Welcome to the Sametime Bot Service!");
	}

	public void imOpened(ImEvent event) {
	}

	public void textReceived(ImEvent event) {
		System.out.println("[ImListener] Text received.");
		Im im = event.getIm();
		String receivedMessage = event.getText().trim();
		String partnerName = event.getIm().getPartner().getDisplayName();

		String responseMessage ="Message received : " + receivedMessage;
		System.out.println("[ImListener] Response message is " + responseMessage);
		im.sendText(true, responseMessage);
	}

	public void dataReceived(ImEvent evt) {
	}

	public void openImFailed(ImEvent evt) {
		Im im = evt.getIm();
		im.removeImListener(this);
		m_ImOpened.removeElement(im);
	}

	public void imClosed(ImEvent event) {
		Im im = event.getIm();
		Im currentIm = null;

		for (int i = 0; i < m_ImOpened.size(); i++) {
			currentIm = (Im) m_ImOpened.elementAt(i);
			if (currentIm.equals(im)) {
				m_ImOpened.removeElement(im);
				im.close(0);
				im.removeImListener(this);
				break;
			}
		}
	}

	public void acceptMeeting(MeetingInfo info) {
		System.out.println("accepting meeting ...");
	}
	public void declineMeeting(MeetingInfo info) {
	}

	public void serviceAvailable(PlacesServiceEvent event) {
		System.out.println("Place Service Available");
	}
	public void serviceUnavailable(PlacesServiceEvent event) {
	}

	public void activityAdded(PlaceEvent event) {
	}
	public void activityRemoved(PlaceEvent event) {
	}
	public void addActivityFailed(PlaceEvent event) {
	}
	public void addAllowedUsersFailed(PlaceEvent event) {
	}
	public void entered(PlaceEvent event) {
		Place place = event.getPlace();
		System.out.println("Entered the place: " + place.getName());

		// todo
		m_myself = place.getMyselfInPlace();
		m_myself.addMyMsgListener(this);
	}
	public void enterFailed(PlaceEvent event) {
		Place place = event.getPlace();
		System.out.println("Enter falied the place: " + place.getName());
	}
	public void invite15UserFailed(PlaceEvent event) {
	}
	public void left(PlaceEvent event) {
	}
	public void removeAllowedUsersFailed(PlaceEvent event) {
	}
	public void sectionAdded(PlaceEvent event) {
	}
	public void sectionRemoved(PlaceEvent event) {
	}
	
	public void attributeChanged(PlaceMemberEvent event) {
	}
	public void attributeRemoved(PlaceMemberEvent event) {
	}
	public void changeAttributeFailed(PlaceMemberEvent event) {
	}
	public void queryAttrContentFailed(PlaceMemberEvent event) {
	}
	public void removeAttributeFailed(PlaceMemberEvent event) {
	}
	public void sendFailed(PlaceMemberEvent event) {
	}

	public void dataReceived(MyselfEvent event) {
	}
	public void textReceived(MyselfEvent event) {
		System.out.println("[MyMsgListener] Text received.");
		UserInPlace sender = (UserInPlace) event.getSender();
		if (!sender.getId().equals(m_myself.getId())) {
			String receivedMessage = event.getText().trim();
			String responseMessage = "Message received : "  + receivedMessage;
			System.out.println("[MyMsgListener] Responding message is " + responseMessage);
			m_myself.getPlace().sendText(responseMessage);

		} else {
			System.out.println("[MyMsgListener] It was my message.");
		}
	}
}
