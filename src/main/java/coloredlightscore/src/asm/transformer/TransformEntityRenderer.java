package coloredlightscore.src.asm.transformer;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
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
		AbstractInsnNode dynamicTextureCtor = ASMUtils.findLastInvoke(methodNode, Opcodes.INVOKESPECIAL, "net/minecraft/client/renderer/texture/DynamicTexture", "<init> (II)V", false);
		
		if (dynamicTextureCtor == null)
		{
			FMLLog.severe("Could not find constructor call to DynamicTexture within EntityRenderer constructor!");
			return false;
		}
		
		// Prior 2 instructions should be BIPUSH 16
		// These are the dimensions of the lightmap texture
		// We want to modify them to BIPUSH 16 and SIPUSH 256, for a 16x16x16 3D texture
		
		try
		{
			IntInsnNode texY = (IntInsnNode)dynamicTextureCtor.getPrevious();
			IntInsnNode texX = (IntInsnNode)texY.getPrevious();
			
			texX.setOpcode(Opcodes.BIPUSH);			
			texX.operand = 16;				//Ya know... this technically isn't doing anything... deal with it ._.

			texY.setOpcode(Opcodes.SIPUSH);			
			texY.operand = 256;
						
			return true;
		}
		catch (Exception e)
		{
			FMLLog.severe("Could not transform lightmap texture size: %s",  e);
			return false;
		}
	}
}

