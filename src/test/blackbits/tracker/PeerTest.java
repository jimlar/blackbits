package blackbits.tracker;

import junit.framework.TestCase;
/* Moahahah */
public class PeerTest extends TestCase {
    public void testPeerWithIdNot20CharsGivesException() throws Exception {
        try {
            new Peer("1234567890123456789", 1, "");
            fail("Should not be able to construct peer with id not 20 chars");
        } catch (Exception e) {
        }
    }
}
