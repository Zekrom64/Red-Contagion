package com.redcontagion.mod.transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import org.objectweb.asm.MethodVisitor;

public class ClassTransformer {
	
	public static class ClassTransforms {
		
		private ClassTransforms() {}
		
		private boolean upgradeAccess;
		public final Set<FieldSignature> fieldTransforms = new HashSet<>();
		public final Map<FieldSignature, MethodTransform> methodTransforms = new HashMap<>();
		
		public boolean getUpgradeAccess() {
			return upgradeAccess;
		}
		
	}
	
	public static class MethodTransform {
		
		private MethodTransform() {}
		
		private Consumer<MethodVisitor> transformer;
		private String transformerMod;
		private boolean upgradeAccess;
		
		public Consumer<MethodVisitor> getTransformer() {
			return transformer;
		}
		
		public boolean getUpgradeAccess() {
			return upgradeAccess;
		}
		
	}
	
	private static final Map<String,ClassTransforms> classTransforms = new HashMap<>();
	
	public static void requestMethodTransform(String clazz, FieldSignature fs, Consumer<MethodVisitor> transform, String modid) {
		if (fs == null || transform == null || modid == null) return;
		ClassTransforms ct = classTransforms.get(clazz);
		if (ct == null) {
			ct = new ClassTransforms();
		}
		MethodTransform mt = ct.methodTransforms.get(fs);
		if (mt == null) {
			mt = new MethodTransform();
			ct.methodTransforms.put(fs, mt);
		}
		if (mt.transformer != null) {
			JOptionPane.showMessageDialog(null, "Method transformer from mod " + modid + " conflicts with transformer from " +
					mt.transformerMod, "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		mt.transformer = transform;
		mt.transformerMod = modid;
		
	}
	
	public static void requestMethodAccess(String clazz, FieldSignature fs) {
		if (fs == null) return;
		ClassTransforms ct = classTransforms.get(clazz);
		if (ct == null) {
			ct = new ClassTransforms();
		}
		MethodTransform mt = ct.methodTransforms.get(fs);
		if (mt == null) {
			mt = new MethodTransform();
			ct.methodTransforms.put(fs, mt);
		}
		mt.upgradeAccess = true;
	}
	
	public static void requestFieldAccess(String clazz, FieldSignature fs) {
		if (fs == null) return;
		ClassTransforms ct = classTransforms.get(clazz);
		if (ct == null) {
			ct = new ClassTransforms();
		}
		ct.fieldTransforms.add(fs);
	}
	
	public static void requestClassAccess(String clazz) {
		ClassTransforms ct = classTransforms.get(clazz);
		if (ct == null) {
			ct = new ClassTransforms();
		}
		ct.upgradeAccess = true;
	}
	
	public static ClassTransforms getClassTransforms(String clazz) {
		return classTransforms.get(clazz);
	}
	
}
