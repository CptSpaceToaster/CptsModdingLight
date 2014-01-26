package kovukore.impl;

import kovukore.asm.transformer.manualTransformations.ExtendedClassWriter;
import kovukore.asm.transformer.manualTransformations.TransformerSingleFieldAddition;
import kovukore.asm.transformer.manualTransformations.TransformerSingleMethod;
import net.minecraft.world.chunk.NibbleArray;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;


public class AddField_ExtendedBlockStorage_C extends TransformerSingleFieldAddition implements Opcodes
{
	@Override
	protected boolean transforms(String arg0)
	{
		return (arg0.equals("net.minecraft.world.chunk.storage.ExtendedBlockStorage") || arg0.equals("adp"));
	}

	@Override
	protected String getTypeDescriptor()
	{
		//TODO: Nibble Array Can't be referenced in this manner........
		//Crash Report: http://i.imgur.com/uyhV8Gp.png
		//Console Barf: http://i.imgur.com/lz7Cl3P.png
		return "Lnet/minecraft/world/chunk/NibbleArray";
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