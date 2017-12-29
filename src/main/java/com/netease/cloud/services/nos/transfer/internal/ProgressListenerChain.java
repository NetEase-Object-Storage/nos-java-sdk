package com.netease.cloud.services.nos.transfer.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netease.cloud.services.nos.model.ProgressEvent;
import com.netease.cloud.services.nos.model.ProgressListener;

public class ProgressListenerChain implements ProgressListener {
    private final List<ProgressListener> listeners = new ArrayList<ProgressListener>();

    private static final Log log = LogFactory.getLog(ProgressListenerChain.class);
    
    public ProgressListenerChain(ProgressListener... listeners) {
        for (ProgressListener listener : listeners) addProgressListener(listener);
    }

    public synchronized void addProgressListener(ProgressListener listener) {
        if (listener == null) return;
        this.listeners.add(listener);
    }

    public synchronized void removeProgressListener(ProgressListener listener) {
        if (listener == null) return;
        this.listeners.remove(listener);
    }

    public void progressChanged(final ProgressEvent progressEvent) {
        for ( ProgressListener listener : listeners ) {
            try {
                listener.progressChanged(progressEvent);
            } catch ( Throwable t ) {
                log.warn("Couldn't update progress listener", t);
            }
        }
    }
}
