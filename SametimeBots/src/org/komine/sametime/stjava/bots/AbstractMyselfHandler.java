package org.komine.sametime.stjava.bots;

import java.util.logging.Logger;

import com.lotus.sametime.places.MyselfEvent;
import com.lotus.sametime.places.MyselfInPlace;
import com.lotus.sametime.places.UserInPlace;

public abstract class AbstractMyselfHandler implements MyselfHandler {

	SametimeBot callback;
	
	// Java Logger
	final Logger logger = Logger.getLogger(AbstractMyselfHandler.class.getSimpleName());
	
	public AbstractMyselfHandler(SametimeBot callback) {
		this.callback = callback;
	}
	
	abstract public void responseImText(MyselfInPlace myselfPlace, MyselfEvent event, String receivedMessage);
	
	@Override
	public void processTextReceived(MyselfInPlace myselfPlace, MyselfEvent event) {		
		UserInPlace sender = (UserInPlace) event.getSender();
		if (!sender.getId().equals(myselfPlace.getId())) {
			String receivedMessage = event.getText().trim();
			responseImText(myselfPlace, event, receivedMessage);
		} else {
			System.out.println("It was my message.");
		}
	}
}
