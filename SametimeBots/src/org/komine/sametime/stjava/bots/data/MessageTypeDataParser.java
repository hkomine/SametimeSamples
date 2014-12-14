package org.komine.sametime.stjava.bots.data;

import java.io.IOException;
import java.util.logging.Logger;

import com.lotus.sametime.core.util.NdrInputStream;

public class MessageTypeDataParser extends AbstractDataParser {
	
	String messageType, messageSubType;
	byte[] messageBytes;
	
	// Java Logger
	final Logger logger = Logger.getLogger(MessageTypeDataParser.class.getSimpleName());
	
	@Override
	public void parse(byte[] bytes) throws IOException {
		if (null == bytes)
			return;
		NdrInputStream nis = new NdrInputStream(bytes);

		messageType = nis.readUTF();
		logger.info("messageType = " + messageType);

		messageSubType = nis.readUTF();
		logger.info("messageSubType = " + messageSubType);

		// read message bytes
		int available = nis.available();
		logger.info("available = " + available);
		messageBytes = readBytes(nis, available);
	}
}
