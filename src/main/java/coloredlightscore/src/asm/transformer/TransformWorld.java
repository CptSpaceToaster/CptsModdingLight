package coloredlightscore.src.asm.transformer;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
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
        if (methodNode.name.equals("<init>")) { // TODO: make sure we're transforming both the client, and server constructors?
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
        clazz.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "flag_entry", "Lnet/minecraft/world/EnumSkyBlock;", null, null));
        /*
        public static long[] lightAdditionBlockList = new long[32768]; // Note... this is ridiculously huge...  removed the odd backfill on skylights, and this should be something close to 29*29*29 at it's worst
        public static int[][][] lightAdditionNeeded = new int[29][29][29];
        public static int[] lightBackfillIndexes = new int[15]; // indexes for how many values we added at the index's brightness
        public static int[][] lightBackfillBlockList = new int[15][4991]; // theoretical maximum... "I think"
        public static int[][][] lightBackfillNeeded = new int[29][29][29];
        */


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
        CLLog.info("Added clSunColor!");


        CLLog.info("Transformed World constructor!");
        return true;
    }
}
