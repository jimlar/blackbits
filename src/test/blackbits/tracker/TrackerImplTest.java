package blackbits.tracker;

import blackbits.descriptor.Torrent;
import blackbits.descriptor.TorrentFile;
import junit.framework.TestCase;

import java.io.File;
import java.util.List;

public class TrackerImplTest extends TestCase {
    private Tracker connection;

    protected void setUp() throws Exception {
        super.setUp();
        Peer localPeer = new Peer("BlackBits-0.0.1-0000", 9999, null);
        Torrent torrent = new TorrentFile(new File("testdata", "blackbits.tgz.torrent"));
        connection = new TrackerImpl(localPeer, torrent.getAnnounceUrl(), torrent.getInfoHash());
    }

    public void testSendEvents() throws Exception {
        TrackerResponse trackerResponse = connection.signalStart(0, 2, 6);

        assertTrackerResponse(trackerResponse);

        trackerResponse = connection.update(1, 3, 3);
        assertTrackerResponse(trackerResponse);

        trackerResponse = connection.signalCompleted(1, 3);
        assertTrackerResponse(trackerResponse);

        trackerResponse = connection.signalStopped(1, 3, 0);
        assertTrackerResponse(trackerResponse);
    }

    public void testGetTorrentStatus() throws Exception {
        assertTrue(connection.isGetStatusSupported());
        TorrentStatus status = connection.getStatus();
        System.out.println("status.getSeeders() = " + status.getSeeders());
        System.out.println("status.getLeechers() = " + status.getLeechers());
        System.out.println("status.getNumberOfDownloads() = " + status.getNumberOfDownloads());
    }

    private void assertTrackerResponse(TrackerResponse trackerResponse) {
        assertEquals(1800, trackerResponse.getUpdateIntervalSeconds());
        assertEquals(1, trackerResponse.getNumberOfSeeders());
        //assertEquals(0, trackerResponse.getNumberOfLeechers());

        List peers = trackerResponse.getPeers();
        assertEquals(1, peers.size());
        Peer peer = (Peer) peers.get(0);
        assertEquals("T038-----flu0P1yu4CG", peer.getId());
        assertEquals("192.168.1.5", peer.getAddress());
        assertEquals(17401, peer.getPort());
    }
}
