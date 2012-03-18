package blackbits.bencoding;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class BDictionary extends BObject {
    private Map map = new LinkedHashMap();

    public void put(String key, BObject value) {
        map.put(key, value);
    }

    public int size() {
        return map.size();
    }

    public BObject get(String key) {
        return (BObject) map.get(key);
    }

    public Set keySet() {
        return map.keySet();
    }

    public void encode(OutputStream out) throws IOException {
        write(out, "d");
        for (Iterator i = keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            new BString(key).encode(out);
            get(key).encode(out);
        }
        write(out, "e");
    }
}
