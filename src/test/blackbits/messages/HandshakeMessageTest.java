package blackbits.messages;

import blackbits.hash.SHAHash;
import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class HandshakeMessageTest extends TestCase {
    private HandshakeMessage handshakeMessage;
    private String peerId;
    private SHAHash torrentHash;

    protected void setUp() throws Exception {
        super.setUp();
        torrentHash = new SHAHash("f73a14a622ac09c4cfaa9f40ec12b883ece1cf9d");
        peerId = "12345678901234567890";
        handshakeMessage = new HandshakeMessage(torrentHash, peerId);
    }

    public void testSize() throws Exception {
        assertEquals(68, handshakeMessage.getLength());
    }

    public void testWrite() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(70);
        handshakeMessage.write(buffer);
        assertEquals(68, buffer.position());
        buffer.flip();
        assertEquals(19, buffer.get());
        byte[] bytes = new byte[19];
        buffer.get(bytes);
        assertTrue(Arrays.equals("BitTorrent protocol".getBytes("UTF-8"), bytes));
        assertEquals(0, buffer.getLong());
        bytes = new byte[20];
        buffer.get(bytes);
        assertTrue(Arrays.equals(torrentHash.getBytes(), bytes));
        buffer.get(bytes);
        assertTrue(Arrays.equals(peerId.getBytes("UTF-8"), bytes));
    }

    public void testRead() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(68);
        buffer.put((byte) 19);
        buffer.put("BitTorrent protocol".getBytes("UTF-8"));
        buffer.putLong(0);
        buffer.put(torrentHash.getBytes());
        buffer.put(peerId.getBytes("UTF-8"));
        buffer.flip();
        HandshakeMessage readHandshakeMessage = new HandshakeMessage();
        readHandshakeMessage.read(buffer, 68);
        assertEquals(handshakeMessage, readHandshakeMessage);
        assertEquals(torrentHash, readHandshakeMessage.getTorrentHash());
        assertEquals(peerId, readHandshakeMessage.getPeerId());
    }
}
