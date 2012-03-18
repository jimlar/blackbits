package blackbits.messages;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class CancelMessageTest extends TestCase {

    public void testLength() throws Exception {
        assertEquals(13, new CancelMessage().getLength());
    }

    public void testRead() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.putInt(2);
        buffer.putInt(1024);
        buffer.putInt(16384);
        buffer.flip();
        CancelMessage message = new CancelMessage();
        message.read(buffer, 13);
        assertEquals(2, message.getPieceIndex());
        assertEquals(1024, message.getBlockOffset());
        assertEquals(16384, message.getBlockLength());
    }

    public void testWrite() throws Exception {
        CancelMessage message = new CancelMessage(2, 1024, 16384);
        ByteBuffer buffer = ByteBuffer.allocate(13);
        message.write(buffer);
        buffer.flip();
        assertEquals(8, buffer.get());
        assertEquals(2, buffer.getInt());
        assertEquals(1024, buffer.getInt());
        assertEquals(16384, buffer.getInt());
    }
}
