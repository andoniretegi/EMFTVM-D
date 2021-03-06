/*******************************************************************************
 * Copyright (c) 2011-2014 Dennis Wagelaar, Vrije Universiteit Brussel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Wagelaar, Vrije Universiteit Brussel
 *******************************************************************************/
package org.eclipse.m2m.atl.emftvm.jit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.m2m.atl.common.ATLLogger;
import org.eclipse.m2m.atl.emftvm.CodeBlock;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Instruction;
import org.eclipse.m2m.atl.emftvm.util.StackFrame;
import org.eclipse.m2m.atl.emftvm.util.VMException;
import org.eclipse.m2m.atl.emftvm.util.VMMonitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * JIT that generates and loads a natively generated {@link CodeBlock}
 * for a given EMFTVM bytecode {@link CodeBlock}.
 * @author <a href="mailto:dennis.wagelaar@vub.ac.be">Dennis Wagelaar</a>
 */
public class CodeBlockJIT implements Opcodes {

	public static final String BASE_PACKAGE = "org.eclipse.m2m.atl.emftvm.jit.generated";

	private int counter = 0;
	private boolean dumpBytecode = false;

	/**
	 * {@link ClassLoader} that loads classes generated by the JIT compiler.
	 */
	class JITClassLoader extends ClassLoader {

		/**
		 * Creates a new {@link JITClassLoader}.
		 */
		public JITClassLoader(final ClassLoader parent) {
			super(parent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			if (codeBlocks.containsKey(name)) {
				final byte[] b = internalJit(codeBlocks.get(name), name);
				byteCode.put(name, b);
				if (isDumpBytecode()) {
					dumpByteCode(name, b);
				}
				return defineClass(name, b, 0, b.length);
			}
			return super.findClass(name);
		}
	}

	/**
	 * {@link Map} of class names to input {@link CodeBlock}s for the JIT.
	 */
	protected final Map<String, CodeBlock> codeBlocks = Collections.synchronizedMap(new HashMap<String, CodeBlock>());
	/**
	 * Internal {@link ClassLoader} instance for loading the generated classes.
	 */
	protected final JITClassLoader classLoader = new JITClassLoader(getClass().getClassLoader());
	/**
	 * The execution environment to JIT for.
	 */
	protected final ExecEnv env;
	/**
	 * {@link Map} of class names to generated bytecode.
	 */
	protected final Map<String, byte[]> byteCode = Collections.synchronizedMap(new HashMap<String, byte[]>());

	/**
	 * Creates a new {@link CodeBlockJIT}.
	 * @param env the execution environment to JIT for
	 */
	public CodeBlockJIT(final ExecEnv env) {
		super();
		this.env = env;
	}

	/**
	 * JIT-compiles <code>cb</code> into a natively implemented {@link CodeBlock}.
	 * @param cb the regular EMFTVM bytecode {@link CodeBlock}
	 * @return an instance of the JIT-generated {@link CodeBlock}
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public JITCodeBlock jit(final CodeBlock cb) throws ClassNotFoundException,
			IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		final String className = getNextClassName();
		codeBlocks.put(className, cb);
		try {
			return (JITCodeBlock)classLoader.findClass(className)
					.getConstructor(CodeBlock.class).newInstance(cb);
		} catch (VerifyError e) {
			final byte[] b = byteCode.get(className);
			if (b != null) {
				dumpByteCode(className, b);
			}
			throw e;
		}
	}
	
	/**
	 * Returns the next class name to use for JIT output.
	 * @return the next class name to use for JIT output
	 */
	protected synchronized String getNextClassName() {
		return BASE_PACKAGE + ".CB" + counter++;
	}

