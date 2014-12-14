package org.komine.sametime.stjava.bots.data;

import java.io.IOException;
import java.util.logging.Logger;

import com.lotus.sametime.core.util.NdrInputStream;

abstract public class AbstractDataParser implements DataParser {

	// Java Logger
	final Logger logger = Logger.getLogger(AbstractDataParser.class.getSimpleName());
	
	abstract public void parse(byte[] bytes) throws IOException;

	public byte[] readBytes(NdrInputStream nis, int size) {
		StringBuffer buf = new StringBuffer("Read bytes: ");
		byte[] bytes = new byte[size];
		try {
			nis.read(bytes);
			for (byte b : bytes) {
				buf.append(String.format(" %X", b));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.fine(buf.toString());
		return bytes;
	}
}
