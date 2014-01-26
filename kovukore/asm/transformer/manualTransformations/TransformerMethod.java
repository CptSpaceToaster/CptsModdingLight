package kovukore.asm.transformer.manualTransformations;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class TransformerMethod extends TransformerSelective
{
	@Override
	protected final boolean transform(ClassNode clazz, String className)
	{
		for (MethodNode method : clazz.methods)
		{
			if (transforms(clazz, method))
			{
				System.out.println("Transforming method " + method.name);
				return transform(clazz, method);
			}
		}
		return false;
	}

	protected abstract boolean transforms(ClassNode clazz, MethodNode method);

	protected abstract boolean transform(ClassNode clazz, MethodNode method);
}