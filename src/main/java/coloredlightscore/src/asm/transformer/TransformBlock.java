package kovukore.coloredlights.src.asm.transformer;

import kovukore.coloredlights.src.asm.transformer.core.HelperMethodTransformer;
import org.objectweb.asm.tree.*;

public class TransformBlock extends HelperMethodTransformer {
	
	public TransformBlock() {
		// Inform HelperMethodTransformer which class we are interested in
		super("net.minecraft.block.Block");		
	}

	@Override
	protected Class<?> getHelperClass() {
		
		// We should promote a 1:1 correlation between vanilla classes and helper classes
		return kovukore.coloredlights.src.helper.CLBlockHelper.class;
	}

	
	@Override
	protected boolean transforms(ClassNode classNode, MethodNode methodNode) {

		return methodNode.name.equals("setLightLevel");
		
	}	

	@Override
	protected boolean transform(ClassNode classNode, MethodNode methodNode) {
		
		if (methodNode.name.equals("setLightLevel"))
		{
			return addReturnMethod(classNode, methodNode, "setLightLevel");
		}
		else
			return false;
	}
}
