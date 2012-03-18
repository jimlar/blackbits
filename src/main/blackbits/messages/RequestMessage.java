package blackbits.messages;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RequestMessage implements Message {
    private int pieceIndex;
    private int blockOffset;
    private int blockLength;

    public RequestMessage(int pieceIndex, int blockOffset, int blockLength) {
        this.pieceIndex = pieceIndex;
        this.blockOffset = blockOffset;
        this.blockLength = blockLength;
    }

    public RequestMessage() {
    }

    public void write(ByteBuffer buffer) throws IOException {
        buffer.put((byte) 6);
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

    public String toString() {
        return new ToStringBuilder(this).append("piece", pieceIndex).append("offset", blockOffset).append("size", blockLength).toString();
    }
}
