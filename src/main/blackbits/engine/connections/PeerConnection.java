package blackbits.engine.connections;

import blackbits.messages.HandshakeMessage;
import blackbits.messages.Message;
import blackbits.messages.MessageReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.LinkedList;
import java.util.Queue;

public class PeerConnection {
    private ByteChannel channel;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private MessageReader messageReader;
    private Queue sendMessageQueue = new LinkedList();

    public PeerConnection(ByteChannel channel) {
        this.channel = channel;
        readBuffer = ByteBuffer.allocateDirect(1024 * 20);
        writeBuffer = ByteBuffer.allocateDirect(1024 * 20);
        writeBuffer.limit(0);
        messageReader = new MessageReader(readBuffer);
    }

    public void send(Message message) {
        synchronized (sendMessageQueue) {
            sendMessageQueue.add(message);
        }
    }

    public Message performRead() throws IOException {
        channel.read(readBuffer);
        return messageReader.readNext();
    }

    public void performWrite() throws IOException {
        fillWriteBuffer();
        if (writeBuffer.hasRemaining()) {
            channel.write(writeBuffer);
        }
        fillWriteBuffer();
    }

    public boolean wantsToWrite() {
        return writeBuffer.hasRemaining() || hasMessageInQueue();
    }

    private void fillWriteBuffer() throws IOException {
        synchronized (sendMessageQueue) {
            if (!writeBuffer.hasRemaining() && hasMessageInQueue()) {
                writeBuffer.clear();
                Message message = (Message) sendMessageQueue.poll();
                if (!(message instanceof HandshakeMessage)) {
                    writeBuffer.putInt(message.getLength());
                }
                message.write(writeBuffer);
                writeBuffer.flip();
            }
        }
    }

    private boolean hasMessageInQueue() {
        synchronized (sendMessageQueue) {
            return !sendMessageQueue.isEmpty();
        }
    }
}
