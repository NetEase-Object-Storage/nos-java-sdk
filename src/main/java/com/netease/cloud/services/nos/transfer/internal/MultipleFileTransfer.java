package com.netease.cloud.services.nos.transfer.internal;

import java.util.Collection;

import com.netease.cloud.services.nos.transfer.Transfer;
import com.netease.cloud.services.nos.transfer.TransferProgress;

/**
 * Interface for multiple file transfers
 */
public abstract class MultipleFileTransfer extends AbstractTransfer {

	protected final Collection<? extends Transfer> subTransfers;

	MultipleFileTransfer(String description, TransferProgress transferProgress,
			ProgressListenerChain progressListenerChain, Collection<? extends Transfer> subTransfers) {
		super(description, transferProgress, progressListenerChain);
		this.subTransfers = subTransfers;
	}

	/**
	 * Set the state based on the states of all file downloads. Assumes all file
	 * downloads are done.
	 * <p>
	 * A single failed sub-transfer makes the entire transfer failed. If there
	 * are no failed sub-transfers, a single canceled sub-transfer makes the
	 * entire transfer canceled. Otherwise, we consider ourselves Completed.
	 */
	public void collateFinalState() {
		boolean seenCanceled = false;
		for (Transfer download : subTransfers) {
			if (download.getState() == TransferState.Failed) {
				setState(TransferState.Failed);
				return;
			} else if (download.getState() == TransferState.Canceled) {
				seenCanceled = true;
			}
		}
		if (seenCanceled)
			setState(TransferState.Canceled);
		else
			setState(TransferState.Completed);
	}
}
