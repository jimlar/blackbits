package blackbits.bencoding;


public class BDictionaryTest extends AbstractBObjectTest {
    public void testEncodeDictionaryWithStringAndLong() throws Exception {
        BDictionary dictionary = new BDictionary();
        dictionary.put("spam", new BLong(4711));
        assertEncoded("d4:spami4711ee", dictionary);
    }
}
