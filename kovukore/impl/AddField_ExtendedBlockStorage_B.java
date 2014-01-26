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


public class AddField_ExtendedBlockStorage_B extends TransformerSingleFieldAddition implements Opcodes
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
		return "gColorArray";
	}

	@Override
	protected int access()
	{
		return ACC_PROTECTED;
	}
}