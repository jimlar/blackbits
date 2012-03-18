package blackbits.messages;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NotInterestedMessage implements Message {
    public void write(ByteBuffer buffer) throws IOException {
        buffer.put((byte) 3);
    }

    public int getLength() {
        return 1;
    }

    public void read(ByteBuffer buffer, int length) throws IOException {
    }
}
