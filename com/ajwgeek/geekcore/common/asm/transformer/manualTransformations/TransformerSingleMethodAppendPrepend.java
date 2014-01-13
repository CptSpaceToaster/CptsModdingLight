package com.ajwgeek.geekcore.common.asm.transformer.manualTransformations;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public abstract class TransformerSingleMethodAppendPrepend extends TransformerSingleMethod
{
	@Override
	protected final boolean transform(ClassNode clazz, MethodNode method)
	{
		method.instructions.insert(getPrepends(clazz, method));
		method.instructions.insertBefore(UtilsASM.findLastReturn(method), getAppends(clazz, method));
		return true;
	}

	protected abstract InsnList getAppends(ClassNode clazz, MethodNode method);

	protected abstract InsnList getPrepends(ClassNode clazz, MethodNode method);
}