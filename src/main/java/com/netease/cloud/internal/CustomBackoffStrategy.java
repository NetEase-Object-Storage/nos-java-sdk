package com.netease.cloud.internal;

public abstract class CustomBackoffStrategy {
    public abstract int getBackoffPeriod(int retryAttempts);
}