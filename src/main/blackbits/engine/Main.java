package blackbits.engine;

import blackbits.descriptor.Torrent;
import blackbits.engine.connections.PeerConnectionsManager;
import blackbits.engine.connections.PeerConnectionsManagerImpl;
import blackbits.registry.DiskTorrentRegistry;
import blackbits.registry.RegistryListener;
import blackbits.registry.TorrentRegistry;
import blackbits.tracker.Peer;
import blackbits.tracker.TrackerImpl;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        DiskTorrentRegistry torrentRegistry = new DiskTorrentRegistry(new File("target", "main-torrents"));

        torrentRegistry.addListener(new RegistryListener() {
            public void registered(TorrentRegistry registry, Torrent torrent) {
            }

            public void activated(final TorrentRegistry registry, final Torrent torrent) {
                Peer localPeer = new Peer("BlackBits-0.0.1-0001", 4715, "192.168.1.7");
                TrackerImpl tracker = new TrackerImpl(localPeer, torrent.getAnnounceUrl(), torrent.getInfoHash());
                PeerConnectionsManager peerConnectionsManager = new PeerConnectionsManagerImpl(new InetSocketAddress(4715));
                new TorrentProcess(localPeer, torrent, tracker, peerConnectionsManager).run();
            }

            public void paused(TorrentRegistry registry, Torrent torrent) {
            }
        });


//        Torrent torrent = torrentRegistry.register(new TorrentDescriptorFile(new File("testdata", "multifile.torrent")));
//       torrentRegistry.activate(torrent);
    }
}
