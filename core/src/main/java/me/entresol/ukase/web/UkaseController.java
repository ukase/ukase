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

package me.entresol.ukase.web;

import me.entresol.ukase.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UkaseController {
    @Autowired
    private TaskManager taskManager;

    @RequestMapping(value = "/html", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> generateHtml(Task.Payload payload) {
        Task task = new Task(payload);
        taskManager.registerTask(task);
        task.setStatus(Task.Status.DONE);
        return ResponseEntity.ok(task);
    }

    @RequestMapping(value = "/status/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> getStatus(@PathVariable("uuid") String uuid) {
        return ResponseEntity.ok(new Task(Task.Status.IN_PROGRESS));
    }

    @RequestMapping(value = "/html/{uuid}")
    public ResponseEntity<?> getHtml(@PathVariable("uuid") String id) {
        if (id.equals("fake-uuid")) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML).body("<html><body><h1>Hello</h1></body></html>");
        }
        return ResponseEntity.notFound().build();
    }
}
