package com.github.esbatis.client;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jinzhong.zhang
 */
public class RestClient {

    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    private final Set<String> hostSet = new HashSet<>();
    private final ConcurrentMap<String, DeadHostState> deadHosts = new ConcurrentHashMap<>();

    private final AtomicInteger lastHostIndex = new AtomicInteger(0);

    private final OkHttpClient httpClient;

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_READ_TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_WRITE_TIMEOUT_SECONDS = 30;

    public RestClient(String hosts) {
        this(hosts, DEFAULT_CONNECT_TIMEOUT_SECONDS, DEFAULT_READ_TIMEOUT_SECONDS, DEFAULT_WRITE_TIMEOUT_SECONDS);
    }

    public RestClient(String hosts, int connectTimeout, int readTimeout, int writeTimeout) {
        httpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES))
                .build();

        for (String host : hosts.split(",")) {
            this.hostSet.add(host.trim());
        }
    }


    public HttpResponse send(HttpRequest httpRequest) {
        Iterator<String> hosts = nextHost();
        String host = hosts.next();
        String url = buildUrl(host, httpRequest.getUrl());

        logger.debug("Http request data: {}", httpRequest);
        RequestBody body = RequestBody.create(JSON_TYPE, httpRequest.getBody());
        Request request = new Request.Builder()
                .url(url)
                .method(httpRequest.getMethod(), body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            onResponse(host);
            HttpResponse httpResponse = new HttpResponse(host, response.code(), response.body().string());
            logger.debug("Http response data: {}", httpResponse);
            return  httpResponse;
        } catch (Exception e) {
            onFailure(host);
            throw new RestException(host, e);
        }
    }

    private String buildUrl(String host, String url) {
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        return host + "/" + url;
    }

    private Iterator<String> nextHost() {
        Collection<String> nextHosts = Collections.<String>emptySet();
        do {
            Set<String> filteredHosts = new HashSet<>(this.hostSet);
            for (Map.Entry<String, DeadHostState> entry : this.deadHosts.entrySet()) {
                if (System.nanoTime() - entry.getValue().getDeadUntilNanos() < 0) {
                    filteredHosts.remove(entry.getKey());
                }
            }
            if (filteredHosts.isEmpty()) {
                //last resort: if there are no good host to use, return a single dead one, the one that's closest to being retried
                List<Map.Entry<String, DeadHostState>> sortedHosts = new ArrayList<>(this.deadHosts.entrySet());
                if (sortedHosts.size() > 0) {
                    Collections.sort(sortedHosts, new Comparator<Map.Entry<String, DeadHostState>>() {
                        @Override
                        public int compare(Map.Entry<String, DeadHostState> o1, Map.Entry<String, DeadHostState> o2) {
                            return Long.compare(o1.getValue().getDeadUntilNanos(), o2.getValue().getDeadUntilNanos());
                        }
                    });
                    String deadHost = sortedHosts.get(0).getKey();
                    nextHosts = Collections.singleton(deadHost);
                }
            } else {
                List<String> rotatedHosts = new ArrayList<>(filteredHosts);
                Collections.rotate(rotatedHosts, rotatedHosts.size() - lastHostIndex.getAndIncrement());
                nextHosts = rotatedHosts;
            }
        } while (nextHosts.isEmpty());
        return nextHosts.iterator();
    }

    private void onResponse(String host) {
        DeadHostState removedHost = this.deadHosts.remove(host);
        if (removedHost != null) {
            logger.info("Removed host [" + host + "] from deadHosts");
        }
    }


    private void onFailure(String host) {
            DeadHostState previousDeadHostState = deadHosts.putIfAbsent(host, DeadHostState.INITIAL_DEAD_STATE);
            if (previousDeadHostState == null) {
                logger.info("Added host [" + host + "] to deadHosts");
                return;
            }
            boolean updated = deadHosts.replace(host, previousDeadHostState, new DeadHostState(previousDeadHostState));
            if (updated) {
                logger.info("Updated host [" + host + "] already in deadHosts");
            }
    }

    private static boolean isSuccessfulResponse(int statusCode) {
        return statusCode < 300;
    }


    /**
     * inner class
     */
    static class DeadHostState {

        private static final long MIN_CONNECTION_TIMEOUT_NANOS = TimeUnit.MINUTES.toNanos(1);
        private static final long MAX_CONNECTION_TIMEOUT_NANOS = TimeUnit.MINUTES.toNanos(30);

        static final DeadHostState INITIAL_DEAD_STATE = new DeadHostState();

        private final int failedAttempts;
        private final long deadUntilNanos;

        private DeadHostState() {
            this.failedAttempts = 1;
            this.deadUntilNanos = System.nanoTime() + MIN_CONNECTION_TIMEOUT_NANOS;
        }

        DeadHostState(DeadHostState previousDeadHostState) {
            // timeoutNanos = MIN_CONNECTION_TIMEOUT_NANOS * 2 * 2^(i * 0.5 - 1), i = 1...n
            long timeoutNanos = (long) (MIN_CONNECTION_TIMEOUT_NANOS * 2 * Math.pow(2, previousDeadHostState.failedAttempts * 0.5 - 1));
            timeoutNanos = Math.min(timeoutNanos, MAX_CONNECTION_TIMEOUT_NANOS);
            this.deadUntilNanos = System.nanoTime() + timeoutNanos;
            this.failedAttempts = previousDeadHostState.failedAttempts + 1;
        }

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
}
