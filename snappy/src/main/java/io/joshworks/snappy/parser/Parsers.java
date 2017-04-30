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

package io.joshworks.snappy.parser;

import io.joshworks.snappy.rest.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static io.joshworks.snappy.SnappyServer.*;

/**
 * Created by josh on 3/6/17.
 */
public class Parsers {

    private static final Logger logger = LoggerFactory.getLogger(LOGGER_NAME);

    private static final Map<MediaType, Parser> available = new HashMap<>();
    private static final Set<MediaType> mostSpecificOrderedParsers = new TreeSet<>(new MostSpecificMediaTypeComparator());

    private Parsers() {

    }

    /**
     * @param parser The {@link Parser to be registered}
     * @throws IllegalArgumentException if a null instance or no media type is provided
     */
    public static void register(Parser parser) {
        if (parser == null || parser.mediaType() == null || parser.mediaType().isEmpty()) {
            throw new IllegalArgumentException("Invalid parser, media type not specified, or null instance");
        }
        logger.info("Registering Parser '{}' for type {}", parser.getClass().getSimpleName(), parser.mediaType().toString());
        parser.mediaType().forEach(mt -> {
            available.put(mt, parser);
            mostSpecificOrderedParsers.add(mt);
        });

        for(MediaType mt : mostSpecificOrderedParsers) {
            System.out.println(mt);
        }

    }

    /**
     * @param contentTypes The accept types by the client
     * @return The {@link Parser} for the first match, if no media type is provided, the default {@link JsonParser}
     * @throws ParserNotFoundException If parser is not found
     */
    public static Parser find(List<String> contentTypes) throws ParserNotFoundException {
        List<MediaType> types = new ArrayList<>();
        for (String ct : contentTypes) {
            types.add(MediaType.valueOf(ct));
        }
        return findByType(new HashSet<>(types));
    }

    /**
     * @param contentType The accepted types
     * @return The {@link Parser} for the first match.
     * @throws ParserNotFoundException If parser is not found
     */
    public static Parser getParser(MediaType contentType) throws ParserNotFoundException {
        return findByType(new HashSet<>(Collections.singletonList(contentType)));
    }

    public static Parser getParser(String contentType) {
        return getParser(MediaType.valueOf(contentType));
    }

    private static Parser findByType(Set<MediaType> contentTypes) throws ParserNotFoundException {
        if (contentTypes != null && !contentTypes.isEmpty()) {
            for (MediaType registredType : mostSpecificOrderedParsers) {
                for (MediaType acceptType : contentTypes) {
                    if (registredType.isCompatible(acceptType)) {
                        return available.get(registredType);
                    }
                }
            }
        }

        if (contentTypes != null) {
            throw new ParserNotFoundException(contentTypes.stream().map(MediaType::toString).toArray(String[]::new));
        }
        throw new ParserNotFoundException("[NO MEDIA TYPE]");
    }

    private static class MostSpecificMediaTypeComparator implements Comparator<MediaType>{

        @Override
        public int compare(MediaType first, MediaType second) {
            return getRank(first) - getRank(second);
        }

        public int getRank(MediaType type) {
            int compatibility = 0;
            if (type.isWildcardType()) {
                compatibility++;
            }
            if (type.isWildcardSubtype()) {
                compatibility++;
            }
            return compatibility;
        }


    }

}
