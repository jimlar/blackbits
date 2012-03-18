package blackbits.bencoding;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BObject {

    public abstract void encode(OutputStream out) throws IOException;

    protected void write(OutputStream out, String s) throws IOException {
        out.write(s.getBytes("ascii"));
    }
}
