package coloredlightscore.src.asm.transformer;

import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import org.objectweb.asm.tree.*;

/**
 * Created by Murray on 11/19/2014.
 */
public class TransformChunk extends HelperMethodTransformer {

    // These methods will be replaced by statics in CLWorldHelper
    String methodsToReplace[] = { "getBlockLightValue (IIII)I" };

    public TransformChunk() {
        // Inform HelperMethodTransformer which class we are interested in
        super("net.minecraft.world.chunk.Chunk");
    }

    @Override
    protected Class<?> getHelperClass() {
        return coloredlightscore.src.helper.CLChunkHelper.class;
    }

    @Override
    protected boolean transforms(ClassNode classNode, MethodNode methodNode) {
        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, classNode.name, name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean transform(ClassNode classNode, MethodNode methodNode) {
        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, classNode.name, name)) {
                return redefineMethod(classNode, methodNode, name);
            }
        }
        return false;
    }
}
