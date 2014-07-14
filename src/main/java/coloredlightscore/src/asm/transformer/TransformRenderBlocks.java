package coloredlightscore.src.asm.transformer;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;

public class TransformRenderBlocks extends HelperMethodTransformer {

    // These methods will be replaced by statics in CLRenderBlocksHelper
    String methodsToReplace[] = { "renderStandardBlockWithAmbientOcclusion (Lnet/minecraft/block/Block;IIIFFF)Z",
                                  "renderStandardBlockWithColorMultiplier (Lnet/minecraft/block/Block;IIIFFF)Z",
                                  "getAoBrightness (IIII)I" };

    public TransformRenderBlocks() {
        super("net.minecraft.client.renderer.RenderBlocks");
    }

    @Override
    protected Class<?> getHelperClass() {
        return coloredlightscore.src.helper.CLRenderBlocksHelper.class;
    }

    @Override
    protected boolean transforms(ClassNode classNode, MethodNode methodNode) {

        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
                return true;
        }

        return false;
    }

    @Override
    protected boolean transform(ClassNode classNode, MethodNode methodNode) {

        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name)) {
                return redefineMethod(classNode, methodNode, name);
            }
        }

        return false;
    }
}
