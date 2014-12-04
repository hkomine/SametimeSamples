package org.komine.sametime.stjava.bots;

import com.lotus.sametime.im.ImEvent;

public interface ImHandler {
	public void showWelcomeMessage(ImEvent event);
	public void responseImText(ImEvent event, String rawMessage, String simplifiedMessage);
}
