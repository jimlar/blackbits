package blackbits.registry;

import blackbits.descriptor.Torrent;
import blackbits.descriptor.TorrentFile;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class DiskTorrentRegistry implements TorrentRegistry {
    private File pausedDir;
    private File activeDir;

    private Map activeFilesByDescriptor = new HashMap();
    private Map pausedFilesByDescriptor = new HashMap();
    private Set listeners = new HashSet();

    public DiskTorrentRegistry(File storage) throws IOException {
        pausedDir = new File(storage, "paused");
        pausedDir.mkdirs();
        activeDir = new File(storage, "active");
        activeDir.mkdirs();
        readAndAddDescriptors(pausedDir, pausedFilesByDescriptor);
        readAndAddDescriptors(activeDir, activeFilesByDescriptor);
    }

    private void readAndAddDescriptors(File dir, Map addTo) throws IOException {
        File[] files = dir.listFiles();
        for (int i = 0; files != null && i < files.length; i++) {
            File file = files[i];
            addTo.put(new TorrentFile(file), file);
        }
    }

    public void register(Torrent torrent) {
        if (activeFilesByDescriptor.containsKey(torrent) || pausedFilesByDescriptor.containsKey(torrent)) {
            throw new IllegalArgumentException("Torrent is already registered: " + torrent);
        }
        File file = getNextFreeName(pausedDir);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            torrent.write(out);
        } catch (IOException e) {
            throw new RegistryException("Could not register torrent", e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        pausedFilesByDescriptor.put(torrent, file);
    }

    public void activate(Torrent torrent) {
        File file = (File) pausedFilesByDescriptor.remove(torrent);
        if (file == null) {
            throw new IllegalArgumentException("Cant activate unregistered torrent: " + torrent);
        }
        File newFile = getNextFreeName(activeDir);
        file.renameTo(newFile);
        activeFilesByDescriptor.put(torrent, newFile);
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            RegistryListener listener = (RegistryListener) i.next();
            listener.activated(this, torrent);
        }
    }

    public void pause(Torrent torrent) {
        File file = (File) activeFilesByDescriptor.remove(torrent);
        if (file == null) {
            throw new IllegalArgumentException("Cant pause inactive torrent: " + torrent);
        }
        File newFile = getNextFreeName(pausedDir);
        file.renameTo(newFile);
        pausedFilesByDescriptor.put(torrent, newFile);
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            RegistryListener listener = (RegistryListener) i.next();
            listener.paused(this, torrent);
        }
    }

    private File getNextFreeName(File dir) {
        File file = null;
        int i = 0;
        while (file == null || file.exists()) {
            file = new File(dir, ++i + ".torrent");
        }
        return file;
    }

    public Set getPaused() {
        return Collections.unmodifiableSet(pausedFilesByDescriptor.keySet());
    }

    public Set getActive() {
        return Collections.unmodifiableSet(activeFilesByDescriptor.keySet());
    }

    public void addListener(RegistryListener listener) {
        listeners.add(listener);
        for (Iterator i = pausedFilesByDescriptor.keySet().iterator(); i.hasNext();) {
            Torrent torrent = (Torrent) i.next();
            listener.registered(this, torrent);
        }
        for (Iterator i = activeFilesByDescriptor.keySet().iterator(); i.hasNext();) {
            Torrent torrent = (Torrent) i.next();
            listener.registered(this, torrent);
        }
        for (Iterator i = activeFilesByDescriptor.keySet().iterator(); i.hasNext();) {
            Torrent torrent = (Torrent) i.next();
            listener.activated(this, torrent);
        }
    }
}
