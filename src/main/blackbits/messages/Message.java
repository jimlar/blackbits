package blackbits.messages;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface Message {
    void write(ByteBuffer buffer) throws IOException;

    int getLength();

    void read(ByteBuffer buffer, int length) throws IOException;
}
