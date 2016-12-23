/*
 * Copyright (c) 2016 Konstantin Lepa <konstantin+ukase@lepabox.net>
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

package com.github.ukase.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class TTLChecker implements Runnable {
    private final Thread currentThread;
    private boolean flag = true;

    private final AsyncManager manager;

    @Autowired
    public TTLChecker(AsyncManager manager) {
        this.manager = manager;
        currentThread = new Thread(this, "PDF bulks TTL checker");
    }

    @PostConstruct
    public void startChecker() {
        currentThread.start();
    }

    @PreDestroy
    public void dropFlag() {
        flag = false;
        currentThread.interrupt();
    }

    @Override
    public void run() {
        while (flag) {
            try {
                Thread.sleep(60 * 1000L);
            } catch (InterruptedException e) {
                //ignore
            }
            manager.clearOldPDFs();
        }
    }
}
