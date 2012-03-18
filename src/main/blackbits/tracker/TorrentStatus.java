package blackbits.tracker;

public class TorrentStatus {
    private int seeders;
    private int leechers;
    private int numberOfDownloads;

    public TorrentStatus(int seeders, int leechers, int numberOfDownloads) {
        this.seeders = seeders;
        this.leechers = leechers;
        this.numberOfDownloads = numberOfDownloads;
    }

    public int getSeeders() {
        return seeders;
    }

    public int getLeechers() {
        return leechers;
    }

    public int getNumberOfDownloads() {
        return numberOfDownloads;
    }
}
