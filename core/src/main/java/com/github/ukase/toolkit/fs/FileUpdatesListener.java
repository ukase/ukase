/*
 * Copyright (c) 2015 Konstantin Lepa <konstantin+ukase@lepabox.net>
 *
 * This file is part of Ukase.
 *
 *  Ukase is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ukase.toolkit.fs;

import com.github.ukase.toolkit.SourceListener;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Slf4j
class FileUpdatesListener {
    private final Map<WatchKey,Path> keys;
    private final WatchService watcher;
    private final FileVisitor visitor;
    private boolean flag = true;
    private final Queue<SourceListener> listeners = new ConcurrentLinkedQueue<>();

    FileUpdatesListener(File root) throws IOException {
        this.keys = new HashMap<>();
        this.watcher = Paths.get(root.toURI()).getFileSystem().newWatchService();
        this.visitor = new FileVisitor();
        Files.walkFileTree(root.toPath(), visitor);
        new Thread(new Watcher(), getClass().getName() + "-thread").start();
    }

    void registerListener(SourceListener listener) {
        listeners.add(listener);
    }

    void stopNear() {
        flag = false;
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    private class FileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            register(dir);
            return FileVisitResult.CONTINUE;
        }
    }

    private class Watcher implements Runnable {
        @Override
        public void run() {
            while (flag) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException e) {
                    return;
                }

                processKey(key);

                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);
                    if (keys.isEmpty()) {
                        stopNear();
                    }
                }
            }
        }

        private void processKey(WatchKey key) {
            Path dir = keys.get(key);
            if (dir == null) {
                log.warn("WatchKey is not recognized: " + key);
                return;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                if (kind == OVERFLOW) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path name = ev.context();
                Path child = dir.resolve(name);

                notifyListeners(child.toFile().getName());
                registerNewDirectories(kind, child);
            }
        }

        private void registerNewDirectories(WatchEvent.Kind kind, Path child) {
            if (kind == ENTRY_CREATE) {
                try {
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        Files.walkFileTree(child, visitor);
                    }
                } catch (IOException x) {
                    log.warn("Some problem with registering watcher to newly created directory" + child);
                }
            }
        }

        private void notifyListeners(String resourceName) {
            SourceListener listener;
            while ((listener = listeners.poll()) != null) {
                listener.resourceUpdated(resourceName);
            }
        }
    }
}
