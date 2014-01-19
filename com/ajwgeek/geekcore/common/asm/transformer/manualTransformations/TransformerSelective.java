package com.ajwgeek.geekcore.common.asm.transformer.manualTransformations;

import kovukore.coloredlights.asm.ColoredLightsLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public abstract class TransformerSelective implements IClassTransformer
{
	@Override
	public final byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (!ColoredLightsLoadingPlugin.devEnvironment) 
			transformedName = name;
		
		if (bytes != null && transforms(transformedName))
		{
			ClassNode clazz = UtilsASM.getClassNode(bytes);
			if (transform(clazz, transformedName))
			{
				System.out.println("Transmogrifying class " + transformedName);
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
