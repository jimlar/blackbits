package blackbits.bencoding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class BList extends BObject {
    private ArrayList elements = new ArrayList();

    public BList(BObject[] objects) {
        for (int i = 0; i < objects.length; i++) {
            add(objects[i]);
        }
    }

    public void add(BObject object) {
        elements.add(object);
    }

    public BList() {
    }

    public Iterator iterator() {
        return elements.iterator();
    }

    public int size() {
        return elements.size();
    }

    public BObject get(int index) {
        return (BObject) elements.get(index);
    }

    public ListIterator listIterator() {
        return elements.listIterator();
    }

    public void encode(OutputStream out) throws IOException {
        write(out, "l");
        for (Iterator i = iterator(); i.hasNext();) {
            ((BObject) i.next()).encode(out);
        }
        write(out, "e");
    }
}
