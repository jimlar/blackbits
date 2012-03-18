package blackbits.bencoding;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class AbstractBObjectTest extends TestCase {
    protected void assertEncoded(String expected, BObject bObject) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bObject.encode(out);
        assertEquals(expected, new String(out.toByteArray(), "ascii"));
    }
}
