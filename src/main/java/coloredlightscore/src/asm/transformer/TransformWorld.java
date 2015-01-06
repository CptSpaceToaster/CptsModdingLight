package coloredlightscore.src.asm.transformer;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
import net.minecraft.world.EnumSkyBlock;
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

    EnumSkyBlock pants;
    int dance;

    public TransformWorld() {
        // Inform HelperMethodTransformer which class we are interested in
        super("net.minecraft.world.World");
        pants = EnumSkyBlock.Sky;
        dance = 0;
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

        clazz.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "lightAdditionBlockList", "[J", null, null));
        clazz.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "lightAdditionNeeded", "[[[I", null, null));
        clazz.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "lightBackfillIndexes", "[I", null, null));
        clazz.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "lightBackfillBlockList", "[[I", null, null));
        clazz.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "lightBackfillNeeded", "[[[I", null, null));
        clazz.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "updateFlag", "I", null, null));
        clazz.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "flagEntry", "Lnet/minecraft/world/EnumSkyBlock;", null, null));

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
        InsnList initInternalLightVariables = new InsnList();
        // this.clSunColor = new float[]{1.0f, 1.0f, 1.0f};
        initInternalLightVariables.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.BIPUSH, 3));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_FLOAT));
        initInternalLightVariables.add(new InsnNode(Opcodes.DUP));
        initInternalLightVariables.add(new InsnNode(Opcodes.ICONST_0));
        initInternalLightVariables.add(new InsnNode(Opcodes.FCONST_1));
        initInternalLightVariables.add(new InsnNode(Opcodes.FASTORE));
        initInternalLightVariables.add(new InsnNode(Opcodes.DUP));
        initInternalLightVariables.add(new InsnNode(Opcodes.ICONST_1));
        initInternalLightVariables.add(new InsnNode(Opcodes.FCONST_1));
        initInternalLightVariables.add(new InsnNode(Opcodes.FASTORE));
        initInternalLightVariables.add(new InsnNode(Opcodes.DUP));
        initInternalLightVariables.add(new InsnNode(Opcodes.ICONST_2));
        initInternalLightVariables.add(new InsnNode(Opcodes.FCONST_1));
        initInternalLightVariables.add(new InsnNode(Opcodes.FASTORE));
        initInternalLightVariables.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/World", "clSunColor", "[F"));

        //this.lightAdditionBlockList = new long[32768];
        initInternalLightVariables.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initInternalLightVariables.add(new LdcInsnNode(32768));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_LONG));
        initInternalLightVariables.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/World", "lightAdditionBlockList", "[J"));

        //this.lightAdditionNeeded = new int[29][29][29];
        initInternalLightVariables.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.BIPUSH, 29));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.BIPUSH, 29));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.BIPUSH, 29));
        initInternalLightVariables.add(new MultiANewArrayInsnNode("[[[I", 3));
        initInternalLightVariables.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/World", "lightAdditionNeeded", "[[[I"));

        //this.lightBackfillIndexes = new int[15];
        initInternalLightVariables.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.BIPUSH, 15));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_INT));
        initInternalLightVariables.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/World", "lightBackfillIndexes", "[I"));

        //this.lightBackfillBlockList = new int[15][4991];
        initInternalLightVariables.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.BIPUSH, 15));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.SIPUSH, 4991));
        initInternalLightVariables.add(new MultiANewArrayInsnNode("[[I", 2));
        initInternalLightVariables.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/World", "lightBackfillBlockList", "[[I"));

        //this.lightBackfillNeeded = new int[29][29][29];
        initInternalLightVariables.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.BIPUSH, 29));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.BIPUSH, 29));
        initInternalLightVariables.add(new IntInsnNode(Opcodes.BIPUSH, 29));
        initInternalLightVariables.add(new MultiANewArrayInsnNode("[[[I", 3));
        initInternalLightVariables.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/World", "lightBackfillNeeded", "[[[I"));

        //this.updateFlag = 1;
        initInternalLightVariables.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initInternalLightVariables.add(new InsnNode(Opcodes.ICONST_1)); // started at one, because the array is full of zeros
        initInternalLightVariables.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/World", "updateFlag", "I"));

        //this.flagEntry = EnumSkyBlock.Block;
        initInternalLightVariables.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initInternalLightVariables.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/world/EnumSkyBlock", "Block", "Lnet/minecraft/world/EnumSkyBlock;"));
        initInternalLightVariables.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/World", "flagEntry", "Lnet/minecraft/world/EnumSkyBlock;"));

        AbstractInsnNode returnNode = ASMUtils.findLastReturn(methodNode);
        methodNode.instructions.insertBefore(returnNode, initInternalLightVariables);
        CLLog.info("Transformed World constructor!");
        return true;
    }
}
