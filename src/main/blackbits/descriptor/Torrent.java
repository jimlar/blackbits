package blackbits.descriptor;

import blackbits.hash.SHAHash;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

public interface Torrent {

    URL getAnnounceUrl();

    String getName();

    long getPieceLength();

    List getPieceHashes();

    List getFiles();

    Date getCreationDate();

    SHAHash getInfoHash();

    String getComment();

    String getCreatedBy();

    void write(OutputStream out) throws IOException;

    int getNumberOfPieces();
}
