package blackbits.bencoding;


public class BStringTest extends AbstractBObjectTest {
    public void testEncodeString() throws Exception {
        assertEncoded("4:spam", new BString("spam"));
    }
}
