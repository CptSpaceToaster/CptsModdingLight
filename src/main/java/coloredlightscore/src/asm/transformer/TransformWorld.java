package coloredlightscore.src.asm.transformer;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.ClassVisitor;
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
        clazz.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "pipe", "Lcoloredlightscore/src/api/CLWorldPipe;", null, null));

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

        //this.pipe = new CLWorldPipe(this);
        initInternalLightVariables.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initInternalLightVariables.add(new TypeInsnNode(Opcodes.NEW, "coloredlightscore/src/api/CLWorldPipe"));
        initInternalLightVariables.add(new InsnNode(Opcodes.DUP));
        initInternalLightVariables.add(new VarInsnNode(Opcodes.ALOAD, 0));
        initInternalLightVariables.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "coloredlightscore/src/api/CLWorldPipe", "<init>", "(Lnet/minecraft/world/World;)V", false));
        initInternalLightVariables.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/World", "pipe", "Lcoloredlightscore/src/api/CLWorldPipe;"));


        AbstractInsnNode firstInvokeSpecial = null;
        for (int i = 0; i < methodNode.instructions.size(); i++) {
            AbstractInsnNode insn = methodNode.instructions.get(i);
            if (insn.getOpcode() == Opcodes.INVOKESPECIAL) {
                firstInvokeSpecial = insn;
                break;
            }
        }


        methodNode.instructions.insert(firstInvokeSpecial, initInternalLightVariables);
        CLLog.info("Transformed World constructor!");
        return true;
    }
}
