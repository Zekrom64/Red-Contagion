package com.redcontagion;

import java.util.logging.Level;

import com.zekrom_64.ze.base.backend.ZERenderBackend;
import com.zekrom_64.ze.base.backend.ZERenderOutput;
import com.zekrom_64.ze.base.err.ZEngineException;

public interface RedContagionRender {

	public void init();
	
	public void deinit();
	
	public ZERenderBackend<?> getBackend();
	
	public ZERenderOutput<?> getOutput();
	
	// Gets the game's render system.
	static RedContagionRender getRender() throws ZEngineException {
		String backend = RedContagion.tryGetProperty("rc.renderBackend", "gl30");
		switch(backend) {
		case "gl30": RedContagion.rcLogger.log(Level.WARNING, "OpenGL 3.0 backend not yet supported, using OpenGL 2.0");
		case "gl20": return new RedContagionRenderGL20();
		case "vulkan": throw new UnsupportedOperationException("Vulkan backend not yet supported");
		}
		throw new ZEngineException("Render backend \"" + backend + "\" not found");
	}
	
}
