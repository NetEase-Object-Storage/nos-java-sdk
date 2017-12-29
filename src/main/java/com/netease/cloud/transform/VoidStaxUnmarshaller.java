package com.netease.cloud.transform;

/**
 * Simple StAX unmarshaller that iterates through the XML events but always
 * returns null.
 */
public class VoidStaxUnmarshaller<T> implements Unmarshaller<T, StaxUnmarshallerContext> {
	public T unmarshall(StaxUnmarshallerContext context) throws Exception {
        while (context.nextEvent().isEndDocument() == false);
        return null;
    }
}
