package coloredlightscore.src.asm.transformer;

import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

import cpw.mods.fml.common.FMLLog;

public class TransformEntityRenderer extends HelperMethodTransformer {

	// These methods will be replaced by statics in CLRenderBlocksHelper
	String methodsToReplace[] = {
			"updateLightmap (F)V",
			"enableLightmap (D)V",
			"disableLightmap (D)V"
	};
	
	String constructorSignature = "<init> (Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V";
	String oldLightmapDesc = "net/minecraft/client/renderer/texture/DynamicTexture";
	String newLightmapDesc = "coloredlightscore/src/types/CLDynamicTexture3D";
	
	public TransformEntityRenderer()
	{
		super("net.minecraft.client.renderer.EntityRenderer");
	}

	@Override
	protected Class<?> getHelperClass() {
		return coloredlightscore.src.helper.CLEntityRendererHelper.class;
	}

	@Override
	protected boolean transforms(ClassNode classNode, MethodNode methodNode) {

		for(String name : methodsToReplace)
		{
			if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
				return true;
		}
		
		if ((methodNode.name + " " + methodNode.desc).equals(constructorSignature))
			return true;

		return false;		
	}	

	@Override
	protected boolean transform(ClassNode classNode, MethodNode methodNode) {

		for(String name : methodsToReplace)
		{
			if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
			{
				return redefineMethod(classNode, methodNode, name);
			}
		}		

		if ((methodNode.name + " " + methodNode.desc).equals(constructorSignature))
			return transformConstructor(methodNode);
		
		
		return false;
	}
	
	protected boolean transformConstructor(MethodNode methodNode)
	{
		for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext();) {
	        AbstractInsnNode insn = it.next();
	        if (insn.getOpcode() == Opcodes.NEW) {
	        	if (((TypeInsnNode)insn).desc.equals(oldLightmapDesc)) {
		        	FMLLog.info("We found the 2D monster captain!");
		        	((TypeInsnNode)insn).desc = newLightmapDesc;
		        	FMLLog.info("Set Entityrenderer.lightmapTexture to a " + newLightmapDesc);
		        	it.next(); //DUP
		        	it.next(); //BIPUSH 16
		        	it.next(); //BIPUSH 16
		        	it.add(new IntInsnNode(Opcodes.BIPUSH, 16));
		        	FMLLog.info("Added third argument!");
		        	insn = it.next(); //Constructor call to the CLDynamicTexture3D - INVOKESPECIAL
		        	((MethodInsnNode)insn).owner = "coloredlightscore/src/types/CLDynamicTexture3D";
		        	((MethodInsnNode)insn).name = "<init>"; 
		        	((MethodInsnNode)insn).desc = "(III)V";
		        	FMLLog.info("Called the new constructor!!");
		        	//Delete the next line?  I should probably be a little cleaner about this... but whatever...
		        	for (int i=0; i<9; i++) {
			        	it.next();
			        	it.remove();
		        	}
		        	FMLLog.info("Removed the call to the Texture Manager!!!   PREPARE FOR FAILURE");
		        	return true;
	        	}
	        }
		}
		return false;
	}
}

