package coloredlightscore.src.asm.transformer;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
import cpw.mods.fml.common.FMLLog;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;

public class TransformWorld extends HelperMethodTransformer {

    // These methods will be replaced by statics in CLWorldHelper
    String methodsToReplace[] = { "getBlockLightValue_do (IIIZ)I",
                                  "getLightBrightnessForSkyBlocks (IIII)I",
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
        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
                return true;
        }
        if (methodNode.name.equals("<init>")) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean preTransformClass(ClassNode clazz) {
        clazz.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "clSunColor", "[F", null, null));
        return true;
    }

    @Override
    protected boolean transform(ClassNode classNode, MethodNode methodNode) {
        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name)) {
                return redefineMethod(classNode, methodNode, name);
            }
        }
        if (methodNode.name.equals("<init>")) {
            return transformConstructor(methodNode);
        }
        return false;
    }

    private boolean transformConstructor(MethodNode methodNode) {
        InsnList initSunColor = new InsnList();

        // this.clSunColor = new float[]{1.0f, 1.0f, 1.0f};
        initSunColor.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initSunColor.add(new IntInsnNode(Opcodes.BIPUSH, 3));
        initSunColor.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_FLOAT));
        initSunColor.add(new InsnNode(Opcodes.DUP));
        initSunColor.add(new InsnNode(Opcodes.ICONST_0));
        initSunColor.add(new InsnNode(Opcodes.FCONST_1));
        initSunColor.add(new InsnNode(Opcodes.FASTORE));
        initSunColor.add(new InsnNode(Opcodes.DUP));
        initSunColor.add(new InsnNode(Opcodes.ICONST_1));
        initSunColor.add(new InsnNode(Opcodes.FCONST_1));
        initSunColor.add(new InsnNode(Opcodes.FASTORE));
        initSunColor.add(new InsnNode(Opcodes.DUP));
        initSunColor.add(new InsnNode(Opcodes.ICONST_2));
        initSunColor.add(new InsnNode(Opcodes.FCONST_1));
        initSunColor.add(new InsnNode(Opcodes.FASTORE));
        initSunColor.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/World", "clSunColor", "[F"));

        AbstractInsnNode returnNode = ASMUtils.findLastReturn(methodNode);
        methodNode.instructions.insertBefore(returnNode, initSunColor);
        FMLLog.info("Transformed World constructor!");
        return true;
    }
}
