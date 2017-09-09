package com.github.esbatis.client;

import com.github.esbatis.exceptions.RestException;
import com.github.esbatis.utils.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RestClient {

    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    private final Set<String> hostSet = new HashSet<>();
    private final ConcurrentMap<String, DeadHostState> deadHosts = new ConcurrentHashMap<>();

    private final AtomicInteger lastHostIndex = new AtomicInteger(0);

    private final long maxRetryTimeoutMillis = 10000;

    private final long maxRetryCount = 10;

    public RestClient(String hosts) {
        for(String host : hosts.split(",")) {
            this.hostSet.add(host);
        }
    }

    public String send(String url, String method, String message) {
        Iterator<String> hosts = nextHost();
        String host = hosts.next();
        url = buildUrl(host, url);
        logger.info("Request data: \nurl={} \nmethod={} \nmessage={}.", url, method, message);
        HttpClient httpClient = HttpClient.request(url, method);
        if (message != null && message.length() != 0) {
            httpClient.send(message);
        }
        String resp = httpClient.body();
        logger.info("Response data: \n{}.", resp);

        int code = httpClient.code();
        if (isSuccessfulResponse(code)) {
            return resp;
        } else {
            RestException httpException = new RestException(
                    "Request failure, code=" + code + ". \nresponse=" + resp);
            throw httpException;
        }
    }


    private String buildUrl(String host, String url) {
        if (host.endsWith("/")) {
            host = host.substring(0, host.length()-1);
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
                    logger.trace("resurrecting host [" + deadHost + "]");
                    nextHosts = Collections.singleton(deadHost);
                }
            } else {
                List<String> rotatedHosts = new ArrayList<>(filteredHosts);
                Collections.rotate(rotatedHosts, rotatedHosts.size() - lastHostIndex.getAndIncrement());
                nextHosts = rotatedHosts;
            }
        } while(nextHosts.isEmpty());
        return nextHosts.iterator();
    }


    private boolean retryIfPossible(Iterator<String> hosts, long startTime, Exception exception) throws Exception {
        if (hosts.hasNext()) {
            //in case we are retrying, check whether maxRetryTimeout has been reached
            long timeElapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            long timeout = maxRetryTimeoutMillis - timeElapsedMillis;
            if (timeout <= 0) {
                IOException retryTimeoutException = new IOException(
                        "request retries exceeded max retry timeout [" + maxRetryTimeoutMillis + "]");
                throw retryTimeoutException;
            } else {
                return true;
            }
        } else {
            throw exception;
        }
    }

    private static boolean isSuccessfulResponse(int statusCode) {
        return statusCode < 300;
    }

    private static boolean isRetryStatus(int statusCode) {
        switch(statusCode) {
            case 502:
            case 503:
            case 504:
                return true;
        }
        return false;
    }
}
