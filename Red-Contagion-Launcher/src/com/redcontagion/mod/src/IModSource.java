package com.redcontagion.mod.src;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface IModSource {

	public Properties getProperties();
	
	public ClassLoader getClassLoader();
	
	public InputStream getResourceAsStream(String path) throws IOException;
	
}
