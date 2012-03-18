package blackbits.messages;

import java.io.IOException;
import java.nio.ByteBuffer;

public class HaveMessage implements Message {
    private int pieceIndex;

    public HaveMessage() {
    }

    public HaveMessage(int pieceIndex) {
        this.pieceIndex = pieceIndex;
    }

    public void write(ByteBuffer buffer) throws IOException {
        buffer.put((byte) 4);
        buffer.putInt(pieceIndex);
    }

    public int getLength() {
        return 4 + 1;
    }

    public void read(ByteBuffer buffer, int length) throws IOException {
        pieceIndex = buffer.getInt();
    }

    public int getPieceIndex() {
        return pieceIndex;
    }
}
