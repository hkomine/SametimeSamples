package org.komine.sametime.stjava.bots.data;

import java.io.IOException;
import java.util.logging.Logger;

import com.lotus.sametime.core.util.NdrInputStream;

public class PlaceInvitationDataParser extends AbstractDataParser {

	int dataSize;
	String displayName;
	String placeName;
	String serverName;
	
	static final int MAX_UNKNOWNBYTES01 = 21;
	static final int MAX_UNKNOWNBYTES02 = 2;

	// Java Logger
	final Logger logger = Logger.getLogger(PlaceInvitationDataParser.class.getSimpleName());
	
	public String getPlaceName() {
		return placeName;
	}
	
	@Override
	public void parse(byte[] bytes) throws IOException {
		if (null == bytes)
			return;
		NdrInputStream nis = new NdrInputStream(bytes);

		// Read dataSize
		dataSize = nis.readInt();
		logger.fine("[ImDataParser] dataSize = " + dataSize);

		// Read displayName
		displayName = nis.readUTF();
		logger.fine("[ImDataParser] displayName = " + displayName);

		// Read unknown data
		readBytes(nis, MAX_UNKNOWNBYTES01);

		// Read placeName
		placeName = nis.readUTF();
		logger.fine("[ImDataParser] placeName = " + placeName);

		// Read unknown data
		readBytes(nis, MAX_UNKNOWNBYTES02);

		// Read serverName
		serverName = nis.readUTF();
		logger.fine("[ImDataParser] serverName = " + serverName);

		// read remaining data
		int available = nis.available();
		logger.fine("[ImDataParser] available = " + available);
		readBytes(nis, available);
	}

}
