package com.redcontagion.mod;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.redcontagion.mod.src.URLModSource;

public class ModLoader {

	public static final File modsFolder = new File("mods");
	public static final File modPropertiesFile = new File("mods.properties");
	
	static {
		if (!(modsFolder.isDirectory() && modsFolder.exists())) modsFolder.mkdirs();
	}
	
	private static final List<ModWrapper> mods = new ArrayList<>();
	
	private static void getFolderModFiles(File folder, Set<File> modFiles) {
		File[] files = folder.listFiles();
		for(File f : files) {
			if (f.isDirectory()) getFolderModFiles(f, modFiles);
			else {
				if (f.getName().endsWith(".jar")) modFiles.add(f);
			}
		}
	}
	
	public static void loadMods() {
		Set<File> modFiles = new HashSet<>();
		getFolderModFiles(modsFolder, modFiles);
		for(File f : modFiles) {
			try {
				URLModSource src = new URLModSource(f.toURI().toURL());
				Properties props = src.getProperties();
				ClassLoader cl = src.getClassLoader();
				String classNames = props.getProperty("mod.class");
				for(String name : classNames.split(";")) {
					try {
						Class<?> modClass = cl.loadClass(name);
						mods.add(new ModWrapper(src, modClass));
					} catch (ClassNotFoundException | ModException e) {
						System.err.println("Failed to load mod class \"" + name + '\"');
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				System.err.println("Failed to mount mod file \"" + f.getPath() + '\"');
				e.printStackTrace();
			}
		}
	}
	
	public static List<ModWrapper> getMods() {
		return Collections.unmodifiableList(mods);
	}
	
}
