package blackbits.messages;

import java.io.IOException;
import java.nio.ByteBuffer;

public class CancelMessage implements Message {
    private int pieceIndex;
    private int blockOffset;
    private int blockLength;

    public CancelMessage(int pieceIndex, int blockOffset, int blockLength) {
        this.pieceIndex = pieceIndex;
        this.blockOffset = blockOffset;
        this.blockLength = blockLength;
    }

    public CancelMessage() {
    }

    public void write(ByteBuffer buffer) throws IOException {
        buffer.put((byte) 8);
        buffer.putInt(pieceIndex);
        buffer.putInt(blockOffset);
        buffer.putInt(blockLength);
    }

    public int getLength() {
        return 13;
    }

    public void read(ByteBuffer buffer, int length) throws IOException {
        pieceIndex = buffer.getInt();
        blockOffset = buffer.getInt();
        blockLength = buffer.getInt();
    }

    public int getPieceIndex() {
        return pieceIndex;
    }


    public int getBlockOffset() {
        return blockOffset;
    }

    public int getBlockLength() {
        return blockLength;
    }
}
