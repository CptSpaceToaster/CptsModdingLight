package kovukore.coloredlights.src.asm.transformer;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

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
