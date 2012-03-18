package blackbits.tracker;

import java.io.IOException;

public interface Tracker {
    TrackerResponse signalStart(long uploadedBytes, long downloadedBytes, long bytesLeftToDownload) throws IOException, TrackerCommunicationException;

    TrackerResponse signalCompleted(long uploadedBytes, long downloadedBytes) throws IOException, TrackerCommunicationException;

    TrackerResponse signalStopped(long uploadedBytes, long downloadedBytes, long bytesLeftToDownload) throws IOException, TrackerCommunicationException;

    TrackerResponse update(long uploadedBytes, long downloadedBytes, long bytesLeftToDownload) throws IOException, TrackerCommunicationException;

    boolean isGetStatusSupported();

    TorrentStatus getStatus() throws IOException;
}
