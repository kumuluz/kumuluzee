package com.kumuluz.ee.logs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LogDeferrer<T> {

    private Supplier<T> logger;
    private List<Consumer<T>> logs = new ArrayList<>();
    private Boolean finished = false;

    public void init(Supplier<T> logger) {

        if (this.logger != null) {

            throw new IllegalStateException("The LogDeferrer was already initiated with a logger instance");
        }

        this.logger = logger;
    }

    public void defer(Consumer<T> log) {

        this.logs.add(log);
    }

    public void execute() {

        if (finished) {

            throw new IllegalStateException("The LogDeferrer was already executed. Please create a new one.");
        }

        T loggerInstance = logger.get();

        for (Consumer<T> log : logs) {

            log.accept(loggerInstance);
        }

        this.logger = null;
        this.logs.clear();
        this.finished = true;
    }
}
