package kr.pe.codda.weblib;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

public class MockServletInputStream extends ServletInputStream {

    private final InputStream delegate;

    public MockServletInputStream(InputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }

}

