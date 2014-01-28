package kovukore.coloredlightscore.src.asm.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import kovukore.coloredlightscore.src.asm.helpers.HelperBlock;
import kovukore.coloredlightscore.src.asm.transformer.core.ASMUtils;
import kovukore.coloredlightscore.src.asm.transformer.core.AppendingTransformer;

public class BlockTransformer_Init extends AppendingTransformer implements Opcodes
{
	@Override
	protected InsnList getAppends(ClassNode clazz, MethodNode method)
	{
		InsnList insns = new InsnList();
		insns.add(ASMUtils.generateStaticMethodCall(HelperBlock.class.getName(), "initBlockLightHelper", Type.VOID_TYPE));
		return insns;
	}

	@Override
	protected String getMcpMethod()
	{
		return "<init>";
	}

	@Override
	protected String getSrgMethod()
	{
		return "<init>";
	}

	@Override
	protected boolean transforms(String className)
	{
		return className.equals("net.minecraft.block.Block");
	}
}