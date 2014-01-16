package com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.impl;

import net.minecraft.world.chunk.NibbleArray;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.ExtendedClassWriter;
import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.TransformerSingleFieldAddition;
import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.TransformerSingleMethod;

public class AddField_ExtendedBlockStorage_A extends TransformerSingleFieldAddition 
{
	@Override
	protected boolean transforms(String arg0)
	{
		return (arg0.equals("net.minecraft.world.chunk.storage.ExtendedBlockStorage") || arg0.equals("adp"));
	}

	@Override
	protected String getTypeDescriptor()
	{
		return "Lnet/minecraft/world/chunk/NibbleArray";
	}

	@Override
	protected String name()
	{
		return "bColorArray";
	}

	@Override
	protected int access()
	{
		return ACC_PROTECTED;
	}
}