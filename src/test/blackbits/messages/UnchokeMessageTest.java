package blackbits.messages;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class UnchokeMessageTest extends TestCase {
    private UnchokeMessage message;

    protected void setUp() throws Exception {
        super.setUp();
        message = new UnchokeMessage();
    }

    public void testLength() throws Exception {
        assertEquals(1, message.getLength());
    }

    public void testRead() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        message.read(buffer, 1);
        assertEquals(0, buffer.position());
    }

    public void testWrite() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        message.write(buffer);
        assertEquals(1, buffer.position());
        assertEquals(1, buffer.get(0));
    }
}
