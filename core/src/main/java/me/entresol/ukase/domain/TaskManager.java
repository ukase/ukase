/*
 * Copyright (c) 2015 Konstantin Lepa <konstantin+ukase@lepabox.net>
 *
 * This file is part of Ukase.
 *
 * Ukase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package me.entresol.ukase.domain;

import org.springframework.stereotype.Service;
import org.springframework.util.*;

import java.util.*;
import java.util.concurrent.*;

@Service
public class TaskManager {
    private final ConcurrentMap<String, Task> registry = new ConcurrentHashMap<>();

    public void registerTask(Task task) {
        IdGenerator tokenGenerator = new AlternativeJdkIdGenerator();
        do {
            UUID uuid = tokenGenerator.generateId();
            String token = uuid.toString();
            task.setToken(token);
        } while (registry.putIfAbsent(task.getToken(), task) != null);
    }

    public void unregisterTask(String token) {
        registry.remove(token);
    }
}
