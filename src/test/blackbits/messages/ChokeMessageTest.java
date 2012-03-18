package blackbits.messages;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class ChokeMessageTest extends TestCase {
    private ChokeMessage chokeMessage;

    protected void setUp() throws Exception {
        super.setUp();
        chokeMessage = new ChokeMessage();
    }

    public void testLength() throws Exception {
        assertEquals(1, chokeMessage.getLength());
    }

    public void testRead() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        chokeMessage.read(buffer, 1);
        assertEquals(0, buffer.position());
    }

    public void testWrite() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        chokeMessage.write(buffer);
        assertEquals(1, buffer.position());
        assertEquals(0, buffer.get(0));
    }
}
