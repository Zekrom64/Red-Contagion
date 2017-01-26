package com.redcontagion;

import com.zekrom_64.ze.base.backend.render.ZERenderBackend;
import com.zekrom_64.ze.base.backend.render.ZERenderOutput;
import com.zekrom_64.ze.gl.GL20RenderBackend;
import com.zekrom_64.ze.gl.GLRenderOutput;

public class RedContagionRenderGL20 implements RedContagionRender {

	public final GL20RenderBackend backend = new GL20RenderBackend();
	
	private GLRenderOutput output = null;
	
	@Override
	public void init() {
		output = new GLRenderOutput(RedContagion.window);
	}

	@Override
	public void deinit() {
		backend.deinit();
	}

	@Override
	public ZERenderBackend<?> getBackend() {
		return backend;
	}

	@Override
	public ZERenderOutput<?> getOutput() {
		return output;
	}

}
