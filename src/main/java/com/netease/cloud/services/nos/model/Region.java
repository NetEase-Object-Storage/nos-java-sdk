package com.netease.cloud.services.nos.model;

/**
 * Specifies constants that define Nos Regions.
 * <p>
 * Nos Regions allow the user to choose the geographical region where Nos will
 * store the buckets the user creates. Choose a Nos Region to optimize latency,
 * minimize costs, or address regulatory requirements.
 * </p>
 * <p>
 * Objects stored in a Nos Region never leave that region unless explicitly
 * transfered to another region.
 * </p>
 */
public enum Region {

	/**
	 * The US Standard Nos Region. This region uses Nos servers located in the
	 * United States.
	 * <p>
	 * This is the default Nos Region. All requests sent to
	 */
	CN_Standard(null),

	/**
	 * The US-West (Northern California) Nos Region. This region uses Nos
	 * servers located in Northern California.
	 * <p>
	 * <p>
	 * In Nos, the US-West (Northern California) Region provides
	 * read-after-write consistency for PUTS of new objects in Nos buckets and
	 * eventual consistency for overwrite PUTS and DELETES.
	 * </p>
	 */
	CN_Hnagzhou("HZ"),

	/**
	 * The US-West-2 (Oregon) Region. This region uses Nos servers located in
	 * Oregon.
	 */
	CN_Guangzhou("GZ"),

	/**
	 * The EU (Ireland) Nos Region. This region uses Nos servers located in
	 * Ireland.
	 * <p>
	 * In Nos, the EU (Ireland) Region provides read-after-write consistency for
	 * PUTS of new objects in Nos buckets and eventual consistency for overwrite
	 * PUTS and DELETES.
	 * </p>
	 */
	CN_Beijing("BJ");

	/** The unique ID representing each region. */
	private final String regionId;

	/**
	 * Constructs a new region with the specified region ID.
	 * 
	 * @param regionId
	 *            The unique ID representing the Nos region.
	 */
	private Region(String regionId) {
		this.regionId = regionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return regionId;
	}

	/**
	 * Returns the Nos Region enumeration value representing the specified Nos
	 * Region ID string. If specified string doesn't map to a known Nos Region,
	 * then an <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param NosRegionString
	 *            The Nos region ID string.
	 * 
	 * @return The Nos Region enumeration value representing the specified Nos
	 *         Region ID.
	 * 
	 * @throws IllegalArgumentException
	 *             If the specified value does not map to one of the known Nos
	 *             regions.
	 */
	public static Region fromValue(String NosRegionString) throws IllegalArgumentException {
		for (Region region : Region.values()) {
			String regionString = region.toString();
			if (regionString == null && NosRegionString == null)
				return region;
			if (regionString != null && regionString.equals(NosRegionString))
				return region;
		}

		throw new IllegalArgumentException("Cannot create enum from " + NosRegionString + " value!");
	}
}
