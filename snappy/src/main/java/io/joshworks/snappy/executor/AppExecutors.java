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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.joshworks.snappy.SnappyServer.*;

/**
 * Created by josh on 3/7/17.
 */
public class AppExecutors {

    private static final Logger logger = LoggerFactory.getLogger(LOGGER_NAME);

    private static ExecutorContainer container;

    AppExecutors() {

    }

    private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> logger.error(e.getMessage(), e);

    synchronized static void init(ExecutorContainer executorContainer) {
        container = executorContainer;
    }


    public static void submit(Runnable runnable) {
        checkStarted();
        submit(container.defaultExecutor, runnable);
    }

    public static void execute(Runnable runnable) {
        checkStarted();
        execute(container.defaultExecutor, runnable);
    }

    public static void execute(String poolName, Runnable runnable) {
        ExecutorService executor = executor(poolName);
        Thread thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        executor.execute(thread);
    }

    public static Future<?> submit(String poolName, Runnable runnable) {
        ExecutorService executor = executor(poolName);
        return executor.submit(runnable);
    }

    public static <T> Future<T> submit(Runnable runnable, T result) {
        checkStarted();
        return submit(container.defaultExecutor, runnable, result);
    }

    public static <T> Future<T> submit(String poolName, Runnable runnable, T result) {
        ExecutorService executor = executor(poolName);
        return executor.submit(runnable, result);
    }

    public static ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        checkStarted();
        return schedule(container.defaultScheduler, runnable, delay, timeUnit);
    }

    public static ScheduledFuture<?> schedule(String poolName, Runnable runnable, long delay, TimeUnit timeUnit) {
        ScheduledThreadPoolExecutor scheduler = scheduler(poolName);
        return scheduler.schedule(runnable, delay, timeUnit);
    }

    public static <T> ScheduledFuture<T> schedule(Callable<T> callable, long delay, TimeUnit timeUnit) {
        checkStarted();
        return schedule(container.defaultScheduler, callable, delay, timeUnit);
    }

    public static <T> ScheduledFuture<T> schedule(String poolName, Callable<T> callable, long delay, TimeUnit timeUnit) {
        ScheduledThreadPoolExecutor scheduler = scheduler(poolName);
        return scheduler.schedule(callable, delay, timeUnit);
    }

    public static void scheduleAtFixedRate(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        checkStarted();
        scheduleAtFixedRate(container.defaultScheduler, runnable, delay, period, timeUnit);
    }

    public static void scheduleAtFixedRate(String poolName, Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        ScheduledThreadPoolExecutor scheduler = scheduler(poolName);
        scheduler.scheduleAtFixedRate(runnable, delay, period, timeUnit);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        checkStarted();
        return scheduleWithFixedDelay(container.defaultScheduler, runnable, initialDelay, delay, timeUnit);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(String poolName, Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        checkStarted();
        ScheduledThreadPoolExecutor scheduler = scheduler(poolName);
        return scheduler.scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit);
    }

    public static ExecutorService executor() {
        checkStarted();
        return executor(container.defaultExecutor);
    }

    public static ExecutorService executor(String poolName) {
        checkStarted();
        ThreadPoolExecutor threadPoolExecutor = container.executors.get(poolName);
        if (threadPoolExecutor == null) {
            throw new IllegalArgumentException("Thread pool not found for name " + poolName);
        }
        return threadPoolExecutor;
    }

    public static ScheduledThreadPoolExecutor scheduler() {
        checkStarted();
        return scheduler(container.defaultScheduler);
    }

    public static ScheduledThreadPoolExecutor scheduler(String poolName) {
        checkStarted();
        ScheduledThreadPoolExecutor threadPoolExecutor = container.schedulers.get(poolName);
        if (threadPoolExecutor == null) {
            throw new IllegalArgumentException("Thread pool not found for name " + poolName);
        }
        return threadPoolExecutor;
    }

    public static Map<String, ThreadPoolExecutor> executors() {
        checkStarted();
        return new HashMap<>(container.executors);
    }

    public static Map<String, ScheduledThreadPoolExecutor> schedulers() {
        checkStarted();
        return new HashMap<>(container.schedulers);
    }

    private static void checkStarted() {
        if(container == null) {
            throw new IllegalStateException("Server hasn't started");
        }
    }

}
