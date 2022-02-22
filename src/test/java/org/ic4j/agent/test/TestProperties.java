package org.ic4j.agent.test;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class TestProperties extends Properties{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static final Logger LOG = LoggerFactory.getLogger(TestProperties.class);
	
	static String PROPERTIES_FILE_NAME = "test.properties";	

	static String IC_URL_PROPERTY = "icUrl";
	static String IC_CANISTER_ID_PROPERTY = "icCanisterId";
	
	protected static String IC_URL;
	protected static String IC_CANISTER_ID;	
	
	static
	{	 
		InputStream propInputStream = TestProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);

		try {
			Properties props = new Properties();
			props.load(propInputStream);
			
			IC_URL = props.getProperty(IC_URL_PROPERTY);
			IC_CANISTER_ID = props.getProperty(IC_CANISTER_ID_PROPERTY);
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}
}
