package blackbits.messages;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class HaveMessageTest extends TestCase {
    private HaveMessage message;

    protected void setUp() throws Exception {
        super.setUp();
        message = new HaveMessage(5678);
    }

    public void testLength() throws Exception {
        assertEquals(5, message.getLength());
    }

    public void testRead() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(1234);
        buffer.flip();
        message.read(buffer, 5);
        assertEquals(1234, message.getPieceIndex());
    }

    public void testWrite() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        message.write(buffer);
        buffer.flip();
        assertEquals(4, buffer.get());
        assertEquals(5678, buffer.getInt());
    }
}
