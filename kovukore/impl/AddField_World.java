package kovukore.impl;

import kovukore.asm.transformer.manualTransformations.ExtendedClassWriter;
import kovukore.asm.transformer.manualTransformations.TransformerSelective;
import kovukore.asm.transformer.manualTransformations.TransformerSingleFieldAddition;
import kovukore.asm.transformer.manualTransformations.TransformerSingleMethod;
import net.minecraft.world.chunk.NibbleArray;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;


public class AddField_World extends TransformerSingleFieldAddition
{	
	@Override
	protected boolean transforms(String arg0)
	{
		return (arg0.equals("net.minecraft.world.World") || arg0.equals("abw"));
	}
	
	@Override
	protected String getTypeDescriptor()
	{
		return "[L";
	}

	@Override
	protected String name()
	{
		return "lightUpdateBlockList";
	}

	@Override
	protected int access()
	{
		return ACC_PUBLIC;
	}
}