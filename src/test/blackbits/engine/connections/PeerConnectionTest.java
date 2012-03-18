package blackbits.engine.connections;

import blackbits.hash.SHAHash;
import blackbits.messages.HandshakeMessage;
import blackbits.messages.KeepAliveMessage;
import blackbits.messages.UnchokeMessage;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.stub.CustomStub;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class PeerConnectionTest extends MockObjectTestCase {
    private Mock byteChannelMock;
    private PeerConnection peerConnection;

    protected void setUp() throws Exception {
        super.setUp();
        byteChannelMock = mock(ByteChannel.class);
        peerConnection = new PeerConnection((ByteChannel) byteChannelMock.proxy());
    }

    public void testSendDoesNotAffectTheChannel() throws Exception {
        byteChannelMock.expects(never()).method("write");
        peerConnection.send(new KeepAliveMessage());
        byteChannelMock.verify();
    }

    public void testWantsToWriteIsFalseWhenNoMessageInQueue() throws Exception {
        assertFalse(peerConnection.wantsToWrite());
    }

    public void testWantsToWriteIsTrueWhenMessageInQueue() throws Exception {
        peerConnection.send(new KeepAliveMessage());
        assertTrue(peerConnection.wantsToWrite());
    }

    public void testPerformWriteDoesNotWriteIfNoMessageInQueue() throws Exception {
        byteChannelMock.expects(never()).method("write");
        peerConnection.performWrite();
        byteChannelMock.verify();
    }

    public void testPerformWriteDoesWriteMessageInQueue() throws Exception {
        byteChannelMock.expects(once()).method("write").will(returnValue(0));
        peerConnection.send(new KeepAliveMessage());
        peerConnection.performWrite();
        byteChannelMock.verify();
    }

    public void testWriteOfMessagePrefixesMessageWithLength() throws Exception {
        UnchokeMessage message = new UnchokeMessage();
        peerConnection.send(message);
        final ByteBuffer[] buffer = new ByteBuffer[]{null};
        byteChannelMock.expects(once()).method("write").will(new CustomStub("muck") {
            public Object invoke(Invocation invocation) throws Throwable {
                buffer[0] = (ByteBuffer) invocation.parameterValues.get(0);
                return new Integer(0);
            }
        });
        peerConnection.performWrite();
        assertEquals(message.getLength(), buffer[0].getInt(0));
        assertEquals("Message id not written to stream", 1, buffer[0].get(4));
        assertEquals(5, buffer[0].limit());
        assertEquals(0, buffer[0].position());
    }

    public void testWriteOfHandshakeMessageDoesNotPrefixMessageWithLength() throws Exception {
        HandshakeMessage message = new HandshakeMessage(new SHAHash("f73a14a622ac09c4cfaa9f40ec12b883ece1cf9d"),
                                                        "12345678901234567890");
        peerConnection.send(message);
        final ByteBuffer[] buffer = new ByteBuffer[]{null};
        byteChannelMock.expects(once()).method("write").will(new CustomStub("muck") {
            public Object invoke(Invocation invocation) throws Throwable {
                buffer[0] = (ByteBuffer) invocation.parameterValues.get(0);
                return new Integer(0);
            }
        });
        peerConnection.performWrite();
        assertTrue(message.getLength() != buffer[0].getInt(0));
    }

    public void testPerformReadReturnsNullWhenNoDataOnChannel() throws Exception {
        byteChannelMock.expects(once()).method("read").will(returnValue(0));
        assertNull(peerConnection.performRead());
    }
}
