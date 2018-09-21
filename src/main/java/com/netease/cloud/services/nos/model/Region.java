package com.netease.cloud.services.nos.model;

/**
 * Specifies constants that define Nos Regions.
 * <p>
 * Nos Regions allow the user to choose the geographical region where Nos will
 * store the buckets the user creates. Choose a Nos Region to optimize latency,
 * minimize NOSts, or address regulatory requirements.
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
	 * 杭州机房
	 */
	CN_Hnagzhou("HZ"),

	/**
	 * 建德机房
	 */
	CN_Jiande("JD"),

	/**
	 * 北京机房
	 */
	CN_Beijing("BJ"),

	/**
	 * 公有云北京
	 */
	CN_NORTH_1("BJ"),
	/**
	 * 公有云杭州
	 */
	CN_EAST_1("HZ"),
	/**
	 * 私有云杭州
	 */
	CN_EAST_P0("HZ"),
	/**
	 * 公有云建德
	 */
	CN_EAST_3("JD"),
	/**
	 * 私有云建德
	 */
	CN_EAST_P1("JD");

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
