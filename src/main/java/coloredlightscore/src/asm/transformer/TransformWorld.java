package coloredlightscore.src.asm.transformer;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;

public class TransformWorld extends HelperMethodTransformer {

    // These methods will be replaced by statics in CLWorldHelper
    String methodsToReplace[] = { "getBlockLightValue_do (IIIZ)I", 
                                  "getLightBrightnessForSkyBlocks (IIII)I", 
                                  "getLightBrightness (III)F", 
                                  "computeLightValue (IIILnet/minecraft/world/EnumSkyBlock;)I",
                                  "updateLightByType (Lnet/minecraft/world/EnumSkyBlock;III)Z" };

    public TransformWorld() {
        // Inform HelperMethodTransformer which class we are interested in
        super("net.minecraft.world.World");
    }

    @Override
    protected Class<?> getHelperClass() {

        return coloredlightscore.src.helper.CLWorldHelper.class;
    }

    @Override
    protected boolean transforms(ClassNode classNode, MethodNode methodNode) {

        //return true; // Update any reference to lightUpdateBlockList

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
