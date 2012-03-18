package blackbits.descriptor;

import blackbits.hash.SHAHash;
import junit.framework.TestCase;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TorrentFileTest extends TestCase {
    private Torrent torrent;

    protected void setUp() throws Exception {
        super.setUp();
        torrent = new TorrentFile(new File("testdata", "multifile.torrent"));
    }

    public void testAnnounceUrl() throws Exception {
        assertEquals(new URL("http://torrent.mandrakesoft.com:6969/announce/mandrakelinux/4116e0a0d7fcc354659d8932a8cdd92367adbbb8"),
                     torrent.getAnnounceUrl());
    }

    public void testName() throws Exception {
        assertEquals("Mandrakelinux-10.0-Community-Download",
                     torrent.getName());
    }

    public void testPieceLength() throws Exception {
        assertEquals(256 * 1024, torrent.getPieceLength());
    }

    public void testGetInfoHash() throws Exception {
        assertEquals(new SHAHash("f73a14a622ac09c4cfaa9f40ec12b883ece1cf9d"), torrent.getInfoHash());
    }


    public void testCreationDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2004);
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 4);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 16);
        calendar.set(Calendar.SECOND, 24);
        calendar.set(Calendar.MILLISECOND, 0);

        assertEquals(calendar.getTime(), torrent.getCreationDate());
    }


    public void testGetPieceHashes() throws Exception {
        List pieceHashes = torrent.getPieceHashes();
        assertEquals(8340, pieceHashes.size());
        assertEquals(new SHAHash(new byte[]{0x7b, 0x74, (byte) 0xce, (byte) 0xf8, 0x3d,
                                            0x1a, 0x67, 0x14, 0x19, (byte) 0x95,
                                            0x56, 0x7a, 0x6b, 0x02, 0x7e,
                                            (byte) 0x9f, 0x1f, 0x44, (byte) 0xf9, (byte) 0xd1}),
                     pieceHashes.get(0));
    }

    public void testGetFiles() throws Exception {
        List files = torrent.getFiles();
        assertEquals(4, files.size());

        assertFileProperties(0, 728651776, new String[]{"Mandrakelinux-10.0-Community-Download", "Mandrakelinux-10.0-Community-Download-CD1.i586.iso"});
        assertFileProperties(1, 728797184, new String[]{"Mandrakelinux-10.0-Community-Download", "Mandrakelinux-10.0-Community-Download-CD2.i586.iso"});
        assertFileProperties(2, 728829952, new String[]{"Mandrakelinux-10.0-Community-Download", "Mandrakelinux-10.0-Community-Download-CD3.i586.iso"});
        assertFileProperties(3, 491, new String[]{"Mandrakelinux-10.0-Community-Download", "Mandrakelinux-10.0-Community-Download.md5sums.asc"});
    }

    public void testSingleFileTorrent() throws Exception {
        torrent = new TorrentFile(new File("testdata", "singlefile.torrent"));
        assertEquals("yarrow-i386-disc1.iso", torrent.getName());

        List files = torrent.getFiles();
        assertEquals(1, files.size());
        assertFileProperties(0, 660340736, new String[]{"yarrow-i386-disc1.iso"});
        assertEquals(512 * 1024, torrent.getPieceLength());

        List pieceHashes = torrent.getPieceHashes();
        assertEquals(1260, pieceHashes.size());
        assertEquals(new SHAHash("8a4852f1f1c70d84e283ab009b4f67221367d098"),
                     pieceHashes.get(0));
    }

    private void assertFileProperties(int fileIndex, int size, String[] path) {
        List files = torrent.getFiles();
        assertEquals(size, ((FileDescriptor) files.get(fileIndex)).getLength());
        assertEquals(path, ((FileDescriptor) files.get(fileIndex)).getPath());
    }

    public void assertEquals(String[] sa1, String[] sa2) {
        assertEquals(Arrays.asList(sa1), Arrays.asList(sa2));
    }
}
