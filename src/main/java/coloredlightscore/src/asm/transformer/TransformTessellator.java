package coloredlightscore.src.asm.transformer;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;

public class TransformTessellator extends HelperMethodTransformer {

	// These methods will be replaced by statics in CLRenderBlocksHelper
	String methodsToReplace[] = {
			"addVertex (DDD)V",
			"setBrightness (I)V"
			
	};
	
	public TransformTessellator()
	{
		super("net.minecraft.client.renderer.Tessellator");
	}
	
	@Override
	protected Class<?> getHelperClass() {
		return coloredlightscore.src.helper.CLTessellatorHelper.class;
	}

	
	@Override
	protected boolean transforms(ClassNode classNode, MethodNode methodNode) {

		for(String name : methodsToReplace)
		{
			System.out.println(" :" + methodNode.name + " " + methodNode.desc);
			
			if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
				return true;
		}
		
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
		
		return false;
	}
	
}
