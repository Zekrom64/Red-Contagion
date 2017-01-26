package com.redcontagion.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

import com.redcontagion.mod.ModLoader;

public class Launcher {
	
	public static final File versionsFolder = new File("versions");
	public static final File versionFile = new File("version.properties");
	public static final File launcherProperties = new File("launcher.properties");
	private static GameClassLoader classLoader;
	
	static {
		if (!(versionsFolder.exists() && versionsFolder.isDirectory())) versionsFolder.mkdirs();
	}
	
	public static File[] getVersionFiles() {
		return versionsFolder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				return arg0.getName().endsWith(".jar") && arg0.isFile();
			}
			
		});
	}

	public static void main(String[] args) {
		String verName = null;
		try (BufferedReader br = new BufferedReader(new FileReader(versionFile))) {
			verName = br.readLine();
		} catch (IOException e) {
			System.err.println("Failed to read installation version file");
			e.printStackTrace();
		}
		File verFile = null;
		if (System.getProperties().contains("rc.launcher")) {
			verFile = LauncherUI.getVersion(verName);
			if (verFile==null) return;
		} else {
			verFile = new File(versionsFolder, verName);
			if (!verFile.exists()) {
				JOptionPane.showMessageDialog(null, "Version \"" + verName + "\" does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		try {
			classLoader = new GameClassLoader(verFile);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to load game version: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		ModLoader.loadMods();
		try {
			Class<?> gameClass = classLoader.loadClass("com.redcontagion.RedContagion");
			Method m = gameClass.getDeclaredMethod("main", String[].class);
			m.invoke(null, new Object[] {args});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to find game main class: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to invoke main method: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
