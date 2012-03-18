package blackbits.tracker;

import blackbits.bencoding.*;
import blackbits.hash.SHAHash;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TrackerImpl implements Tracker {
    private URL announceUrl;
    private SHAHash infoHash;
    private Peer localPeer;
    private HttpClient httpClient;
    private static final String ANNOUNCE = "announce";

    public TrackerImpl(Peer localPeer, URL announceUrl, SHAHash infoHash) {
        this.announceUrl = announceUrl;
        this.infoHash = infoHash;
        this.localPeer = localPeer;
        try {
            httpClient = new HttpClient();
            HostConfiguration configuration = new HostConfiguration();
            configuration.setHost(new URI(announceUrl.toExternalForm()));
            httpClient.setHostConfiguration(configuration);
        } catch (URIException e) {
            throw new RuntimeException("Invalid URL", e);
        }
    }

    public TrackerResponse signalStart(long uploadedBytes, long downloadedBytes, long bytesLeftToDownload) throws IOException, TrackerCommunicationException {
        return sendEvent(uploadedBytes, downloadedBytes, bytesLeftToDownload, "started");
    }

    public TrackerResponse signalCompleted(long uploadedBytes, long downloadedBytes) throws IOException, TrackerCommunicationException {
        return sendEvent(uploadedBytes, downloadedBytes, 0, "completed");
    }

    public TrackerResponse signalStopped(long uploadedBytes, long downloadedBytes, long bytesLeftToDownload) throws IOException, TrackerCommunicationException {
        return sendEvent(uploadedBytes, downloadedBytes, bytesLeftToDownload, "stopped");
    }

    public TrackerResponse update(long uploadedBytes, long downloadedBytes, long bytesLeftToDownload) throws IOException, TrackerCommunicationException {
        return sendEvent(uploadedBytes, downloadedBytes, bytesLeftToDownload, null);
    }

    private TrackerResponse sendEvent(long uploadedBytes, long downloadedBytes, long bytesLeftToDownload, String event) throws IOException, TrackerCommunicationException {
        GetMethod method = new GetMethod(announceUrl.getFile());
        method.setRequestHeader("User-Agent", "BlackBits/0.1");
        StringBuffer queryString = new StringBuffer(announceUrl.getQuery() == null ? "" : announceUrl.getQuery());
        queryString.append("info_hash=" + URLUtils.encode(infoHash.getBytes()));
        queryString.append("&peer_id=" + URLUtils.encode(localPeer.getId()));
        queryString.append("&port=" + localPeer.getPort());
        queryString.append("&uploaded=" + uploadedBytes);
        queryString.append("&downloaded=" + downloadedBytes);
        queryString.append("&left=" + bytesLeftToDownload);
        if (event != null) {
            queryString.append("&event=" + event);
        }
        if (localPeer.getAddress() != null) {
            queryString.append("&ip=" + localPeer.getAddress());
        }
        method.setQueryString(queryString.toString());
        httpClient.executeMethod(method);
        return decodeResponse(method);
    }

    private TrackerResponse decodeResponse(GetMethod method) throws IOException, TrackerCommunicationException {
        BDecoder decoder = new BDecoder(method.getResponseBodyAsStream());

        BDictionary dictionary = (BDictionary) decoder.decodeNext();
        if (dictionary.get("failure reason") != null) {
            BString failureReason = (BString) dictionary.get("failure reason");
            throw new TrackerCommunicationException(failureReason.getStringValue());
        }

        int seeders = getInt(dictionary, "complete");
        int leechers = getInt(dictionary, "incomplete");
        int updateInterval = getInt(dictionary, "interval");
        BList peersList = (BList) dictionary.get("peers");
        List peers = new ArrayList();

        for (Iterator i = peersList.iterator(); i.hasNext();) {
            BDictionary peerDictionary = (BDictionary) i.next();
            peers.add(new Peer(((BString) peerDictionary.get("peer id")).getStringValue(),
                               ((BLong) peerDictionary.get("port")).intValue(),
                               ((BString) peerDictionary.get("ip")).getStringValue()));

        }
        return new TrackerResponse(seeders, leechers, updateInterval, peers);
    }

    private int getInt(BDictionary dictionary, String key) {
        BObject bObject = dictionary.get(key);
        if (!(bObject instanceof BLong)) {
            return 0;
        }
        return (int) ((BLong) bObject).longValue();
    }

    public boolean isGetStatusSupported() {
        String path = announceUrl.getFile();
        int i = path.lastIndexOf('/');
        if (i == -1) {
            return false;
        }
        return path.substring(i + 1).startsWith(ANNOUNCE);
    }

    /**
     * AKA scraping
     */
    public TorrentStatus getStatus() throws IOException {
        if (!isGetStatusSupported()) {
            throw new IllegalStateException("Get status (scraping) not supported on this tracker");
        }
        String path = announceUrl.getFile();
        int i = path.lastIndexOf('/');
        path = path.substring(0, i + 1) + "scrape" + path.substring(i + ANNOUNCE.length() + 1);

        GetMethod method = new GetMethod(path);
        method.setRequestHeader("User-Agent", "BlackBits/0.1");
        StringBuffer queryString = new StringBuffer(announceUrl.getQuery() == null ? "" : announceUrl.getQuery());
        queryString.append("info_hash=" + URLUtils.encode(infoHash.getBytes()));
        method.setQueryString(queryString.toString());
        httpClient.executeMethod(method);

        BDecoder decoder = new BDecoder(method.getResponseBodyAsStream());
        BDictionary dictionary = (BDictionary) decoder.decodeNext();
        BDictionary files = (BDictionary) dictionary.get("files");
        String key = (String) files.keySet().iterator().next();
        BDictionary info = (BDictionary) files.get(key);
        BLong seeders = (BLong) info.get("complete");
        BLong leechers = (BLong) info.get("incomplete");
        BLong numberOfDownloads = (BLong) info.get("downloaded");
        return new TorrentStatus(seeders.intValue(), leechers.intValue(), numberOfDownloads.intValue());
    }
}
