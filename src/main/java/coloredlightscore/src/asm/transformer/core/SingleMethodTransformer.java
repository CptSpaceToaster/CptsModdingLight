package kovukore.coloredlights.src.asm.transformer.core;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class SingleMethodTransformer extends MethodTransformer
{
	@Override
	protected final boolean transforms(ClassNode clazz, MethodNode method)
	{
		return method.name.equals(getMcpMethod()) || ASMUtils.deobfuscate(clazz.name, method).equals(getSrgMethod());
	}

	protected abstract String getMcpMethod();

	protected abstract String getSrgMethod();
}