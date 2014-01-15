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

public class AddField_ExtendedBlockStorage_C extends TransformerSingleFieldAddition implements Opcodes
{
	@Override
	protected boolean transforms(String arg0)
	{
		return arg0.equals("net.minecraft.world.chunk.storage.ExtendedBlockStorage");
	}

	@Override
	protected Type type()
	{
		//TODO: Nibble Array Can't be referanced in this manner........
		//Crash Report: http://i.imgur.com/uyhV8Gp.png
		//Console Barf: http://i.imgur.com/lz7Cl3P.png
		return Type.getType(NibbleArray.class);
	}

	@Override
	protected String name()
	{
		return "rColorArray";
	}

	@Override
	protected int access()
	{
		return ACC_PROTECTED;
	}
}