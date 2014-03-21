package kovukore.coloredlights.src.asm.transformer;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import kovukore.coloredlights.src.asm.transformer.core.HelperMethodTransformer;

public class TransformChunkCache extends HelperMethodTransformer {

	public TransformChunkCache() {
		super("net.minecraft.world.ChunkCache");
	}

	@Override
	protected Class<?> getHelperClass() {
		return kovukore.coloredlights.src.helper.CLChunkCacheHelper.class;
	}
	
	@Override
	protected boolean transforms(ClassNode clazz, MethodNode method) {
		
		return method.name.equals("getLightBrightnessForSkyBlocks");
	}

	@Override
	protected boolean transform(ClassNode clazz, MethodNode method) {
		
		if (method.name.equals("getLightBrightnessForSkyBlocks"))
		{
			return redefineMethod(clazz, method, "getLightBrightnessForSkyBlocks");
		}
		else
			return false;
		
	}

}
