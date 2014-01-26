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


public class AddField_Block extends TransformerSingleFieldAddition implements Opcodes
{
	@Override
	protected boolean transform(ClassNode arg0, String arg1)
	{
		ExtendedClassWriter ecw = new ExtendedClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		arg0.accept(ecw);
		ecw.visitField(access(), name(), "[F", null, null).visitEnd();
		return true;
	}
	
	@Override
	protected boolean transforms(String arg0)
	{
		return arg0.equals("net.minecraft.block.Block") || arg0.equals("aqz");
	}

	public static Float[] l;
	
	static
	{
		l = new Float[]{0F, 1F / 15, 2F / 15, 3F / 15, 4F / 15, 5F / 15, 6F / 15, 7F / 15, 8F / 15, 9F / 15, 10F / 15, 11F / 15, 12F / 15, 13F / 15, 14F / 15, 1F }; 
	}
	
	@Override
	protected String getTypeDescriptor()
	{
		return null;
	}

	@Override
	protected String name()
	{
		return "l";
	}

	@Override
	protected int access()
	{
		return ACC_PUBLIC + ACC_STATIC;
	}
}