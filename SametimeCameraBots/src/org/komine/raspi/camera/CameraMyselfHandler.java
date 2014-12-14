package org.komine.raspi.camera;

import org.komine.sametime.stjava.bots.AbstractMyselfHandler;
import org.komine.sametime.stjava.bots.SametimeBot;

import com.lotus.sametime.places.MyselfEvent;
import com.lotus.sametime.places.MyselfInPlace;

public class CameraMyselfHandler extends AbstractMyselfHandler {

	public CameraMyselfHandler(SametimeBot callback) {
		super(callback);
	}

	@Override
	public void responseImText(MyselfInPlace myselfPlace, MyselfEvent event,
			String receivedMessage) {
		myselfPlace.getPlace().sendText("Received in place: " + receivedMessage);
	}

}
