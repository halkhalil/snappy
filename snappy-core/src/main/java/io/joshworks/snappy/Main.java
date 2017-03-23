/*
 * Copyright 2017 Josue Gontijo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.joshworks.snappy;

import io.joshworks.snappy.multipart.Part;
import io.joshworks.snappy.rest.MediaType;

import static io.joshworks.snappy.SnappyServer.*;

/**
 * Created by Josh Gontijo on 3/22/17.
 */
public class Main {

    public static void main(String[] args) {
        multipart("/upload", exchange -> {
            Part part = exchange.part("myFile");
            String name = part.file().name();
            long size = part.file().size();
            MediaType type = part.type();

            System.out.println(name + " => " + size + " => " + type);

        });

        start();
    }
}
