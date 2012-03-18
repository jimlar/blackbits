package blackbits.messages;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageReader {
    private ByteBuffer buffer;
    private boolean hasHandshaked;

    public MessageReader(ByteBuffer buffer) {
        this.buffer = buffer;
        this.hasHandshaked = false;
    }

    /**
     * @return next message or null if no message ready yet
     */
    public Message readNext() throws IOException {
        if (!hasHandshaked) {
            return readHandshake();
        } else {
            return readOrdinary();
        }
    }

    private Message readHandshake() throws IOException {
        int messageLength = HandshakeMessage.LENGTH;
        if (buffer.position() < messageLength) {
            /* No complete message in buffer yet */
            return null;
        }

        buffer.flip();
        Message message = new HandshakeMessage();
        message.read(buffer, 0);
        buffer.compact();
        hasHandshaked = true;
        return message;
    }

    private Message readOrdinary() throws IOException {
        int messageLength = getMessageLength();
        if (messageLength == -1 || (buffer.position() - 4) < messageLength) {
            /* No complete message in buffer yet */
            return null;
        }

        buffer.flip();
        Message message = null;
        messageLength = buffer.getInt();
        if (messageLength == 0) {
            message = new KeepAliveMessage();
        } else {
            byte messageId = buffer.get();
            message = createMessageInstance(messageId);
        }

        message.read(buffer, messageLength);
        buffer.compact();
        return message;
    }

    private Message createMessageInstance(byte messageId) {
        switch (messageId) {
            case 0:
                return new ChokeMessage();
            case 1:
                return new UnchokeMessage();
            case 2:
                return new InterestedMessage();
            case 3:
                return new NotInterestedMessage();
            case 4:
                return new HaveMessage();
            case 5:
                return new BitFieldMessage();
            case 6:
                return new RequestMessage();
            case 7:
                return new PieceMessage();
            case 8:
                return new CancelMessage();
            default:
                throw new IllegalArgumentException("Unknown message type: " + messageId);
        }
    }

    private int getMessageLength() {
        if (buffer.position() < 4) {
            return -1;
        }
        return buffer.getInt(0);
    }
}
