package blackbits.engine;

import blackbits.descriptor.Torrent;
import blackbits.engine.connections.PeerConnection;
import blackbits.engine.connections.PeerConnectionListener;
import blackbits.engine.connections.PeerConnectionsManager;
import blackbits.messages.*;
import blackbits.tracker.Peer;
import blackbits.tracker.Tracker;
import blackbits.tracker.TrackerCommunicationException;
import blackbits.tracker.TrackerResponse;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class TorrentProcess implements Runnable {
    private Torrent torrent;
    private Tracker tracker;
    private PeerConnectionsManager peerConnectionsManager;

    public TorrentProcess(final Peer localPeer, final Torrent torrent, Tracker tracker, PeerConnectionsManager peerConnectionsManager) {
        this.torrent = torrent;
        this.tracker = tracker;
        this.peerConnectionsManager = peerConnectionsManager;
        this.peerConnectionsManager.addListener(new PeerConnectionListener() {
            private int messages = 0;
            private int requested = 0;

            public void connected(PeerConnection connection) {
                connection.send(new HandshakeMessage(torrent.getInfoHash(), localPeer.getId()));
            }

            public void received(PeerConnection connection, Message message) {
                messages++;
                if (messages > 80) {
                    System.out.println("");
                    messages = 0;
                }
                String name = message.getClass().getName();
                System.out.print(name.charAt(name.lastIndexOf('.') + 1));

                if (message instanceof HandshakeMessage) {
                    connection.send(new BitFieldMessage(torrent.getNumberOfPieces()));
                    connection.send(new InterestedMessage());
                    connection.send(new UnchokeMessage());
                }
                if (message instanceof UnchokeMessage) {
                    connection.send(new RequestMessage(0, 0, 16384));
                    connection.send(new RequestMessage(1, 0, 16384));
                    connection.send(new RequestMessage(2, 0, 16384));
                    connection.send(new RequestMessage(3, 0, 16384));
                    connection.send(new RequestMessage(4, 0, 16384));
                    connection.send(new RequestMessage(5, 0, 16384));
                    requested = 5;
                }

                if (message instanceof PieceMessage) {
                    requested++;
                    if (requested >= torrent.getNumberOfPieces()) {
                        requested = 0;
                    }
                    connection.send(new RequestMessage(requested, 0, 16384));
                }
            }
        });
    }

    public void run() {
        try {
            System.out.println("Process started for torrent: " + torrent);
            start();
            while (true) {
                peerConnectionsManager.performIO();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stop();
            } catch (Exception e) {
                System.out.println("Could not stop");
                e.printStackTrace();
            }
        }
    }

    private void start() throws IOException, TrackerCommunicationException {
        System.out.println("Starting comms manager");
        peerConnectionsManager.start();

        System.out.println("Signaling start to tracker");
        TrackerResponse trackerResponse = tracker.signalStart(0, 0, 1024);

        List peers = trackerResponse.getPeers();
        System.out.println("Connecting to " + peers + " peers");
        for (Iterator i = peers.iterator(); i.hasNext();) {
            peerConnectionsManager.connect((Peer) i.next());
        }
    }

    private void stop() throws IOException, TrackerCommunicationException {
        System.out.println("Signalling stop to tracker");
        tracker.signalStopped(0, 0, 1024);
        peerConnectionsManager.stop();
        System.out.println("Torrent process end");
    }
}
