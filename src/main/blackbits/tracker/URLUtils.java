package blackbits.tracker;

import java.io.UnsupportedEncodingException;

public class URLUtils {
    public static String encode(String s) throws UnsupportedEncodingException {
        return encode(s.getBytes("ascii"));
    }

    public static String encode(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            char chr = (char) (0xff & (char) b);
            if (needsEncoding(chr)) {
                buffer.append('%');
                if (chr < 16) {
                    buffer.append('0');
                }
                buffer.append(Integer.toHexString(chr).toUpperCase());

            } else {
                buffer.append(chr);
            }
        }

        return buffer.toString();
    }

    private static boolean needsEncoding(char chr) {
        if (chr >= '0' && chr <= '9') {
            return false;
        }
        if (chr >= 'a' && chr <= 'z') {
            return false;
        }
        if (chr >= 'A' && chr <= 'Z') {
            return false;
        }
        switch(chr) {
            case '$':
            case '-':
            case '_':
            case '.':
            case '+':
            case '!':
            case '*':
            case '\'':
                return false;
        }

        return true;
    }
}
