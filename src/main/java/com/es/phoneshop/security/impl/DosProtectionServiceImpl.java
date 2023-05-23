package com.es.phoneshop.security.impl;

import com.es.phoneshop.security.DosProtectionService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.abs;

public enum DosProtectionServiceImpl implements DosProtectionService {
    INSTANCE;

    private static final Long resetTimeSeconds = 5L;
    private static final long THRESHOLD = 30;
    private static final Map<String, Info> countMap = new ConcurrentHashMap<>();

    @Override
    public boolean isAllowed(String ip) {
        Info info = countMap.get(ip);

        if (info == null) {
            info = new Info();
            info.quantity = 1L;
            countMap.put(ip, info);
        }

        if (info.quantity > THRESHOLD) {
            if (info.blockedTime == null) {
                info.blockedTime = LocalDateTime.now();
            }
            return reset(info);
        }

        info.quantity++;
        return true;
    }

    private boolean reset(Info info) {
        if (abs(ChronoUnit.SECONDS.between(LocalDateTime.now(), info.blockedTime)) >= resetTimeSeconds) {
            info.quantity = 1L;
            info.blockedTime = null;
            return true;
        }
        return false;
    }

    private static class Info {
        Long quantity;
        LocalDateTime blockedTime;
    }
}
