package kr.pe.codda.weblib;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class MockServletInputStream extends ServletInputStream {

	private final InputStream sourceStream;

	public MockServletInputStream(InputStream sourceStream) {
		this.sourceStream = sourceStream;
	}

	@Override
	public int read() throws IOException {
		return sourceStream.read();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public boolean isFinished()	{
		return false;
	}

	@Override
	public void setReadListener(ReadListener rl) {
	}
}
