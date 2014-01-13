package com.ajwgeek.geekcore.common.asm.transformer.manualTransformations;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public abstract class TransformerSelective implements IClassTransformer
{
	@Override
	public final byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (bytes != null && transforms(transformedName))
		{
			ClassNode clazz = UtilsASM.getClassNode(bytes);
			if (transform(clazz, transformedName))
			{
				System.out.println("Transforming class " + transformedName);
				ClassWriter writer = new ExtendedClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				clazz.accept(writer);
				bytes = writer.toByteArray();
			}
		}
		return bytes;
	}

	protected abstract boolean transforms(String className);

	protected abstract boolean transform(ClassNode clazz, String className);
}
