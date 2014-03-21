package kovukore.coloredlights.src.asm.transformer.core;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public abstract class PrependingTransformer extends SingleMethodTransformer
{
	@Override
	protected boolean transform(ClassNode clazz, MethodNode method)
	{
		method.instructions.insert(getPrepends(clazz, method));
		return true;
	}

	protected abstract InsnList getPrepends(ClassNode clazz, MethodNode method);
}