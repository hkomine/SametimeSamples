package org.komine.sametime.stjava.bots;

import java.util.logging.Logger;

import com.lotus.sametime.chat.invitation.Invitation;
import com.lotus.sametime.chat.invitation.InvitationListener;
import com.lotus.sametime.chat.invitation.InvitationManager;
import com.lotus.sametime.community.CommunityService;
import com.lotus.sametime.community.LoginEvent;
import com.lotus.sametime.community.LoginListener;
import com.lotus.sametime.community.ServiceEvent;
import com.lotus.sametime.community.ServiceListener;
import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.constants.EncLevel;
import com.lotus.sametime.core.constants.ImTypes;
import com.lotus.sametime.core.constants.STError;
import com.lotus.sametime.im.ImEvent;
import com.lotus.sametime.im.ImListener;
import com.lotus.sametime.im.ImServiceListener;
import com.lotus.sametime.im.InstantMessagingService;
import com.lotus.sametime.places.MyMsgListener;
import com.lotus.sametime.places.MyselfEvent;
import com.lotus.sametime.places.MyselfInPlace;
import com.lotus.sametime.places.Place;
import com.lotus.sametime.places.PlaceEvent;
import com.lotus.sametime.places.PlaceListener;
import com.lotus.sametime.places.PlaceMemberEvent;
import com.lotus.sametime.places.PlacesService;
import com.lotus.sametime.places.PlacesServiceEvent;
import com.lotus.sametime.places.PlacesServiceListener;
import com.lotus.sametime.places.UserInPlace;

public class SametimeBot implements ServiceListener, LoginListener, ImServiceListener, ImListener, PlaceListener, MyMsgListener, PlacesServiceListener, InvitationListener {
	
	private STSession session;
	private CommunityService service;
	private InstantMessagingService imService;
	private PlacesService placesService;
	private MyselfInPlace myselfPlace;
	private InvitationManager invManage;
	
	private ImHandler imHandler;
	
	// Java Logger
	final Logger logger = Logger.getLogger(SametimeBot.class.getSimpleName());
	
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

	public void enterPlace(String placeName) {
		logger.info("Entering place, " + placeName);
		Place place = placesService.createPlace(placeName, placeName + "'s Place", EncLevel.ENC_LEVEL_RC2_40, 0);
		place.addPlaceListener(this);
		place.enter();
	}
	
	public STSession getStSession() {
		return session;
	}
	
