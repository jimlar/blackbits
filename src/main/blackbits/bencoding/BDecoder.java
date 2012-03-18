package blackbits.bencoding;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class BDecoder {
    private PushbackInputStream in;

    public BDecoder(InputStream in) {
        this.in = new PushbackInputStream(in, 1);
    }

    public BObject decodeNext() throws IOException {
        return decodeNext(false);
    }

    private BObject decodeNext(boolean returnNullOnEndChar) throws IOException {
        int b = in.read();
        switch (b) {
            case 'i':
                return decodeLong();
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                in.unread(b);
                return decodeString();
            case 'l':
                return decodeList();
            case 'd':
                return decodeDictionary();
            case 'e':
                if (returnNullOnEndChar) {
                    return null;
                }
        }
        throw new IllegalStateException("Unexpected data on stream: '" + (char) b + "'");

    }

    private BDictionary decodeDictionary() throws IOException {
        BDictionary result = new BDictionary();
        while (true) {
            BString key = (BString) decodeNext(true);
            if (key == null) {
                break;
            }
            BObject value = decodeNext();
            result.put(key.getStringValue(), value);
        }
        return result;
    }

    private BList decodeList() throws IOException {
        BList result = new BList();
        BObject o;
        while ((o = decodeNext(true)) != null) {
            result.add(o);
        }
        return result;
    }

    private BString decodeString() throws IOException {
        int length = Integer.parseInt(readUpToChar(':'));

        byte[] stringData = new byte[length];
        for (int i = 0; i < length; i++) {
            stringData[i] = (byte) in.read();
        }
        return new BString(stringData);
    }

    private BLong decodeLong() throws IOException {
        return new BLong(readUpToChar('e'));
    }

    private String readUpToChar(char chr) throws IOException {
        StringBuffer buffer = new StringBuffer();
        int i;
        while ((i = in.read()) != chr) {
            if (i == -1) {
                throw new EOFException("Unexpected end of stream");
            }
            buffer.append((char) i);
        }
        return buffer.toString();
    }
}
