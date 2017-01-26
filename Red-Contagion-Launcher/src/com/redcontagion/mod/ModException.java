package com.redcontagion.mod;

import com.redcontagion.mod.ann.Mod;

public class ModException extends Exception {

	private static final long serialVersionUID = -3276193310489981447L;
	public final Mod mod;
	
	public ModException(String msg) {
		super(msg);
		mod = null;
	}
	
	public ModException(Mod mod, String msg) {
		super("Exception caused by mod \"" + mod.id() + "\": " + msg);
		this.mod = mod;
	}
	
	public ModException(String msg, Throwable cause) {
		super(msg, cause);
		mod = null;
	}
	
	public ModException(Mod mod, String msg, Throwable cause) {
		super("Exception caused by mod \"" + mod.id() + "\": " + msg, cause);
		this.mod = mod;
	}
	
}