	/* (non-Javadoc)
	 * @see com.lotus.sametime.community.LoginListener#loggedIn(com.lotus.sametime.community.LoginEvent)
	 */
	@Override
	public void loggedIn(LoginEvent event) {
		logger.info("Logged in");
		
		imService = (InstantMessagingService) session.getCompApi(InstantMessagingService.COMP_NAME);
		imService.registerImType(ImTypes.IM_TYPE_CHAT);
		imService.addImServiceListener(this);
		
		invManage = new InvitationManager(session);
		invManage.setListener(this);
		
		placesService = (PlacesService) session.getCompApi(PlacesService.COMP_NAME);
		placesService.addPlacesServiceListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.community.LoginListener#loggedOut(com.lotus.sametime.community.LoginEvent)
	 */
	@Override
	public void loggedOut(LoginEvent event) {
	    int reason = event.getReason();
	    if (reason == 0)
	    	logger.info("Successfully logged out.");
	    else 
	    	logger.warning("Failed to login.  Return Code = " + reason + ", message = " + STError.getMessageString(reason));
	    session.stop();
	    session.unloadSession();
	}

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.community.ServiceListener#serviceAvailable(com.lotus.sametime.community.ServiceEvent)
	 */
	@Override
	public void serviceAvailable(ServiceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.im.ImServiceListener#imReceived(com.lotus.sametime.im.ImEvent)
	 */
	@Override
	public void imReceived(ImEvent event) {
		logger.info("IM received.");

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
		logger.info("Data received.");

		if (null != imHandler) imHandler.processDataReceived(event);
	}

	/*
	 * (non-Javadoc)
	 * com.lotus.sametime.im.ImListener
	 */
	@Override
	public void imClosed(ImEvent event) {
		logger.info("IM Closed.");
		event.getIm().removeImListener(this);
	}

	/*
	 * (non-Javadoc)
	 * com.lotus.sametime.im.ImListener
	 */
	@Override
	public void imOpened(ImEvent event) {
		logger.info("IM opened.");
	}

	/*
	 * (non-Javadoc)
	 * com.lotus.sametime.im.ImListener
	 */
	@Override
	public void openImFailed(ImEvent event) { }

	/*
	 * (non-Javadoc)
	 * com.lotus.sametime.im.ImListener
	 */
	@Override
	public void textReceived(ImEvent event) {
		String rawMessage = event.getText();
		logger.info("Text received. raw message = " + rawMessage);
		if (null != imHandler) imHandler.processTextReceived(event);
	}

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceMemberListener#attributeChanged(com.lotus.sametime.places.PlaceMemberEvent)
	 */
	@Override
	public void attributeChanged(PlaceMemberEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceMemberListener#attributeRemoved(com.lotus.sametime.places.PlaceMemberEvent)
	 */
	@Override
	public void attributeRemoved(PlaceMemberEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceMemberListener#changeAttributeFailed(com.lotus.sametime.places.PlaceMemberEvent)
	 */
	@Override
	public void changeAttributeFailed(PlaceMemberEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceMemberListener#queryAttrContentFailed(com.lotus.sametime.places.PlaceMemberEvent)
	 */
	@Override
	public void queryAttrContentFailed(PlaceMemberEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceMemberListener#removeAttributeFailed(com.lotus.sametime.places.PlaceMemberEvent)
	 */
	@Override
	public void removeAttributeFailed(PlaceMemberEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceMemberListener#sendFailed(com.lotus.sametime.places.PlaceMemberEvent)
	 */
	@Override
	public void sendFailed(PlaceMemberEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceListener#activityAdded(com.lotus.sametime.places.PlaceEvent)
	 */
	@Override
	public void activityAdded(PlaceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceListener#activityRemoved(com.lotus.sametime.places.PlaceEvent)
	 */
	@Override
	public void activityRemoved(PlaceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceListener#addActivityFailed(com.lotus.sametime.places.PlaceEvent)
	 */
	@Override
	public void addActivityFailed(PlaceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceListener#addAllowedUsersFailed(com.lotus.sametime.places.PlaceEvent)
	 */
	@Override
	public void addAllowedUsersFailed(PlaceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceListener#enterFailed(com.lotus.sametime.places.PlaceEvent)
	 */
	@Override
	public void enterFailed(PlaceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceListener#entered(com.lotus.sametime.places.PlaceEvent)
	 */
	@Override
	public void entered(PlaceEvent event) {
		Place place = event.getPlace();
		logger.info("Entered the place: " + place.getName());

		// todo
		myselfPlace = place.getMyselfInPlace();
		myselfPlace.addMyMsgListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceListener#invite15UserFailed(com.lotus.sametime.places.PlaceEvent)
	 */
	@Override
	public void invite15UserFailed(PlaceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceListener#left(com.lotus.sametime.places.PlaceEvent)
	 */
	@Override
	public void left(PlaceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceListener#removeAllowedUsersFailed(com.lotus.sametime.places.PlaceEvent)
	 */
	@Override
	public void removeAllowedUsersFailed(PlaceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceListener#sectionAdded(com.lotus.sametime.places.PlaceEvent)
	 */
	@Override
	public void sectionAdded(PlaceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlaceListener#sectionRemoved(com.lotus.sametime.places.PlaceEvent)
	 */
	@Override
	public void sectionRemoved(PlaceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.MyMsgListener#dataReceived(com.lotus.sametime.places.MyselfEvent)
	 */
	@Override
	public void dataReceived(MyselfEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.MyMsgListener#textReceived(com.lotus.sametime.places.MyselfEvent)
	 */
	@Override
	public void textReceived(MyselfEvent event) {
		logger.info("Text received.");
		UserInPlace sender = (UserInPlace) event.getSender();
		if (!sender.getId().equals(myselfPlace.getId())) {
			String receivedMessage = event.getText().trim();
			String responseMessage = "Message received : "  + receivedMessage;
			logger.info("Responding message is " + responseMessage);
			myselfPlace.getPlace().sendText(responseMessage);

		} else {
			logger.info("It was my message.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlacesServiceListener#serviceAvailable(com.lotus.sametime.places.PlacesServiceEvent)
	 */
	@Override
	public void serviceAvailable(PlacesServiceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.places.PlacesServiceListener#serviceUnavailable(com.lotus.sametime.places.PlacesServiceEvent)
	 */
	@Override
	public void serviceUnavailable(PlacesServiceEvent event) { }

	/*
	 * (non-Javadoc)
	 * @see com.lotus.sametime.chat.invitation.InvitationListener#invitedToMeeting(com.lotus.sametime.chat.invitation.Invitation)
	 */
	@Override
	public void invitedToMeeting(Invitation event) {}
}
