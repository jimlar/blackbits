package blackbits.bencoding;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class BObjectTest extends TestCase {
    public void testDecodedAndReencodedAreBinaryEqual() throws Exception {
        byte[] expectedBytes = IOUtils.toByteArray(new FileInputStream(new File("testdata", "multifile.torrent")));
        BObject decoded = new BDecoder(new ByteArrayInputStream(expectedBytes)).decodeNext();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        decoded.encode(out);
        byte[] actualBytes = out.toByteArray();

        assertEquals("Wrong length of recoded data", expectedBytes.length, actualBytes.length);
        for (int i = 0; i < expectedBytes.length; i++) {
            assertEquals("Byte @ " + i + " is not correct, expected "
                         + expectedBytes[i] + "(" + ((char) expectedBytes[i]) + ") but was "
                         + actualBytes[i] + "(" + ((char) actualBytes[i]) + ")", expectedBytes[i], actualBytes[i]);
        }
    }
}
