package com.lopezinho.friendly_expense_tracker.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimiterService {

    private final ConcurrentMap<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    public boolean tryConsumeLogin(String ip) {
        Bucket bucket = loginBuckets.computeIfAbsent(ip, key -> createLoginBucket());
        return bucket.tryConsume(1);
    }

    public boolean tryConsumeRegister(String ip) {
        Bucket bucket = registerBuckets.computeIfAbsent(ip, key -> createRegisterBucket());
        return bucket.tryConsume(1);
    }

    //FIVE LOGINS PER MINUTE
    private Bucket createLoginBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    //FIVE REGISTRATIONS PER HOUR
    private Bucket createRegisterBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.greedy(3, Duration.ofHours(1)));
        return Bucket.builder().addLimit(limit).build();
    }

}