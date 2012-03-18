package blackbits.engine;

import blackbits.descriptor.Torrent;
import blackbits.engine.connections.PeerConnectionsManager;
import blackbits.tracker.Tracker;
import blackbits.tracker.TrackerResponse;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.util.Collections;

public class TorrentProcessTest extends MockObjectTestCase {

    public void testRun() throws Exception {
        Mock torrentMock = mock(Torrent.class);

        Mock trackerMock = mock(Tracker.class);
        trackerMock.expects(once()).method("signalStart").will(returnValue(new TrackerResponse(0, 0, 1800, Collections.EMPTY_LIST)));
        trackerMock.expects(once()).method("signalStopped");

        Mock peerConnectionsManagerMock = mock(PeerConnectionsManager.class);
        peerConnectionsManagerMock.expects(once()).method("addListener");
        peerConnectionsManagerMock.expects(once()).method("start");
        peerConnectionsManagerMock.expects(once()).method("stop");
        peerConnectionsManagerMock.expects(once()).method("performIO").will(throwException(new RuntimeException()));

        TorrentProcess process = new TorrentProcess(null,
                                                    (Torrent) torrentMock.proxy(),
                                                    (Tracker) trackerMock.proxy(),
                                                    (PeerConnectionsManager) peerConnectionsManagerMock.proxy());

        try {
            process.run();
            fail("Should have gotten the exception from performIO()");
        } catch (Exception e) {
        }
        trackerMock.verify();
        torrentMock.verify();
        peerConnectionsManagerMock.verify();
    }
}
