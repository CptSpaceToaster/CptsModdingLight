package com.ajwgeek.geekcore.common.asm.transformer.manualTransformations;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class TransformerSingleMethod extends TransformerMethod
{
	@Override
	protected final boolean transforms(ClassNode clazz, MethodNode method)
	{
		return method.name.equals(getMcpMethod()) || UtilsASM.deobfuscate(clazz.name, method).equals(getSrgMethod());
	}

	protected abstract String getMcpMethod();

	protected abstract String getSrgMethod();
}
