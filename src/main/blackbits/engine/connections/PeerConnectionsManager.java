package blackbits.engine.connections;

import blackbits.tracker.Peer;

import java.io.IOException;

public interface PeerConnectionsManager {

    void start() throws IOException;

    void stop() throws IOException;

    void connect(Peer peer) throws IOException;

    void performIO() throws IOException;

    void addListener(PeerConnectionListener listener);
}
