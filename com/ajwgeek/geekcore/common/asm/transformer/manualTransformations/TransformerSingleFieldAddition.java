package com.ajwgeek.geekcore.common.asm.transformer.manualTransformations;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class TransformerSingleFieldAddition extends TransformerSelective implements Opcodes
{
	@Override
	protected boolean transform(ClassNode arg0, String arg1)
	{
		ExtendedClassWriter ecw = new ExtendedClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		arg0.accept(ecw);
		ecw.visitField(access(), name(), type().getDescriptor(), null, null);
		return true;
	}

	protected abstract Type type();
	protected abstract String name();
	protected abstract int access();
	protected abstract boolean transforms(String arg0);
}