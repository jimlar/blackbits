package blackbits.messages;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * Used to initially say wich pieces you have bit one is piece index 0 and if set the client has the piece
 */
public class BitFieldMessage implements Message {
    private int length;
    private BitSet bitSet;

    public BitFieldMessage() {
    }

    public BitFieldMessage(int numberOfPieces) {
        this.length = numberOfPieces / 8 + (numberOfPieces % 8 != 0 ? 1 : 0);
        this.bitSet = new BitSet(length * 8);
    }


    public void write(ByteBuffer buffer) throws IOException {
        buffer.put((byte) 5);
        for (int i = 0; i < length; i++) {
            byte b = 0;
            for (int j = 0; j < 8; j++) {
                b = (byte) (b << 1);
                if (havePiece(i * 8 + j)) {
                    b += 1;
                }
            }
            buffer.put(b);
        }
    }

    public int getLength() {
        return length + 1;
    }

    public void read(ByteBuffer buffer, int length) throws IOException {
        this.length = length - 1;
        bitSet = new BitSet(this.length * 8);

        for (int i = 0; i < this.length; i++) {
            byte b = buffer.get();
            for (int j = 0; j < 8; j++) {
                setHave(i * 8 + j, ((b >> (7 - j)) & 1) == 1);
            }
        }
    }

    public boolean havePiece(int pieceIndex) {
        return bitSet.get(pieceIndex);
    }

    public void setHave(int pieceIndex, boolean havePiece) {
        bitSet.set(pieceIndex, havePiece);
    }

    public String toString() {
        return new ToStringBuilder(this).append("length", length).append("bits", bitSet).toString();
    }
}
