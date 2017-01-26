package com.redcontagion.mod.src;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

public class URLModSource implements IModSource {

	public final URLClassLoader classLoader;
	private Properties props = new Properties();
	
	public URLModSource(URL source) throws IOException {
		classLoader = new URLClassLoader(new URL[] {source});
		URL propsURL = new URL(source, "/modinfo.properties");
		props.load(propsURL.openStream());
	}
	
	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public InputStream getResourceAsStream(String path) throws IOException {
		return classLoader.getResourceAsStream(path);
	}

}
