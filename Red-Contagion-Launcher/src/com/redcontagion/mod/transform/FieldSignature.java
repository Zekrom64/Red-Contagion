package com.redcontagion.mod.transform;

public class FieldSignature {

	public final String name;
	public final String signature;
	public final boolean isStatic;
	
	public FieldSignature(String n, String s, boolean is) {
		name = n;
		signature = s;
		isStatic = is;
	}
	
	public boolean equals(Object o) {
		if (o instanceof FieldSignature) return equals((FieldSignature)o);
		return false;
	}
	
	public boolean equals(FieldSignature s) {
		if (s == null) return false;
		return name.equals(s.name) && signature.equals(s.signature) && isStatic == s.isStatic;
	}
	
}
