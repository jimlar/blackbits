package blackbits.descriptor;

import blackbits.bencoding.*;
import blackbits.hash.SHAHash;
import blackbits.hash.SHAHashCalculator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.*;
import java.net.URL;
import java.util.*;

public class TorrentFile implements Torrent {
    private byte[] descriptorData;

    private URL announceUrl;
    private String name;
    private long pieceLength;
    private List pieceHashes;
    private List files;
    private Date creationDate;
    private SHAHash infoHash;
    private String comment;
    private String createdBy;

    public TorrentFile(File file) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            descriptorData = IOUtils.toByteArray(in);
        } finally {
            IOUtils.closeQuietly(in);
        }
        try {
            in = new ByteArrayInputStream(descriptorData);
            BDecoder decoder = new BDecoder(in);
            BDictionary root = (BDictionary) decoder.decodeNext();
            announceUrl = new URL(((BString) root.get("announce")).getStringValue());

            BDictionary infoDictionary = (BDictionary) root.get("info");
            name = ((BString) infoDictionary.get("name")).getStringValue();
            pieceLength = ((BLong) infoDictionary.get("piece length")).longValue();
            pieceHashes = decodePieceHashes(infoDictionary);
            files = decodeFiles(infoDictionary);

            parseCreationDate(root);
            calculateInfoHash(infoDictionary);

            BString commentString = (BString) root.get("comment");
            if (commentString != null) {
                comment = commentString.getStringValue();
            }
            BString createdByString = (BString) root.get("created by");
            if (createdByString != null) {
                createdBy = createdByString.getStringValue();
            }

        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public URL getAnnounceUrl() {
        return announceUrl;
    }

    public String getName() {
        return name;
    }

    public long getPieceLength() {
        return pieceLength;
    }

    public List getPieceHashes() {
        return pieceHashes;
    }

    public List getFiles() {
        return files;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public SHAHash getInfoHash() {
        return infoHash;
    }

    public String getComment() {
        return comment;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void write(OutputStream out) throws IOException {
        out.write(descriptorData);
    }

    public int getNumberOfPieces() {
        return getPieceHashes().size();
    }

    private void parseCreationDate(BDictionary root) {
        BLong creatingDateSeconds = (BLong) root.get("creation date");
        if (creatingDateSeconds != null) {
            creationDate = new Date(creatingDateSeconds.longValue() * 1000L);
        }
    }

    private void calculateInfoHash(BDictionary infoDictionary) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        infoDictionary.encode(out);
        infoHash = new SHAHashCalculator(out.toByteArray()).calculate();
    }

    private List decodeFiles(BDictionary infoDictionary) {
        ArrayList files = new ArrayList();

        BList fileDictionaries = (BList) infoDictionary.get("files");
        if (fileDictionaries == null) {
            files.add(new FileDescriptor(new String[]{name}, ((BLong) infoDictionary.get("length")).longValue()));

        } else {
            for (Iterator i = fileDictionaries.iterator(); i.hasNext();) {
                BDictionary fileDictionary = (BDictionary) i.next();
                long length = ((BLong) fileDictionary.get("length")).longValue();

                BList pathList = (BList) fileDictionary.get("path");
                String[] path = new String[pathList.size() + 1];
                path[0] = name;

                for (ListIterator j = pathList.listIterator(); j.hasNext();) {
                    BString string = (BString) j.next();
                    path[j.previousIndex() + 1] = string.getStringValue();
                }
                files.add(new FileDescriptor(path, length));
            }
        }
        return files;
    }

    private List decodePieceHashes(BDictionary infoDictionary) {
        List hashes = new ArrayList();
        BString piecesString = (BString) infoDictionary.get("pieces");
        if (piecesString.length() % 20 != 0) {
            throw new IllegalArgumentException("The piece hashes are corrupt (length not a multiple of 20)");
        }
        byte[] piecesBytes = piecesString.getByteValue();
        for (int i = 0; i < piecesBytes.length; i += 20) {
            byte[] pieceBytes = new byte[20];
            System.arraycopy(piecesBytes, i, pieceBytes, 0, 20);
            SHAHash hash = new SHAHash(pieceBytes);
            hashes.add(hash);
        }
        return hashes;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TorrentFile)) return false;

        final TorrentFile torrentDescriptorFile = (TorrentFile) o;

        if (!infoHash.equals(torrentDescriptorFile.infoHash)) return false;

        return true;
    }

    public int hashCode() {
        return infoHash.hashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("infohash", infoHash).toString();
    }
}
