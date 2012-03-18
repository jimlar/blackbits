package blackbits.messages;

import blackbits.hash.SHAHash;
import junit.framework.TestCase;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageReaderTest extends TestCase {
    private MessageReader messageReader;
    private ByteBuffer buffer;
    private HandshakeMessage handshakeMessage;

    protected void setUp() throws Exception {
        super.setUp();
        buffer = ByteBuffer.allocate(256);
        messageReader = new MessageReader(buffer);
        handshakeMessage = new HandshakeMessage(new SHAHash("f73a14a622ac09c4cfaa9f40ec12b883ece1cf9d"), "12345678901234567890");
        handshakeMessage.write(buffer);
    }

    public void testContainsHandshakeMessageReturnsFalseOnEmptyBuffer() throws Exception {
        assertNull(new MessageReader(ByteBuffer.allocate(128)).readNext());
    }

    public void testContainsHandshakeMessageReturnsTrueWhenHandshakeMessageInBuffer() throws Exception {
        assertEquals(handshakeMessage, messageReader.readNext());
    }

    public void testDecodeBufferWithKeepAliveMessage() throws Exception {
        assertDecodeMessage(new KeepAliveMessage());
    }

    public void testDecodeBufferWithChokeMessage() throws Exception {
        assertDecodeMessage(new ChokeMessage());
    }

    public void testDecodeBufferWithUnchokeMessage() throws Exception {
        assertDecodeMessage(new UnchokeMessage());
    }

    public void testDecodeBufferWithInteresetedMessage() throws Exception {
        assertDecodeMessage(new InterestedMessage());
    }

    public void testDecodeBufferWithNotInterestedMessage() throws Exception {
        assertDecodeMessage(new NotInterestedMessage());
    }

    public void testDecodeBufferWithHaveMessage() throws Exception {
        assertDecodeMessage(new HaveMessage(1));
    }

    public void testDecodeBufferWithBitFieldsMessage() throws Exception {
        assertDecodeMessage(new BitFieldMessage(90));
    }

    public void testDecodeBufferWithRequestMessage() throws Exception {
        assertDecodeMessage(new RequestMessage(1, 2, 3));
    }

    public void testDecodeBufferWithPieceMessage() throws Exception {
        assertDecodeMessage(new PieceMessage(1, 2, new byte[64]));
    }

    public void testDecodeBufferWithCancelMessage() throws Exception {
        assertDecodeMessage(new CancelMessage());
    }

    private void assertDecodeMessage(Message message) throws IOException {
        buffer.putInt(message.getLength());
        message.write(buffer);
        assertTrue(messageReader.readNext() instanceof HandshakeMessage);
        assertTrue(message.getClass().isInstance(messageReader.readNext()));
        assertEquals(0, buffer.position());
    }
}
