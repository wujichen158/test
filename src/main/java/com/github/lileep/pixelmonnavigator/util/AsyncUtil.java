package com.github.lileep.pixelmonnavigator.util;

import com.github.lileep.pixelmonnavigator.PixelmonNavigator;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.util.DefaultUncaughtExceptionHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class AsyncUtil {
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(5,
            (new ThreadFactoryBuilder())
                    .setDaemon(true)
                    .setNameFormat("pixelmonnavigator_concurrency_%d")
                    .setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(PixelmonNavigator.LOGGER))
                    .build());

    public static void runAsync(Runnable runnable) {
        CompletableFuture.runAsync(runnable, SCHEDULED_EXECUTOR_SERVICE).exceptionally((throwable) -> {
            PixelmonNavigator.LOGGER.error("Error while executing async task", throwable);
            return null;
        });
    }
}
