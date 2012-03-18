package blackbits.messages;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class KeepAliveMessageTest extends TestCase {
    private KeepAliveMessage keepAliveMessage;

    protected void setUp() throws Exception {
        super.setUp();
        keepAliveMessage = new KeepAliveMessage();
    }

    public void testSize() throws Exception {
        assertEquals(0, keepAliveMessage.getLength());
    }

    public void testRead() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        keepAliveMessage.read(buffer, 0);
        assertEquals(0, buffer.position());
    }

    public void testWrite() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        keepAliveMessage.write(buffer);
        assertEquals(0, buffer.position());
    }

}
