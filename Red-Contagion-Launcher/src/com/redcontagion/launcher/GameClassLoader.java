package com.redcontagion.launcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.redcontagion.mod.transform.ClassTransformer;
import com.redcontagion.mod.transform.ClassTransformer.ClassTransforms;
import com.redcontagion.mod.transform.ClassTransformer.MethodTransform;
import com.redcontagion.mod.transform.FieldSignature;

public class GameClassLoader extends ClassLoader implements Opcodes {

	public final JarFile jarFile;
	
	GameClassLoader(File version) throws IOException {
		jarFile = new JarFile(version);
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String path = name.replace('.', '/').concat(".class");
		JarEntry entry = jarFile.getJarEntry(path);
		if (entry==null) throw new ClassNotFoundException(name);
		try (InputStream classStream = jarFile.getInputStream(entry)) {
			ClassTransforms ct = ClassTransformer.getClassTransforms(name);
			if (ct != null) {
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
				ClassVisitor cv = new ClassVisitor(ASM6, cw) {

					@Override
					public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
						if (ct.fieldTransforms.contains(new FieldSignature(name, signature, (access & ACC_STATIC) != 0))) {
							access &= ~(ACC_PRIVATE | ACC_PROTECTED);
							access |= ACC_PUBLIC;
						}
						return super.visitField(access, name, desc, signature, value);
					}

					@Override
					public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
						MethodTransform mt = ct.methodTransforms.get(new FieldSignature(name, signature, (access & ACC_STATIC) != 0));
						if (mt.getUpgradeAccess()) {
							access &= ~(ACC_PRIVATE | ACC_PROTECTED);
							access |= ACC_PUBLIC;
						}
						MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
						Consumer<MethodVisitor> transform = mt.getTransformer();
						if (transform != null) {
							transform.accept(mv);
							return null;
						} else return mv;
					}
				
				};
				ClassReader cr = new ClassReader(classStream);
				cr.accept(cv, 0);
				byte[] bytes = cw.toByteArray();
				return defineClass(name, bytes, 0, bytes.length);
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int read = -1;
				while((read = classStream.read(buf)) != -1) baos.write(buf, 0, read);
				byte[] bytes = baos.toByteArray();
				return defineClass(name, bytes, 0, bytes.length);
			}
		} catch (IOException e) {
			throw new ClassNotFoundException(name, e);
		}
	}
	
}
