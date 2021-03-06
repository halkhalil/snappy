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

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by josh on 3/14/17.
 */
public class SchedulerConfig extends ExecutorConfigBase {

    private ScheduledThreadPoolExecutor scheduler;

    public SchedulerConfig(String name) {
        super(name);
    }

    public static SchedulerConfig withDefaults(String name) {
        SchedulerConfig defaultConfig = new SchedulerConfig(name);
        defaultConfig.scheduler = new ScheduledThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE);
        defaultConfig.scheduler.setMaximumPoolSize(DEFAULT_MAX_POOL_SIZE);
        defaultConfig.scheduler.setKeepAliveTime(DEFAULT_KEEP_ALIVE, TimeUnit.MINUTES);

        return defaultConfig;
    }

    public SchedulerConfig executor(ScheduledThreadPoolExecutor scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public ScheduledThreadPoolExecutor getScheduler() {
        return scheduler;
    }
}
