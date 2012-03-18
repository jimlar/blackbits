package blackbits.hash;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAHashCalculator {
    private InputStream in;

    public SHAHashCalculator(byte[] bytes) {
        this(new ByteArrayInputStream(bytes));
    }

    public SHAHashCalculator(InputStream in) {
        this.in = in;
    }

    public SHAHash calculate() throws IOException {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA1");

            byte[] readBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(readBuffer)) != -1) {
                sha.update(readBuffer, 0, bytesRead);
            }
            return new SHAHash(sha.digest());
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA1 is missing in your JRE", e);
        }
    }
}
