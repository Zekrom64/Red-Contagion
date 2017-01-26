package com.redcontagion.mod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.redcontagion.mod.ann.Mod;
import com.redcontagion.mod.ann.ModInstance;
import com.redcontagion.mod.ann.ModOnInit;
import com.redcontagion.mod.ann.ModOnMount;
import com.redcontagion.mod.ann.ModOnPreInit;
import com.redcontagion.mod.ann.ModOnUnmount;
import com.redcontagion.mod.src.IModSource;

public class ModWrapper {

	public final Class<?> modClass;
	private Object instance;
	boolean isLoaded;
	boolean isActive;
	public final Mod modInfo;
	public final IModSource modSource;
	
	public ModWrapper(IModSource src, Class<?> mod) throws ModException {
		modSource = src;
		modClass = mod;
		modInfo = mod.getAnnotation(Mod.class);
		if (modInfo==null) throw new ModException("Class \"" + mod.getCanonicalName() + "\" is missing @Mod annotation");
	}
	
	public Object getInstance() {
		return instance;
	}
	
	public boolean isLoaded() {
		return isLoaded;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	private Method getMethod(Class<?> annotation) {
		Method[] methods = modClass.getDeclaredMethods();
		for(Method m : methods) {
			Annotation[] annotations = m.getAnnotations();
			for(Annotation a : annotations) if (a.getClass() == annotation) return m;
		}
		return null;
	}
	
	private void invokeMethodVoid(Class<?> annotation) throws ModException {
		Method m = getMethod(annotation);
		if (m!=null) {
			try {
				m.invoke(instance);
			} catch (Exception e) {
				throw new ModException(modInfo, "Failed to invoke mod class method", e);
			}
		}
	}
	
	public void load() throws ModException {
		if (instance != null) unload();
		try {
			Constructor<?> ctor = modClass.getConstructor();
			instance = ctor.newInstance();
			Field[] fields = modClass.getDeclaredFields();
			for(Field f : fields) {
				Annotation annotations[]  = f.getAnnotations();
				for(Annotation a : annotations) {
					if (a.getClass()==ModInstance.class) {
						f.set(null, instance);
					}
				}
			}
			invokeMethodVoid(ModOnMount.class);
		} catch (Exception e) {
			if (instance!=null) unload();
			if (e instanceof ModException) throw (ModException)e;
			else throw new ModException(modInfo, "Failed to invoke unmount callback on mod", e);
		}
		isLoaded = true;
	}
	
	public ModException unload() {
		ModException err = null;
		try {
			invokeMethodVoid(ModOnUnmount.class);
		} catch (Exception e) {
			if (e instanceof ModException) err = (ModException)e;
			else err = new ModException(modInfo, "Failed to invoke unmount callback on mod", e);
		}
		instance = null;
		isLoaded = false;
		return err;
	}
	
	public void preinit() throws ModException {
		if (isLoaded) invokeMethodVoid(ModOnPreInit.class);
	}
	
	public void init() throws ModException {
		if (isLoaded) invokeMethodVoid(ModOnInit.class);
	}
	
}
