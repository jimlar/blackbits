package blackbits.hash;

import java.io.IOException;
import java.util.Arrays;

public class SHAHash {
    private byte[] hash;

    public SHAHash(byte[] hash) {
        this.hash = hash;
    }

    public SHAHash(String hexString) {
        if (hexString.length() != 40) {
            throw new IllegalArgumentException("The hex string needs to be 40 characters (was " + hexString.length() + ")");
        }

        hash = new byte[20];
        for (int i = 0; i < hash.length; i++) {
            hash[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SHAHash)) return false;

        final SHAHash hash1 = (SHAHash) o;

        if (!Arrays.equals(hash, hash1.hash)) return false;

        return true;
    }

    public int hashCode() {
        return Arrays.hashCode(hash);
    }

    public String toString() {
        StringBuffer result = new StringBuffer("[SHAHash ");
        for (int i = 0; i < hash.length; i++) {
            byte b = hash[i];
            if (i != 0) {
                result.append(", ");
            }
            result.append("0x");
            result.append(Integer.toHexString(((char) b) & 0xff));
        }
        result.append(']');
        return result.toString();
    }

    public byte[] getBytes() {
        return hash;
    }
}
