package blackbits.tracker;

import java.util.List;

public class TrackerResponse {
    private int seeders;
    private int leechers;
    private int updateInterval;
    private List peers;

    public TrackerResponse(int seeders, int leechers, int updateInterval, List peers) {
        this.seeders = seeders;
        this.leechers = leechers;
        this.updateInterval = updateInterval;
        this.peers = peers;
    }

    public int getNumberOfSeeders() {
        return seeders;
    }

    public int getNumberOfLeechers() {
        return leechers;
    }

    public int getUpdateIntervalSeconds() {
        return updateInterval;
    }

    public List getPeers() {
        return peers;
    }
}
