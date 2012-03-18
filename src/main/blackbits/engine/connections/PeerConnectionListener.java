package blackbits.engine.connections;

import blackbits.messages.Message;

public interface PeerConnectionListener {
    void connected(PeerConnection connection);

    void received(PeerConnection connection, Message message);
}
