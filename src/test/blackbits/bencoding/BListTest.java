package blackbits.bencoding;


public class BListTest extends AbstractBObjectTest {
    public void testEncodeListOfStringAndLong() throws Exception {
        assertEncoded("l4:spami4711ee", new BList(new BObject[]{new BString("spam"), new BLong(4711)}));
    }
}
