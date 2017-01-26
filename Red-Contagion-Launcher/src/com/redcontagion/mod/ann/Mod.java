package com.redcontagion.mod.ann;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

@Target(TYPE)
public @interface Mod {

	public String id();
	public String version() default "";
	public String name() default "";
	public String unlocName() default "";
	public String gameVersion() default "";
	public String requiredMods() default "";
	
}
