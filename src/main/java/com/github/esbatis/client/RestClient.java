package com.github.esbatis.client;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    public RestClient(String hosts) {
        httpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES))
                .build();

        for (String host : hosts.split(",")) {
            this.hostSet.add(host);
        }
    }

    public String send(String url, String method, String message) {
        Iterator<String> hosts = nextHost();
        String host = hosts.next();
        url = buildUrl(host, url);

        logger.debug("Http Request Data: \nurl = {} \nmethod = {} \nmessage = {}", url, method, message);
        RequestBody body = RequestBody.create(JSON_TYPE, message);
        Request request = new Request.Builder()
                .url(url)
                .method(method, body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            onResponse(host);
            int code = response.code();
            String resp = response.body().string();
            logger.debug("Http Response Data: \nurl = {} \nmethod = {} \nmessage = {} \nresponse = {}", url, method, message, resp);
            if (isSuccessfulResponse(code)) {
                return resp;
            } else {
                throw new RestException("Http Request Failure: code = " + code + " \nresponse = " + resp);
            }

        } catch (IOException e) {
            onFailure(host);
            logger.error("Http Request IOException. \nurl = {} \nmethod = {} \nmessage = {}", url, method, message, e);
            throw new RestException(e);
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
        while (true) {
            DeadHostState previousDeadHostState = deadHosts.putIfAbsent(host, DeadHostState.INITIAL_DEAD_STATE);
            if (previousDeadHostState == null) {
                logger.warn("Added host [" + host + "] to deadHosts");
                break;
            }
            if (deadHosts.replace(host, previousDeadHostState, new DeadHostState(previousDeadHostState))) {
                logger.warn("Updated host [" + host + "] already in deadHosts");
                break;
            }
        }
    }

    private static boolean isSuccessfulResponse(int statusCode) {
        return statusCode < 300;
    }

}
