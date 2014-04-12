package coloredlightscore.src.asm.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import coloredlightscore.src.helper.CLTesselatorHelper;

public class TransformTessellator extends HelperMethodTransformer {

	protected String setBrightness = "setBrightness (I)V";
	
	public TransformTessellator() {
		super("net.minecraft.client.renderer.Tessellator");
	}

	@Override
	protected Class<?> getHelperClass() {
		return CLTesselatorHelper.class;
	}	
	
	@Override
	protected boolean transforms(ClassNode classNode, MethodNode methodNode) {

		if (NameMapper.getInstance().isMethod(methodNode, super.className, setBrightness))
			return true;
				
		return false;		
	}
	@Override
	protected boolean transform(ClassNode clazz, MethodNode method) {
		
		if (NameMapper.getInstance().isMethod(method, super.className, setBrightness))
			return redefineMethod(clazz, method, "setBrightness"); // transformSetBrightness(method);
		else
			return false;

	}

	private boolean transformSetBrightness(MethodNode method)
	{
        /* 
         * This ASM was taken from the mockup "CLTesselatorHelper.setBrightness", as compared to Tessellator.class
         * We want to insert the lines starting with '+':
         * 
         * 0  aload_0 [this]
         * 1  iconst_1
         * 2  putfield kovukore.coloredlights.src.helper.CLTesselatorHelper.hasBrightness : boolean [20]
         * 5  aload_0 [this]
         * 6  iload_1 [par1]
         * + 7  ldc <Integer 15728880> [22]
         * + 9  iand
         * 10  putfield kovukore.coloredlights.src.helper.CLTesselatorHelper.brightness : int [23]
         * 13  return
         * 
         */		
		
		AbstractInsnNode putBrightness = ASMUtils.findLastOpcode(method, Opcodes.PUTFIELD);
		InsnList andOperation = new InsnList();
		
		andOperation.add(new LdcInsnNode((int)15728880));
		andOperation.add(new InsnNode(Opcodes.IAND));
		
		method.instructions.insertBefore(putBrightness, andOperation);
						
		return true;
	}

}
