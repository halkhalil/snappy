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

package io.joshworks.snappy.metric;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by josh on 3/11/17.
 */
public class Metrics {

    private static final Map<String, Supplier<Object>> lazyProperty = new ConcurrentHashMap<>();
    private static final Map<String, Object> properties = new ConcurrentHashMap<>();


    private Metrics() {
    }


    public static void addMetric(String key, Object value) {
        properties.put(key, value);
    }

    public static void addMetric(String key, Supplier<Object> value) {
        lazyProperty.put(key, value);
    }

    static Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.putAll(properties);
        Map<String, Object> collect = lazyProperty.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, val -> val.getValue().get()));
        data.putAll(collect);
        return data;
    }

}