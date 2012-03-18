package blackbits.messages;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class BitFieldMessageTest extends TestCase {
    private BitFieldMessage message;

    protected void setUp() throws Exception {
        super.setUp();
        message = new BitFieldMessage(4);
    }

    public void testLength() throws Exception {
        assertEquals(2, message.getLength());
        message = new BitFieldMessage(9);
        assertEquals(3, message.getLength());
        message = new BitFieldMessage(16);
        assertEquals(3, message.getLength());
    }

    public void testRead() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 0xf0);
        buffer.flip();
        message.read(buffer, 2);
        assertTrue(message.havePiece(0));
        assertTrue(message.havePiece(1));
        assertTrue(message.havePiece(2));
        assertTrue(message.havePiece(3));
        assertFalse(message.havePiece(4));
        assertFalse(message.havePiece(5));
        assertFalse(message.havePiece(6));
        assertFalse(message.havePiece(7));
    }

    public void testWrite() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        message.setHave(0, true);
        message.setHave(1, true);
        message.setHave(2, true);
        message.setHave(3, true);
        message.write(buffer);
        buffer.flip();
        assertEquals(5, buffer.get());
        assertEquals((byte) 0xf0, buffer.get());
        assertFalse(buffer.hasRemaining());
    }
}
