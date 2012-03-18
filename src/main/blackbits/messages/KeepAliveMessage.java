package blackbits.messages;

import java.io.IOException;
import java.nio.ByteBuffer;

public class KeepAliveMessage implements Message {
    public void write(ByteBuffer buffer) throws IOException {
    }

    public int getLength() {
        return 0;
    }

    public void read(ByteBuffer buffer, int length) throws IOException {
    }


}
