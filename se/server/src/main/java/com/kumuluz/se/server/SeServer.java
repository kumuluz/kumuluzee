package com.kumuluz.se.server;

import java.util.logging.Logger;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.config.ServerConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDependency;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.dependencies.ServerDef;

/**
 * NoOp Kumuluz server that serves as CDI SE container
 * 
 * @author Yog Sothoth
 *
 */
@ServerDef(value = "CDI No-Web server")
@EeComponentDependency(EeComponentType.CDI)
public class SeServer implements KumuluzServer {
    private static final Logger log = Logger.getLogger(SeServer.class.getSimpleName());
    
	private final SeContainerInitializer initializer =
			SeContainerInitializer.newInstance();
    private SeContainer ctx;
    private ServerConfig serverConfig;
    
    /*
     * (non-Javadoc)
     * @see com.kumuluz.ee.common.KumuluzServer#initServer()
     */
	@Override
	public void initServer() {
		log.info("Initializing SE server");
		try {
			this.ctx = initializer.initialize();
		}
		catch (UnsupportedOperationException uoe) {
			throw new IllegalStateException("Can't start CDI SE within a servlet environment");			
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
	}

	/*
	 * (non-Javadoc)
	 * @see com.kumuluz.ee.common.KumuluzServer#startServer()
	 */
	@Override
	public void startServer() {
		log.info("Starting SE server");
		
		if (ctx.getBeanManager() == null) {
			throw new IllegalStateException("CDI Bean Manager isn't initialized");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.kumuluz.ee.common.KumuluzServer#stopServer()
	 */
	@Override
	public void stopServer() {
		log.info("Stopping SE server");
		
		synchronized (ctx) {
			if (ctx.isRunning()) {
				ctx.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.kumuluz.ee.common.KumuluzServer#setServerConfig(com.kumuluz.ee.common.config.ServerConfig)
	 */
	@Override
	public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
	}

	/*
	 * (non-Javadoc)
	 * @see com.kumuluz.ee.common.KumuluzServer#getServerConfig()
	 */
	@Override
	public ServerConfig getServerConfig() {
        return serverConfig;
	}
}
