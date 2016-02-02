package de.adesso.iliasdownloader2.util;

import lombok.Data;

import java.io.*;

@Data
@Deprecated
public class CircularStream {

	private InputStream inputStream;
	private OutputStream outputStream;

	public CircularStream() {
		try {
			PipedInputStream p = new PipedInputStream();
			inputStream = p;
			outputStream = new PipedOutputStream(p);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
