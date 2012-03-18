package blackbits.bencoding;

import java.io.IOException;
import java.io.OutputStream;

public class BLong extends BObject {
    private Long value;

    public BLong(String value) {
        this.value = new Long(value);
    }

    public BLong(long value) {
        this.value = new Long(value);
    }

    public long longValue() {
        return value.longValue();
    }

    public int intValue() {
        return (int) longValue();
    }

    public void encode(OutputStream out) throws IOException {
        write(out, "i" + value + "e");
    }

    public String toString() {
        return value.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BLong)) return false;

        BLong bLong = (BLong) o;
        return value.equals(bLong.value);
    }

    public int hashCode() {
        return value.hashCode();
    }
}
