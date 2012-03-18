package blackbits.registry;

import blackbits.descriptor.Torrent;
import blackbits.descriptor.TorrentFile;
import org.apache.commons.io.FileUtils;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.io.File;

public class DiskTorrentRegistryTest extends MockObjectTestCase {
    private TorrentRegistry torrentRegistry;
    private Torrent torrent;
    private File registryStorage;

    protected void setUp() throws Exception {
        super.setUp();
        registryStorage = File.createTempFile("blackbits", ".tmp");
        registryStorage.delete();
        registryStorage.mkdirs();
        torrentRegistry = new DiskTorrentRegistry(registryStorage);
        torrent = new TorrentFile(new File("testdata", "multifile.torrent"));
        torrentRegistry.register(torrent);
    }

    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(registryStorage);
        super.tearDown();
    }

    public void testRegisterdTorrentIsPaused() throws Exception {
        assertTrue(torrentRegistry.getPaused().contains(torrent));
        assertFalse(torrentRegistry.getActive().contains(torrent));
    }

    public void testActivateTorrentUnPausesTheTorrent() throws Exception {
        torrentRegistry.activate(torrent);
        assertFalse(torrentRegistry.getPaused().contains(torrent));
        assertTrue(torrentRegistry.getActive().contains(torrent));
    }

    public void testPauseTorrentPausesTheTorrent() throws Exception {
        torrentRegistry.activate(torrent);
        torrentRegistry.pause(torrent);
        assertTrue(torrentRegistry.getPaused().contains(torrent));
        assertFalse(torrentRegistry.getActive().contains(torrent));
    }

    public void testCantRegisterAlreadyRegisteredTorrent() throws Exception {
        try {
            torrentRegistry.register(torrent);
            fail("Should not be albe to register already registered torrent");
        } catch (IllegalArgumentException shouldHappen) {
        }
    }

    public void testCantActivateUnregisteredTorrent() throws Exception {
        try {
            torrentRegistry.activate(new TorrentFile(new File("testdata", "singlefile.torrent")));
            fail();
        } catch (IllegalArgumentException shouldHappen) {
        }
    }

    public void testTorrentGetsNameFromDescriptor() throws Exception {
        assertEquals("Mandrakelinux-10.0-Community-Download", torrent.getName());
    }

    public void testRegistredTorrentIsPersistent() throws Exception {
        DiskTorrentRegistry diskTorrentRegistry = new DiskTorrentRegistry(registryStorage);
        assertTrue(diskTorrentRegistry.getPaused().contains(torrent));
        assertFalse(diskTorrentRegistry.getActive().contains(torrent));
    }

    public void testRegistredActivatedTorrentIsPersistent() throws Exception {
        torrentRegistry.activate(torrent);
        DiskTorrentRegistry diskTorrentRegistry = new DiskTorrentRegistry(registryStorage);
        assertFalse(diskTorrentRegistry.getPaused().contains(torrent));
        assertTrue(diskTorrentRegistry.getActive().contains(torrent));
    }

    public void testRegistryListenerCalledOnActivate() throws Exception {
        Mock mock = mock(RegistryListener.class);
        mock.expects(once()).method("registered").with(same(torrentRegistry), same(torrent));
        mock.expects(once()).method("activated").with(same(torrentRegistry), same(torrent));
        torrentRegistry.addListener((RegistryListener) mock.proxy());
        torrentRegistry.activate(torrent);
        mock.verify();
    }

    public void testRegistryListenerCalledOnPause() throws Exception {
        torrentRegistry.activate(torrent);
        Mock mock = mock(RegistryListener.class);
        mock.expects(once()).method("registered").with(same(torrentRegistry), same(torrent));
        mock.expects(once()).method("activated").with(same(torrentRegistry), same(torrent));
        mock.expects(once()).method("paused").with(same(torrentRegistry), same(torrent));
        torrentRegistry.addListener((RegistryListener) mock.proxy());
        torrentRegistry.pause(torrent);
        mock.verify();
    }

    public void testRegistryListenerCalledOnRegister() throws Exception {
        Mock mock = mock(RegistryListener.class);
        mock.expects(once()).method("registered").with(same(torrentRegistry), same(torrent));
        torrentRegistry.addListener((RegistryListener) mock.proxy());
        mock.verify();
    }

    public void testRegistryListenerUpdatedWithOldTorrents() throws Exception {
        Torrent torrent2 = new TorrentFile(new File("testdata", "singlefile.torrent"));
        torrentRegistry.register(torrent2);
        torrentRegistry.activate(torrent2);

        Mock mock = mock(RegistryListener.class);
        mock.expects(once()).method("registered").with(same(torrentRegistry), same(torrent));
        mock.expects(once()).method("registered").with(same(torrentRegistry), same(torrent2));
        mock.expects(once()).method("activated").with(same(torrentRegistry), same(torrent2));
        torrentRegistry.addListener((RegistryListener) mock.proxy());
        mock.verify();
    }
}
