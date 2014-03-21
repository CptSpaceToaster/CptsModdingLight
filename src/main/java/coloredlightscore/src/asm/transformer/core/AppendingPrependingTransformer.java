package coloredlightscore.src.asm.transformer.core;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public abstract class AppendingPrependingTransformer extends SingleMethodTransformer
{
	@Override
	protected final boolean transform(ClassNode clazz, MethodNode method)
	{
		method.instructions.insert(getPrepends(clazz, method));
		method.instructions.insertBefore(ASMUtils.findLastReturn(method), getAppends(clazz, method));
		return true;
	}

	protected abstract InsnList getAppends(ClassNode clazz, MethodNode method);

	protected abstract InsnList getPrepends(ClassNode clazz, MethodNode method);
}