package com.netease.cloud.services.nos.transfer;

/**
 * Describes the progress of a transfer.
 */
public abstract class TransferProgress {
    protected volatile long bytesTransfered = 0;
    protected volatile long totalBytesToTransfer = -1;

    /**
     * Returns the number of bytes completed in the associated transfer.
     *
     * @return The number of bytes completed in the associated transfer.
     */
    public long getBytesTransfered() {
        return bytesTransfered;
    }

    /**
     * Returns the total size in bytes of the associated transfer, or -1
     * if the total size isn't known.
     *
     * @return The total size in bytes of the associated transfer.
     * 		   Returns or -1 if the total size of the associated
     * 		   transfer isn't known yet.
     */
    public long getTotalBytesToTransfer() {
        return totalBytesToTransfer;
    }

    /**
     * Returns a percentage of the number of bytes transfered out of the total
     * number of bytes to transfer.
     *
     * @return A percentage of the number of bytes transfered out of the total
     *         number of bytes to transfer.
     */
    public synchronized double getPercentTransfered() {
        if (getBytesTransfered() < 0) return 0;

        return ((double)getBytesTransfered() / (double)getTotalBytesToTransfer()) * (double)100;
    }
}
