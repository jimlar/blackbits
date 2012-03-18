package blackbits.bencoding;


public class BLongTest extends AbstractBObjectTest {
    public void testEncodeLong() throws Exception {
        assertEncoded("i123e", new BLong(123));
    }
}
