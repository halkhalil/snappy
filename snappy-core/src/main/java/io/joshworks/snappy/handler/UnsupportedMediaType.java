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

package io.joshworks.snappy.handler;

import io.joshworks.snappy.parser.MediaTypes;
import io.undertow.util.HeaderValues;

import java.util.stream.Collectors;

/**
 * Created by Josh Gontijo on 3/15/17.
 */
public class UnsupportedMediaType extends Exception {

    private static final String MESSAGE_PREFIX = "Unsupported media type: ";
    public final HeaderValues headerValues;
    public final MediaTypes types;

    private UnsupportedMediaType(String message, HeaderValues headerValues, MediaTypes mediaTypes) {
        super(message);
        this.headerValues = headerValues;
        this.types = mediaTypes;
    }

    public static UnsupportedMediaType unsuportedMediaType(HeaderValues headerValues, MediaTypes types) {
        String typesString = headerValues.isEmpty() ? "" : headerValues.stream().collect(Collectors.joining(", "));
        typesString = "[" + typesString + "]";
        return new UnsupportedMediaType(MESSAGE_PREFIX + typesString, headerValues, types);
    }
}
