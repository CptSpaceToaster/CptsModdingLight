package coloredlightscore.src.asm.transformer;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;

public class TransformBlock extends HelperMethodTransformer {

    public TransformBlock() {
        // Inform HelperMethodTransformer which class we are interested in
        super("net.minecraft.block.Block");
    }

    @Override
    protected Class<?> getHelperClass() {

        // We should promote a 1:1 correlation between vanilla classes and helper classes
        return coloredlightscore.src.helper.CLBlockHelper.class;
    }

    @Override
    protected boolean transforms(ClassNode classNode, MethodNode methodNode) {
        return NameMapper.getInstance().isMethod(methodNode, super.className, "setLightLevel (F)Lnet/minecraft/block/Block;");
    }

    @Override
    protected boolean transform(ClassNode classNode, MethodNode methodNode) {

        if (NameMapper.getInstance().isMethod(methodNode, super.className, "setLightLevel (F)Lnet/minecraft/block/Block;"))
        //if ((methodNode.name + " " + methodNode.desc).equals("setLightLevel (F)Lnet/minecraft/block/Block;"))
        {
            return addReturnMethod(classNode, methodNode, "setLightLevel");
        } else
            return false;
    }
}
