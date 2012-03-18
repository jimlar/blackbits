package blackbits.registry;

import blackbits.descriptor.Torrent;

import java.util.Set;

public interface TorrentRegistry {

    void register(Torrent descriptor);

    void activate(Torrent torrent);

    void pause(Torrent torrent);

    Set getPaused();

    Set getActive();

    void addListener(RegistryListener listener);

}
