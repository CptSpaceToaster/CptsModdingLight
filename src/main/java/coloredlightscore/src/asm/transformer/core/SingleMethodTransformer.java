package coloredlightscore.src.asm.transformer.core;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class SingleMethodTransformer extends MethodTransformer
{
	@Override
	protected final boolean transforms(ClassNode clazz, MethodNode method)
	{
		//return method.name.equals(getMcpMethod()) || ASMUtils.deobfuscate(clazz.name, method).equals(getSrgMethod());
		return NameMapper.getInstance().isMethod(method, getClassName(), getMcpMethod());
	}

	protected abstract String getClassName();

	
	protected abstract String getMcpMethod();
}