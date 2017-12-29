package com.netease.cloud.http;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ClientConnectionManager;

/**
 * Daemon thread to periodically check connection pools for idle connections.
 * <p>
 * Connections sitting around idle in the HTTP connection pool for too long will
 * eventually be terminated by the end of the connection, and will go into
 * CLOSE_WAIT. If this happens, sockets will sit around in CLOSE_WAIT, still
 * using resources on the client side to manage that socket. Many sockets stuck
 * in CLOSE_WAIT can prevent the OS from creating new connections.
 * <p>
 * This class closes idle connections before they can move into the CLOSE_WAIT
 * state.
 */
public class IdleConnectionReaper extends Thread {

	/** The period between invocations of the idle connection reaper. */
	private static final int PERIOD_MILLISECONDS = 1000 * 60 * 1;

	/**
	 * The list of registered connection managers, whose connections will be
	 * periodically checked and idle connections closed.
	 */
	private static ArrayList<ClientConnectionManager> connectionManagers = new ArrayList<ClientConnectionManager>();

	/** Singleton instance of the connection reaper. */
	private static IdleConnectionReaper instance;

	/** Shared log for any errors during connection reaping. */
	static final Log log = LogFactory.getLog(IdleConnectionReaper.class);

	/** Private constructor - singleton pattern. */
	private IdleConnectionReaper() {
		super("java-sdk-http-connection-reaper");
		setDaemon(true);
		start();
	}

	public static synchronized void registerConnectionManager(ClientConnectionManager connectionManager) {
		if (instance == null)
			instance = new IdleConnectionReaper();
		connectionManagers.add(connectionManager);
	}

	public static synchronized void removeConnectionManager(ClientConnectionManager connectionManager) {
		connectionManagers.remove(connectionManager);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(PERIOD_MILLISECONDS);

				// Copy the list of managed ConnectionManagers to avoid possible
				// ConcurrentModificationExceptions if registerConnectionManager
				// or
				// removeConnectionManager are called while we're iterating
				// (rather
				// than block/lock while this loop executes).
				@SuppressWarnings({ "unchecked", "static-access" })
				List<ClientConnectionManager> connectionManagers = (List<ClientConnectionManager>) this.connectionManagers
						.clone();
				for (ClientConnectionManager connectionManager : connectionManagers) {
					// When we release connections, the connection manager
					// leaves them
					// open so they can be reused. We want to close out any idle
					// connections so that they don't sit around in CLOSE_WAIT.
					try {
						connectionManager.closeIdleConnections(60, TimeUnit.SECONDS);
					} catch (Throwable t) {
						log.warn("Unable to close idle connections", t);
					}
				}
			} catch (Throwable t) {
				log.warn("Unable to close idle connections", t);
			}
		}
	}
}