package com.netease.cloud.services.nos.transfer.internal;

import com.netease.cloud.services.nos.transfer.Transfer;
import com.netease.cloud.services.nos.transfer.Transfer.TransferState;

/**
 * Listener for transfer state changes.  Not intended to be consumed externally.
 */
public interface TransferStateChangeListener {
    public void transferStateChanged(Transfer transfer, TransferState state);
}