package kovukore.coloredlights.src.asm.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import cpw.mods.fml.common.FMLLog;
import kovukore.coloredlights.src.asm.transformer.core.HelperMethodTransformer;

public class TransformWorld extends HelperMethodTransformer {

	// These methods will be replaced by statics in CLWorldHelper
	String methodsToReplace[] = {
			"getBlockLightValue_do",
			"getLightBrightnessForSkyBlocks",
			"getLightBrightness",
			"computeLightValue",
			"updateLightByType"
	};
	
	//private FieldNode lightUpdateBlockList;
	//private boolean translatedFields = false;
	
	public TransformWorld() {
		// Inform HelperMethodTransformer which class we are interested in
		super("net.minecraft.world.World");		
	}

	@Override
	protected Class<?> getHelperClass() {
		
		return kovukore.coloredlights.src.helper.CLWorldHelper.class;
	}

	
	@Override
	protected boolean transforms(ClassNode classNode, MethodNode methodNode) {

		//return true; // Update any reference to lightUpdateBlockList
				
		for(String name : methodsToReplace)
		{
			if (methodNode.name.equals(name))
				return true;
		}
		
		return false;		
	}	

	@Override
	protected boolean transform(ClassNode classNode, MethodNode methodNode) {
		
		boolean changed = false;
		
		//if (!translatedFields)
			//translateFields(classNode);
		
		//changed |= applyNewFieldDescriptor(classNode, methodNode, lightUpdateBlockList);
		
		//if (methodNode.name.equals("<init>"))
			//return translateConstructor(methodNode);
		
		for(String name : methodsToReplace)
		{
			if (methodNode.name.equals(name))
			{
				return redefineMethod(classNode, methodNode, name);
			}
		}		
		
		return changed;
	}
	
//	private void translateFields(ClassNode classNode)
//	{
//		for (FieldNode f : classNode.fields)
//		{
//			if (f.name.equals("lightUpdateBlockList") && f.desc.equals("[I"))
//			{
//				lightUpdateBlockList = f;
//				
//				// Change from int[] to long[]
//				f.desc = "[J";
//			}
//		}
//		
//		translatedFields = true;
//	}
	
//	private boolean translateConstructor(MethodNode ctor)
//	{
//	    224  ldc <Integer 32768> [59]
//	    226  newarray int [10]    ----> newarray long [11]
//	    228  putfield net.minecraft.world.World.lightUpdateBlockList : int[] [60]		
//		
//		for (int i=0;i<ctor.instructions.size();i++)
//		{
//			AbstractInsnNode insn = ctor.instructions.get(i);
//			
//			if (insn.getOpcode() == Opcodes.PUTFIELD)
//			{
//				FieldInsnNode finsn = (FieldInsnNode)insn;
//				
//				if (finsn.name.equals(lightUpdateBlockList.name) && finsn.desc.equals(lightUpdateBlockList.desc))
//				{
//					//Already done by applyNewFieldDescriptor: finsn.desc = lightUpdateBlockList.desc;
//					
//					IntInsnNode newArray = (IntInsnNode)ctor.instructions.get(i - 1);
//					
//					newArray.operand = 11;
//				}
//			}
//		}
//		
//		return true;
//	}
	
//	private boolean applyNewFieldDescriptor(ClassNode c, MethodNode m, FieldNode f)
//	{
//		// Update all GETFIELD/PUTFIELD instructions to use new descriptor
//		boolean changed = false;
//
//		FMLLog.info("Looking for GET/PUT to %s in %s.%s", f.name, c.name, m.name);
//		
//		for (int i=0;i<m.instructions.size();i++)
//		{
//			AbstractInsnNode insn = m.instructions.get(i);
//			
//			if (insn.getOpcode() == Opcodes.PUTFIELD || insn.getOpcode() == Opcodes.GETFIELD)
//			{
//				FieldInsnNode finsn = (FieldInsnNode)insn;
//				
//				if (finsn.name.equals(f.name) && finsn.owner.equals(c.name))
//				{
//					finsn.desc = f.desc;
//					changed = true;
//					
//					FMLLog.info("Updated field descriptor %s in %s.%s", f.name, c.name, m.name);
//				}
//			}
//		}	
//		
//		return changed;
//	}
}
