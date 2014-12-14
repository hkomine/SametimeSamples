package org.komine.sametime.stjava.bots.data;

import java.io.IOException;

public interface DataParser {
	public void parse(byte[] bytes) throws IOException;
}
