package kovukore.coloredlights.src.asm.transformer;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import kovukore.coloredlights.src.asm.transformer.core.HelperMethodTransformer;

public class TransformEntityRenderer extends HelperMethodTransformer {

	// These methods will be replaced by statics in CLRenderBlocksHelper
	String methodsToReplace[] = {
			"updateLightmap"
	};

	public TransformEntityRenderer()
	{
		super("net.minecraft.client.renderer.EntityRenderer");
	}

	@Override
	protected Class<?> getHelperClass() {
		return kovukore.coloredlights.src.helper.CLEntityRendererHelper.class;
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

