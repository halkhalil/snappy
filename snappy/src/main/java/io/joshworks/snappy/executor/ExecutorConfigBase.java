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

package io.joshworks.snappy.executor;

/**
 * Created by josh on 3/14/17.
 */
public abstract class ExecutorConfigBase {

    private final String name;
    private boolean defaultExecutor;
    protected static final int DEFAULT_CORE_POOL_SIZE = 0;
    protected static final int DEFAULT_MAX_POOL_SIZE = 5;
    protected static final int DEFAULT_KEEP_ALIVE = 1;

    public ExecutorConfigBase(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    boolean isDefaultExecutor() {
        return defaultExecutor;
    }

    public ExecutorConfigBase markAsDefault() {
        this.defaultExecutor = true;
        return this;
    }
}
