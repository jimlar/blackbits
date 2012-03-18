package blackbits.bencoding;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * This is needed instead of ordinary strings since the BEncoding (at least the one used in bittorrent) is rather wicked:
 * - The Bencoded files are ascii but the strings inside are UTF-8 AND the strings can
 * be 'byte strings' which cannot really be used as string but rather byte arrays.
 */
public class BString extends BObject {
    private String stringValue;
    private byte[] byteValue;

    public BString(String stringValue) {
        this.stringValue = stringValue;
        try {
            this.byteValue = stringValue.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Your JRE does not have UTF-8 encoding? It is broken!", e);
        }
    }

    public BString(byte[] byteValue) {
        this.byteValue = byteValue;
        try {
            this.stringValue = new String(byteValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Your JRE does not have UTF-8 encoding? It is broken!", e);
        }
    }

    public String getStringValue() {
        return stringValue;
    }

    public byte[] getByteValue() {
        return byteValue;
    }

    public void encode(OutputStream out) throws IOException {
        write(out, length() + ":");
        out.write(getByteValue());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BString)) return false;

        BString bString = (BString) o;
        if (!Arrays.equals(byteValue, bString.byteValue)) return false;

        return true;
    }

    public int hashCode() {
        return Arrays.hashCode(byteValue);
    }

    public String toString() {
        return stringValue;
    }

    public int length() {
        return byteValue.length;
    }
}
