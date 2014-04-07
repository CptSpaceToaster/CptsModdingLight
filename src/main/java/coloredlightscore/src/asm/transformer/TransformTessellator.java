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
import coloredlightscore.src.helper.CLRenderBlocksHelper;
import coloredlightscore.src.helper.CLTesselatorHelper;
import cpw.mods.fml.common.FMLLog;

public class TransformTessellator extends HelperMethodTransformer {

	protected String setBrightness = "setBrightness (I)V";
	protected String draw = "draw ()I";
	
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

		if (NameMapper.getInstance().isMethod(methodNode, super.className, draw))
			return true;
				
		return false;		
	}
	@Override
	protected boolean transform(ClassNode clazz, MethodNode method) {
		
		if (NameMapper.getInstance().isMethod(method, super.className, setBrightness))
		{
			int a = 15790080;
			int b = 15790080;
			int c = 15790080;
			int d = 15790080;
			FMLLog.info(">> %s", CLRenderBlocksHelper.getAoBrightness(a, b, c, d));
			
			return redefineMethod(clazz, method, "setBrightness"); // transformSetBrightness(method);
		}
		else if (NameMapper.getInstance().isMethod(method, super.className, draw))
			return redefineMethod(clazz, method, "draw");
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
