package blackbits.messages;

import blackbits.hash.SHAHash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HandshakeMessage implements Message {
    public static final int LENGTH = 68;
    private static final String PROTOCOL_IDENTIFIER = "BitTorrent protocol";
    private SHAHash torrentHash;
    private String peerId;

    public HandshakeMessage(SHAHash torrentHash, String peerId) {
        this.torrentHash = torrentHash;
        this.peerId = peerId;
    }

    public HandshakeMessage() {
    }

    public void write(ByteBuffer buffer) throws IOException {
        buffer.put((byte) 19);
        buffer.put(PROTOCOL_IDENTIFIER.getBytes("ascii"));
        buffer.putLong(0);
        buffer.put(torrentHash.getBytes());
        buffer.put(peerId.getBytes("UTF-8"));
    }


    public int getLength() {
        return 68;
    }

    public void read(ByteBuffer buffer, int length) throws IOException {
        verify("Unsupported protocol version", 19 == buffer.get());
        byte[] bytes = new byte[19];
        buffer.get(bytes);
        verify("Unsupported protocol version", Arrays.equals(PROTOCOL_IDENTIFIER.getBytes("UTF-8"), bytes));
        buffer.getLong(); /* ignored the reserved bits */

        bytes = new byte[20];
        buffer.get(bytes);
        torrentHash = new SHAHash(bytes);

        bytes = new byte[20];
        buffer.get(bytes);
        peerId = new String(bytes, "UTF-8");
    }

    private void verify(String failureMessage, boolean valid) throws IOException {
        if (!valid) {
            throw new IOException(failureMessage);
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HandshakeMessage)) return false;

        final HandshakeMessage handshakeMessage = (HandshakeMessage) o;

        if (peerId != null ? !peerId.equals(handshakeMessage.peerId) : handshakeMessage.peerId != null) return false;
        if (torrentHash != null ? !torrentHash.equals(handshakeMessage.torrentHash) : handshakeMessage.torrentHash != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (torrentHash != null ? torrentHash.hashCode() : 0);
        result = 29 * result + (peerId != null ? peerId.hashCode() : 0);
        return result;
    }

    public SHAHash getTorrentHash() {
        return torrentHash;
    }

    public String getPeerId() {
        return peerId;
    }
}
