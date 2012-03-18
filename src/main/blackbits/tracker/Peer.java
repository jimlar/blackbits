package blackbits.tracker;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Peer {
    private String id;
    private int port;
    private String address;

    public Peer(String id, int port, String address) {
        this.id = id;
        this.port = port;
        this.address = address;
        if (id.length() != 20) {
            throw new IllegalArgumentException("The peer id has to have a length of 20");
        }
    }

    public String getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public String toString() {
        return new ToStringBuilder(this).append("address", address).append("port", port).append("id", id).toString();
    }
}
