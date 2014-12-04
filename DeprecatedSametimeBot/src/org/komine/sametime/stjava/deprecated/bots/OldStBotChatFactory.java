package org.komine.sametime.stjava.deprecated.bots;

import com.lotus.sametime.chatui.*;
import com.lotus.sametime.core.comparch.*;
import com.lotus.sametime.core.types.*;

public class OldStBotChatFactory
	extends DefaultChatFactory
	implements ChatModelListener {
	private OldStBot m_stBot;
	private ChatModel m_chatModel; // keep ChatModel
	
	public OldStBotChatFactory(OldStBot stBot, STSession session) {
		super(session);
		m_stBot = stBot;
	}

	public void createView(ChatModel model, String partnerame, boolean originator) {
		m_chatModel = model;
		m_chatModel.addChatModelListener(this);	//	Adding ChatModelListener
	}

	public void showJoinDialog(STSession session, MeetingInfo info, STUser inviter, String invitation, JoinMeetingListener listener) {
		/*
		 * If UI app, we should show dialog for entering place.
		 */
		listener.acceptMeeting(info);
		m_stBot.enterPlace(info.getPlaceName());
	}
	
	public void switchToConference() {
		/*
		 * On converting Chat to Conference (Chat only meeting is handled as n-way chat internally.)
		 */
		System.out.println("switchToConference");
		MeetingInfo info = m_chatModel.getConfInfo();
		System.out.println("Entering place, " + info.getDisplayName());
		m_stBot.enterPlace(info.getPlaceName());
	}
	
	public void switchToMeeting(MeetingInfo info) {
		/*
		 * On converting from Chat to Meeting
		 */
		System.out.println("swtichToMeeting");
	}
}
