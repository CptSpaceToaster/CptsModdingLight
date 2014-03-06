package kovukore.coloredlights.src.asm.transformer;

import kovukore.coloredlights.src.asm.transformer.core.HelperMethodTransformer;

import org.objectweb.asm.tree.*;

public class TransformRenderBlocks extends HelperMethodTransformer {

	// These methods will be replaced by statics in CLRenderBlocksHelper
	String methodsToReplace[] = {
			"renderStandardBlockWithAmbientOcclusion",
			"renderStandardBlockWithColorMultiplier"
	};	
	
	public TransformRenderBlocks()
	{
		super("net.minecraft.client.renderer.RenderBlocks");
	}
	
	@Override
	protected Class<?> getHelperClass() {
		
		return kovukore.coloredlights.src.helper.CLRenderBlocksHelper.class;
	}

	
	@Override
	protected boolean transforms(ClassNode classNode, MethodNode methodNode) {

		for(String name : methodsToReplace)
		{
			if (methodNode.name.equals(name))
				return true;
		}
		
		return false;		
	}	

	@Override
	protected boolean transform(ClassNode classNode, MethodNode methodNode) {
		
		for(String name : methodsToReplace)
		{
			if (methodNode.name.equals(name))
			{
				return redefineMethod(classNode, methodNode, name);
			}
		}		
		
		return false;
	}
	
}
