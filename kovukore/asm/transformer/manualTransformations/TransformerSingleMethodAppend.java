package kovukore.asm.transformer.manualTransformations;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public abstract class TransformerSingleMethodAppend extends TransformerSingleMethod
{
	@Override
	protected final boolean transform(ClassNode clazz, MethodNode method)
	{
		method.instructions.insertBefore(UtilsASM.findLastReturn(method), getAppends(clazz, method));
		return true;
	}

	protected abstract InsnList getAppends(ClassNode clazz, MethodNode method);
}