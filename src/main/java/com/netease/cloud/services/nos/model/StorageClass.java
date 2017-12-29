package com.netease.cloud.services.nos.model;

/**
 * <p>
 * Specifies constants that define Nos storage classes. The standard storage
 * class is the default storage class.
 * </p>
 * <p>
 * Nos offers multiple storage classes for different customers' needs. The
 * <code>STANDARD</code> storage class is the default storage class, and means
 * that redundant copies of data will be stored in different locations.
 * </p>
 * <p>
 * The <code>REDUCED_REDUNDANCY</code> storage class offers customers who are
 * using Nos for storing non-critical, reproducible data a low-cost highly
 * available, but less redundant, storage option.
 * </p>
 */
public enum StorageClass {

	/**
	 * The default Nos storage class. The standard storage class is a highly
	 * available and highly redundant storage option provided for an affordable
	 * price.
	 */
	Standard("standard"),

	/**
	 * The cheap storage class. This storage class allows customers to reduce
	 * their storage costs in return for a reduced level of data redundancy.
	 */
	Cheap("cheap"),

	/**
	 * The trivial storage class simple stores user data with no raid.
	 */
	Trivial("trivial"),

	/**
	 * The default archive storage. Used to store mass of archive data with
	 * infrequent read.
	 */
	ArchiveStandard("archive-standard"),

	/**
	 * The cheap archive storage class.
	 */
	ArchiveCheap("archive-cheap"),

	/**
	 * The archive trivial storage class simple stores archive data with no raid.
	 */
	ArchiveTrivial("archive-trivial");

	/**
	 * Returns the Nos {@link StorageClass} enumeration value representing the
	 * specified Nos <code>StorageClass</code> ID string. If the specified
	 * string doesn't map to a known Nos storage class, an
	 * <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param NosStorageClassString
	 *            The Nos storage class ID string.
	 * 
	 * @return The Nos <code>StorageClass</code> enumeration value representing
	 *         the specified Nos storage class ID.
	 * 
	 * @throws IllegalArgumentException
	 *             If the specified value does not map to one of the known Nos
	 *             storage classes.
	 */
	public static StorageClass fromValue(String NosStorageClassString) throws IllegalArgumentException {
		for (StorageClass storageClass : StorageClass.values()) {
			if (storageClass.toString().equals(NosStorageClassString))
				return storageClass;
		}

		throw new IllegalArgumentException("Cannot create enum from " + NosStorageClassString + " value!");
	}

	private final String storageClassId;

	private StorageClass(String id) {
		this.storageClassId = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return storageClassId;
	}

}
