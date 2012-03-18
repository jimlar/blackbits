package blackbits.engine.connections;

import blackbits.messages.Message;
import blackbits.tracker.Peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PeerConnectionsManagerImpl implements PeerConnectionsManager {
    private Selector selector;
    private InetSocketAddress serverSocketAddress;
    private ServerSocketChannel serverSocketChannel;
    private List listeners = new ArrayList();

    public PeerConnectionsManagerImpl(InetSocketAddress serverSocketAddress) {
        this.serverSocketAddress = serverSocketAddress;
    }

    public void start() throws IOException {
        selector = Selector.open();
        System.out.println("Starting server socket");
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(serverSocketAddress);

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Waiting for connections on " + serverSocketAddress);
    }

    public void stop() throws IOException {
        serverSocketChannel.close();
        selector.close();
    }

    public void connect(Peer peer) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(peer.getAddress(), peer.getPort()));
        socketChannel.register(selector, SelectionKey.OP_CONNECT, new PeerConnection(socketChannel));
    }

    public void performIO() throws IOException {
        selector.select();

        for (Iterator i = selector.selectedKeys().iterator(); i.hasNext();) {
            SelectionKey key = (SelectionKey) i.next();
            i.remove();

            /* Disable the interest that we are processing */
            key.interestOps(key.interestOps() & ~key.readyOps());

            PeerConnection peerConnection = null;
            if (key.isAcceptable()) {
                SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                clientChannel.configureBlocking(false);
                peerConnection = new PeerConnection(clientChannel);
                clientChannel.register(selector, SelectionKey.OP_READ, peerConnection);
                fireConnected(peerConnection);

            } else if (key.isConnectable()) {
                SocketChannel clientChannel = (SocketChannel) key.channel();
                if (clientChannel.isConnectionPending()) {
                    if (clientChannel.finishConnect()) {
                        peerConnection = (PeerConnection) key.attachment();
                        fireConnected(peerConnection);
                    } else {
                        key.interestOps(SelectionKey.OP_CONNECT);
                    }
                }

            } else {
                peerConnection = (PeerConnection) key.attachment();
                if (key.isReadable()) {
                    Message message = peerConnection.performRead();
                    if (message != null) {
                        fireMessageReceived(peerConnection, message);
                    }
                }

                /* Read could have invalidated the key */
                if (key.isValid() && key.isWritable()) {
                    peerConnection.performWrite();
                }
            }

            if (peerConnection != null && key.isValid()) {
                int interestOps = SelectionKey.OP_READ | key.interestOps();
                if (peerConnection.wantsToWrite()) {
                    interestOps |= SelectionKey.OP_WRITE;
                }
                key.interestOps(interestOps);
            }
        }
    }

    private void fireMessageReceived(PeerConnection connection, Message message) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            PeerConnectionListener listener = (PeerConnectionListener) i.next();
            listener.received(connection, message);
        }
    }

    private void fireConnected(PeerConnection connection) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            PeerConnectionListener listener = (PeerConnectionListener) i.next();
            listener.connected(connection);
        }
    }

    public void addListener(PeerConnectionListener listener) {
        listeners.add(listener);
    }
}
