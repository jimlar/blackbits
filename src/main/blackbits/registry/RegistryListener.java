package blackbits.registry;

import blackbits.descriptor.Torrent;

public interface RegistryListener {
    void registered(TorrentRegistry registry, Torrent torrent);

    void activated(TorrentRegistry registry, Torrent torrent);

    void paused(TorrentRegistry registry, Torrent torrent);
}