	/**
	 * Performs the actual JIT-compile of <code>cb</code> into a {@link JITCodeBlock} class
	 * with name <code>className</code>.
	 * @param cb the {@link CodeBlock} to JIT-compile
	 * @param className the class name for the generated class
	 * @return the generated class data
	 */
	protected byte[] internalJit(final CodeBlock cb, final String className) {
		final String internalName = className.replace('.', '/');
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cw.visit(V1_5, ACC_PUBLIC, internalName, null, Type.getInternalName(JITCodeBlock.class), new String[0]);
		generateConstructor(
				cw.visitMethod(ACC_PUBLIC, 
						"<init>", 
						Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{Type.getType(CodeBlock.class)}), 
						null, 
						null),
				internalName);
		generateExecute(
				cw.visitMethod(ACC_PUBLIC, 
						"execute",
						Type.getMethodDescriptor(Type.getType(Object.class), new Type[] { Type.getType(StackFrame.class) }),
						null, 
						null),
				cb,
				internalName);
		cw.visitEnd();
		return cw.toByteArray();
	}

	/**
	 * Generates a constructor for the {@link JITCodeBlock}.
	 * @param init the constructor visitor
	 * @param className the name of the generated class
	 */
	protected static void generateConstructor(final MethodVisitor init, final String className) {
		init.visitCode();
		// Generate labels
		final Label start = new Label();
		final Label end = new Label();
		// Generate bytecode
		init.visitLabel(start);
		init.visitVarInsn(ALOAD, 0); // this
		init.visitVarInsn(ALOAD, 1); // cb
		init.visitMethodInsn(INVOKESPECIAL, // super(cb) 
				Type.getInternalName(JITCodeBlock.class), 
				"<init>", 
				Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{Type.getType(CodeBlock.class)}));
		init.visitInsn(RETURN);
		init.visitLabel(end);
		// Create local variable table
		init.visitLocalVariable("this", "L" + className + ";", null, start, end, 0);
		init.visitLocalVariable("cb", Type.getDescriptor(CodeBlock.class), null, start, end, 1);
		// Finalise
		init.visitMaxs(2, 2);
		init.visitEnd();
	}

	/**
	 * Generates an execute method for the {@link JITCodeBlock}.
	 * @param execute the execute method visitor
	 * @param cb the {@link CodeBlock} to JIT-compile
	 * @param className the name of the generated class
	 */
	protected void generateExecute(final MethodVisitor execute, final CodeBlock cb, final String className) {
		execute.visitCode();
		// Generate labels
		final LabelSwitch ls = new LabelSwitch();
		for (Instruction instr : cb.getCode()) {
			ls.doSwitch(instr);
		}
		// Default labels
		final Label start = new Label();
		final Label end = new Label();
		final Label tryStart = new Label();
		final Label vmExceptionHandler = new Label();
		final Label exceptionHandler = new Label();
		final Label catchEnd = new Label();
		// Create exception table
		execute.visitTryCatchBlock(tryStart, vmExceptionHandler, vmExceptionHandler, Type.getInternalName(VMException.class));
		execute.visitTryCatchBlock(tryStart, vmExceptionHandler, exceptionHandler, Type.getInternalName(Exception.class));
		// Generate bytecode
		execute.visitLabel(start);
		execute.visitVarInsn(ALOAD, 1); // frame
		execute.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(StackFrame.class), "getEnv", Type.getMethodDescriptor(Type.getType(ExecEnv.class), new Type[0]));
		execute.visitVarInsn(ASTORE, 2);
		execute.visitVarInsn(ALOAD, 2); // env
		execute.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(ExecEnv.class), "getMonitor", Type.getMethodDescriptor(Type.getType(VMMonitor.class), new Type[0]));
		execute.visitVarInsn(ASTORE, 3); // monitor
		final boolean hasMonitor = getEnv().getMonitor() != null;
		if (hasMonitor) {
			execute.visitVarInsn(ALOAD, 3); // monitor: [..., monitor]
			execute.visitVarInsn(ALOAD, 1); // frame: [..., monitor, frame]
			execute.visitMethodInsn(INVOKEINTERFACE, // monitor.enter(frame): [...]
					Type.getInternalName(VMMonitor.class), 
					"enter", 
					Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{
							Type.getType(StackFrame.class)
					}));
		}
		execute.visitLabel(tryStart);
		final ByteCodeSwitch bs = new ByteCodeSwitch(this, execute, ls);
		final EList<Instruction> code = cb.getCode();
		for (Instruction instr : code) {
			if (hasMonitor) {
				// do checkMonitor() before each instruction
				generateCheckMonitor(execute, code.indexOf(instr) + 1); // pc
			}
			// generate instruction-specific code
			bs.doSwitch(instr);
		}
		execute.visitJumpInsn(GOTO, catchEnd);
		// catch (VMException e)
		execute.visitLabel(vmExceptionHandler);
		execute.visitVarInsn(ASTORE, 4); // e
		execute.visitVarInsn(ALOAD, 4); // e
		execute.visitInsn(ATHROW); // throw e
		// catch (Exception e)
		execute.visitLabel(exceptionHandler);
		execute.visitVarInsn(ASTORE, 4); // e
		execute.visitTypeInsn(NEW, Type.getInternalName(VMException.class)); // new VMException(): [vme, ...]
		execute.visitInsn(DUP); // [vme, vme, e, ...]
		execute.visitVarInsn(ALOAD, 1); // frame: [frame, vme, vme, ...]
		execute.visitVarInsn(ALOAD, 4); // e: [e, frame, vme, vme, ...]
		execute.visitMethodInsn(INVOKESPECIAL, // new VMException(frame, e): [vme, ...]
				Type.getInternalName(VMException.class), 
				"<init>", 
				Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{
						Type.getType(StackFrame.class),
						Type.getType(Throwable.class)
				}));
		execute.visitInsn(ATHROW); // throw vme
		// Regular method return
		execute.visitLabel(catchEnd);
		if (hasMonitor) {
			execute.visitVarInsn(ALOAD, 3); // monitor: [..., monitor]
			execute.visitVarInsn(ALOAD, 1); // frame: [..., monitor, frame]
			execute.visitMethodInsn(INVOKEINTERFACE, // monitor.leave(frame): [...]
					Type.getInternalName(VMMonitor.class), 
					"leave", 
					Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{
							Type.getType(StackFrame.class)
					}));
		}
		if (cb.getStackLevel() == 0) {
			execute.visitInsn(ACONST_NULL); // push null
		}
		execute.visitInsn(ARETURN); // return result
		execute.visitLabel(end);
		// Create local variable table
		execute.visitLocalVariable("this", "L" + className + ";", null, start, end, 0);
		execute.visitLocalVariable("frame", Type.getDescriptor(StackFrame.class), null, start, end, 1);
		execute.visitLocalVariable("env", Type.getDescriptor(ExecEnv.class), null, start, end, 2);
		execute.visitLocalVariable("monitor", Type.getDescriptor(VMMonitor.class), null, start, end, 3);
		execute.visitLocalVariable("e", Type.getDescriptor(VMException.class), null, vmExceptionHandler, exceptionHandler, 4);
		execute.visitLocalVariable("e", Type.getDescriptor(Exception.class), null, exceptionHandler, catchEnd, 4);
		// Finalise
		execute.visitMaxs(0, 0); // recalculate 
		execute.visitEnd();
	}

	/**
	 * Generates bytecode for checking the VM monitor
	 * @param mv the method visitor to generate code for
	 * @param pc the current program counter
	 */
	protected static void generateCheckMonitor(final MethodVisitor mv, final int pc) {
		// Labels
		final Label notTerminated = new Label();
		// Generate bytecode
		mv.visitVarInsn(ALOAD, 3); // monitor: [..., monitor]
		mv.visitMethodInsn(INVOKEINTERFACE, // monitor.isTerminated(): [..., boolean]
				Type.getInternalName(VMMonitor.class), 
				"isTerminated", 
				Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[0]));
		mv.visitJumpInsn(IFEQ, notTerminated); // jump if isTerminated == false: [...]
		mv.visitTypeInsn(NEW, Type.getInternalName(VMException.class)); // new VMException: [..., vme]
		mv.visitInsn(DUP); // [..., vme, vme]
		mv.visitVarInsn(ALOAD, 1); // frame: [..., vme, vme, frame]
		mv.visitLdcInsn("Execution terminated."); // [..., vme, vme, frame, msg]
		mv.visitMethodInsn(INVOKESPECIAL, // vme.<init>(frame, msg): [..., vme]
				Type.getInternalName(VMException.class), 
				"<init>", 
				Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{
						Type.getType(StackFrame.class),
						Type.getType(String.class)
				}));
		mv.visitInsn(ATHROW); // throw vme: [...]
		mv.visitLabel(notTerminated);
		mv.visitVarInsn(ALOAD, 1); // frame: [..., frame]
		generatePushInt(mv, pc); // [..., frame, pc]
		mv.visitMethodInsn(INVOKEVIRTUAL, // frame.setPc(pc): [...] 
				Type.getInternalName(StackFrame.class), 
				"setPc", 
				Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{
						Type.INT_TYPE
				}));
		mv.visitVarInsn(ALOAD, 3); // monitor: [..., monitor]
		mv.visitVarInsn(ALOAD, 1); // frame: [..., monitor, frame]
		mv.visitMethodInsn(INVOKEINTERFACE, // monitor.step(frame): [...]
				Type.getInternalName(VMMonitor.class), 
				"step", 
				Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{
						Type.getType(StackFrame.class)
				}));
	}

	/**
	 * The execution environment to JIT for.
	 * @return the env
	 */
	public ExecEnv getEnv() {
		return env;
	}

	/**
	 * Generates an optimised instruction for pushing a constant integer <code>value</code>
	 * onto the stack.
	 * @param value the constant integer value to push
	 */
	static void generatePushInt(final MethodVisitor mv, final int value) {
		if (value >= -1 && value <= 5) {
			switch (value) {
			case -1: mv.visitInsn(ICONST_M1); break;
			case 0:  mv.visitInsn(ICONST_0);  break;
			case 1:  mv.visitInsn(ICONST_1);  break;
			case 2:  mv.visitInsn(ICONST_2);  break;
			case 3:  mv.visitInsn(ICONST_3);  break;
			case 4:  mv.visitInsn(ICONST_4);  break;
			default: mv.visitInsn(ICONST_5);  assert value == 5; break;
			}
		} else if (value < Byte.MAX_VALUE && value > Byte.MIN_VALUE) {
			mv.visitIntInsn(BIPUSH, value);
		} else if (value < Short.MAX_VALUE && value > Short.MIN_VALUE) {
			mv.visitIntInsn(SIPUSH, value);
		} else {
			mv.visitLdcInsn(value);
		}
	}

	/**
	 * Returns whether or not to dump the generated bytecode to a class file.
	 * @return whether or not to dump the generated bytecode to a class file
	 */
	public boolean isDumpBytecode() {
		return dumpBytecode;
	}

	/**
	 * Sets whether or not to dump the generated bytecode to a class file.
	 * @param dumpBytecode whether or not to dump the generated bytecode to a class file
	 */
	public void setDumpBytecode(boolean dumpBytecode) {
		this.dumpBytecode = dumpBytecode;
	}

	/**
	 * Cleans up all generated bytecode.
	 */
	public synchronized void cleanup() {
		for (CodeBlock cb : codeBlocks.values()) {
			cb.setJITCodeBlock(null);
		}
		codeBlocks.clear();
		byteCode.clear();
	}
	
	/**
	 * Dumps generated bytecode to the temp directory.
	 * @param name the class name
	 * @param b the bytecode to dump
	 */
	private void dumpByteCode(String name, byte[] b) {
		try {
			final String path = name.substring(0, name.lastIndexOf('.')).replace('.', File.separatorChar);
			final File p = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + path);
			p.mkdirs();
			final File f = new File(p, name.substring(name.lastIndexOf('.') + 1) + ".class");
			f.createNewFile();
			final FileOutputStream fos = new FileOutputStream(f);
			try {
				fos.write(b);
			} finally {
				fos.close();
			}
			ATLLogger.info(String.format("Wrote JIT-ed code block %s to %s", 
					codeBlocks.get(name), f.getAbsolutePath()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
