package blackbits.messages;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PieceMessage implements Message {
    private int pieceIndex;
    private int blockOffset;
    private byte[] data;

    public PieceMessage(int pieceIndex, int blockOffset, byte[] data) {
        this.pieceIndex = pieceIndex;
        this.blockOffset = blockOffset;
        this.data = data;
    }

    public PieceMessage() {
    }

    public void write(ByteBuffer buffer) throws IOException {
        buffer.put((byte) 7);
        buffer.putInt(pieceIndex);
        buffer.putInt(blockOffset);
        buffer.put(data);
    }

    public int getLength() {
        return data.length + 9;
    }

    public void read(ByteBuffer buffer, int length) throws IOException {
        pieceIndex = buffer.getInt();
        blockOffset = buffer.getInt();
        data = new byte[length - 9];
        buffer.get(data);
    }

    public int getPieceIndex() {
        return pieceIndex;
    }

    public int getBlockOffset() {
        return blockOffset;
    }

    public byte[] getData() {
        return data;
    }

    public String toString() {
        return new ToStringBuilder(this).append("piece", pieceIndex).append("offset", blockOffset).append("size", data.length).toString();
    }
}
