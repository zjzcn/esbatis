package com.github.esbatis.client;

import java.util.concurrent.TimeUnit;

/**
 * @author jinzhong.zhang
 */
public final class DeadHostState {

    private static final long MIN_CONNECTION_TIMEOUT_NANOS = TimeUnit.MINUTES.toNanos(1);
    private static final long MAX_CONNECTION_TIMEOUT_NANOS = TimeUnit.MINUTES.toNanos(30);

    static final DeadHostState INITIAL_DEAD_STATE = new DeadHostState();

    private final int failedAttempts;
    private final long deadUntilNanos;

    private DeadHostState() {
        this.failedAttempts = 1;
        this.deadUntilNanos = System.nanoTime() + MIN_CONNECTION_TIMEOUT_NANOS;
    }

    /**
     * We keep track of how many times a certain node fails consecutively. The higher that number is the longer we will wait
     * to retry that same node again. Minimum is 1 minute (for a node the only failed once), maximum is 30 minutes (for a node
     * that failed many consecutive times).
     */
    DeadHostState(DeadHostState previousDeadHostState) {
        long timeoutNanos = (long)Math.min(MIN_CONNECTION_TIMEOUT_NANOS * 2 * Math.pow(2, previousDeadHostState.failedAttempts * 0.5 - 1),
                MAX_CONNECTION_TIMEOUT_NANOS);
        this.deadUntilNanos = System.nanoTime() + timeoutNanos;
        this.failedAttempts = previousDeadHostState.failedAttempts + 1;
    }

    /**
     * Returns the timestamp (nanos) till the host is supposed to stay dead without being retried.
     * After that the host should be retried.
     */
    long getDeadUntilNanos() {
        return deadUntilNanos;
    }

    @Override
    public String toString() {
        return "DeadHostState{" +
                "failedAttempts=" + failedAttempts +
                ", deadUntilNanos=" + deadUntilNanos +
                '}';
    }
}