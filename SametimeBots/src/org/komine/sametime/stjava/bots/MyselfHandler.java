package org.komine.sametime.stjava.bots;

import com.lotus.sametime.places.MyselfEvent;
import com.lotus.sametime.places.MyselfInPlace;

public interface MyselfHandler {
	public void processTextReceived(MyselfInPlace myselfPlace, MyselfEvent event);
}
