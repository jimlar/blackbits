package blackbits.messages;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class PieceMessageTest extends TestCase {

    public void testLength() throws Exception {
        PieceMessage message = new PieceMessage(2, 1024, new byte[16384]);
        assertEquals(16384 + 9, message.getLength());
    }

    public void testWrite() throws Exception {
        PieceMessage message = new PieceMessage(2, 1024, new byte[16384]);
        ByteBuffer buffer = ByteBuffer.allocate(16393);
        message.write(buffer);
        buffer.flip();
        assertEquals(7, buffer.get());
        assertEquals(2, buffer.getInt());
        assertEquals(1024, buffer.getInt());
        assertEquals(16384, buffer.remaining());
    }

    public void testRead() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16392);
        buffer.putInt(2);
        buffer.putInt(1024);
        buffer.put(new byte[16384]);
        buffer.flip();
        PieceMessage message = new PieceMessage();
        message.read(buffer, 16384 + 9);
        assertEquals(2, message.getPieceIndex());
        assertEquals(1024, message.getBlockOffset());
        assertEquals(16384, message.getData().length);
    }

}
