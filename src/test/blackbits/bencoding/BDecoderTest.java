package blackbits.bencoding;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

public class BDecoderTest extends TestCase {

    public void testDecodeInteger() throws Exception {
        BDecoder decoder = createDecoder("i4711e");
        assertEquals(new BLong(4711), decoder.decodeNext());
    }

    public void testDecodeString() throws Exception {
        BDecoder decoder = createDecoder("4:spam");
        assertEquals(new BString("spam"), decoder.decodeNext());
    }

    public void testDecodeEndOfListCharacterGivesException() throws Exception {
        BDecoder decoder = createDecoder("e");
        try {
            decoder.decodeNext();
            fail("Should get exception when reading stream containing end of list char");
        } catch (IllegalStateException shouldHappen) {
        }
    }

    public void testDecodeList() throws Exception {
        BDecoder decoder = createDecoder("l4:spam4:swapi4711ee");
        BList actualList = (BList) decoder.decodeNext();
        assertEquals(3, actualList.size());
        assertEquals(new BString("spam"), actualList.get(0));
        assertEquals(new BString("swap"), actualList.get(1));
        assertEquals(new BLong(4711), actualList.get(2));
    }

    public void testDecodeDictionary() throws Exception {
        BDecoder decoder = createDecoder("d4:spam4:swap3:eggi4711ee");
        BDictionary actual = (BDictionary) decoder.decodeNext();
        assertEquals(2, actual.size());
        assertEquals(new BString("swap"), actual.get("spam"));
        assertEquals(new BLong(4711), actual.get("egg"));
    }

    private BDecoder createDecoder(String data) throws UnsupportedEncodingException {
        return new BDecoder(new ByteArrayInputStream(data.getBytes("ascii")));
    }
}
