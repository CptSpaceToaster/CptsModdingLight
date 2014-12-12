package coloredlightscore.src.asm.transformer;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import java.util.ListIterator;

import coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
import coloredlightscore.src.asm.transformer.core.ExtendedClassWriter;
import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;

import com.google.common.base.Throwables;

public class TransformTessellator extends HelperMethodTransformer {
    String unObfBrightness = "hasBrightness";
    String obfBrightness = "field_78414_p"; //It could also be field_147580_e (trianglecube36: I checked this it is field_78414_p)
    String unObfTexture = "hasTexture";
    String obfTexture = "field_78400_o";
    String unObfByteBuffer = "byteBuffer";
    String obfByteBuffer = "field_78394_d";
    boolean byteBufferIsStatic = false;

    // These methods will be replaced by statics in CLTessellatorHelper
    String methodsToReplace[] = { "addVertex (DDD)V" };
    String constructorToReplace = "<clinit> ()V";


    public TransformTessellator() {
        super("net/minecraft/client/renderer/Tessellator");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes != null && transforms(transformedName)) {
            CLLog.info("Class {} is a candidate for transforming", transformedName);

            try {
                ClassNode clazz = ASMUtils.getClassNode(bytes);

                if (transform(clazz, transformedName)) {
                    CLLog.info("Finished Transforming class " + transformedName);
                    ClassWriter writer = new ExtendedClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                    clazz.accept(writer);
                    bytes = writer.toByteArray();
                } else
                    CLLog.warn("Did not transform {}", transformedName);
            } catch (Exception e) {
                CLLog.error("Exception during transformation of class " + transformedName);
                e.printStackTrace();
                Throwables.propagate(e);
            }
        }
        return bytes;
    }

    @Override
    public boolean preTransformClass(ClassNode classNode) {
        //Some mods like to wholesale replace classes; unfortunately, not all fields survive this transformation
        boolean hasRawBufferSize = false;
        String byteBuffer = ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT ? unObfByteBuffer : obfByteBuffer;
        for (FieldNode field : classNode.fields) {
            if (field.name.equals("rawBufferSize") && field.desc.equals("I")) {
                hasRawBufferSize = true;
            }
            if (field.name.equals(byteBuffer)) {
                byteBufferIsStatic = (field.access & Opcodes.ACC_STATIC) != 0;
            }
        }
        if (!hasRawBufferSize) {
            classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "rawBufferSize", "I", null, null));
        }
        //Don't mind this.  Just cramming a getter and setter into the Tessellator for later use
        //getter
        MethodNode getter = new MethodNode(Opcodes.ACC_PUBLIC, "getRawBufferSize", "()I", null, null);
        getter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        getter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, "rawBufferSize", "I"));
        getter.instructions.add(new InsnNode(Opcodes.IRETURN));
        classNode.methods.add(getter);
        //setter
        MethodNode setter = new MethodNode(Opcodes.ACC_PUBLIC, "setRawBufferSize", "(I)V", null, null);
        setter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        setter.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        setter.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, classNode.name, "rawBufferSize", "I"));
        setter.instructions.add(new InsnNode(Opcodes.RETURN));
        classNode.methods.add(setter);
        //Add new static fields
        FieldNode clProgram = new FieldNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "clProgram", "I", null, null);
        classNode.fields.add(clProgram);
        FieldNode clTexCoordAttribute = new FieldNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "clTexCoordAttribute", "I", null, null);
        classNode.fields.add(clTexCoordAttribute);

        return true;
    }

    @Override
    protected Class<?> getHelperClass() {
        return coloredlightscore.src.helper.CLTessellatorHelper.class;
    }

    @Override
    protected boolean transforms(ClassNode classNode, MethodNode methodNode) {
        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
                return true;
        }

        if (NameMapper.getInstance().isMethod(methodNode, super.className, "draw ()I"))
            return true;

        if ((methodNode.name + " " + methodNode.desc).equals(constructorToReplace))
            return true;

        return false;
    }

    @Override
    protected boolean transform(ClassNode classNode, MethodNode methodNode) {

        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name)) {
                return redefineMethod(classNode, methodNode, name);
            }
        }

        if (NameMapper.getInstance().isMethod(methodNode, super.className, "draw ()I")) {
        	return transformDraw(methodNode);
        }

        if ((methodNode.name + " " + methodNode.desc).equals(constructorToReplace)) {
            return transformConstructor(methodNode);
        }
        return false;
    }

    private boolean transformConstructor(MethodNode methodNode) {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "coloredlightscore/src/helper/CLTessellatorHelper", "setupShaders", "()V"));
        AbstractInsnNode returnNode = ASMUtils.findLastReturn(methodNode);
        methodNode.instructions.insertBefore(returnNode, list);
        return true;
    }

    /*
     * This does stuff...
     */
    protected boolean transformDraw(MethodNode methodNode) {
        boolean transformedEnableTexture = false;
        boolean transformedDisableTexture = false;
        boolean transformedEnableLightmap = false;
        boolean transformedDisableLightmap = false;
        for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insn = it.next();
            if (insn.getOpcode() == Opcodes.GETFIELD && (((FieldInsnNode)insn).name.equals(unObfTexture) || ((FieldInsnNode)insn).name.equals(obfTexture))) {
                if (!transformedEnableTexture) {
                    while (insn.getOpcode() != Opcodes.INVOKEVIRTUAL || !((MethodInsnNode) insn).name.equals("position")) {
                        insn = it.next();
                    }
                    insn = it.next(); // POP
                    it.set(new TypeInsnNode(Opcodes.CHECKCAST, "java/nio/FloatBuffer"));
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "coloredlightscore/src/helper/CLTessellatorHelper", "setTextureCoord", "(Ljava/nio/FloatBuffer;)V"));
                    transformedEnableTexture = true;
                } else {
                    insn = it.next(); // IFEQ L36 (or similar)
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "coloredlightscore/src/helper/CLTessellatorHelper", "unsetTextureCoord", "()V"));
                    transformedDisableTexture = true;
                }
            } else if (insn.getOpcode() == Opcodes.GETFIELD && (((FieldInsnNode)insn).name.equals(unObfBrightness) ||  ((FieldInsnNode)insn).name.equals(obfBrightness))) {
                if (!transformedEnableLightmap) {
                    insn = it.next(); // IFEQ L17 (or similar)
                    String byteBuffer = ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT ? unObfByteBuffer : obfByteBuffer;
                    if (byteBufferIsStatic) {
                        it.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/Tessellator", byteBuffer, "Ljava/nio/ByteBuffer;"));
                    } else {
                        it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        it.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/Tessellator", byteBuffer, "Ljava/nio/ByteBuffer;"));
                    }
                    it.add(new IntInsnNode(Opcodes.BIPUSH, 28));
                    it.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/nio/ByteBuffer", "position", "(I)Ljava/nio/Buffer;"));
                    it.add(new InsnNode(Opcodes.POP));
                    if (byteBufferIsStatic) {
                        it.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/Tessellator", byteBuffer, "Ljava/nio/ByteBuffer;"));
                    } else {
                        it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        it.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/Tessellator", byteBuffer, "Ljava/nio/ByteBuffer;"));
                    }
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "coloredlightscore/src/helper/CLTessellatorHelper", "setLightCoord", "(Ljava/nio/ByteBuffer;)V"));
                    transformedEnableLightmap = true;
                } else {
                    insn = it.next(); // IFEQ L38 (or similar)
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "coloredlightscore/src/helper/CLTessellatorHelper", "unsetLightCoord", "()V"));
                    transformedDisableLightmap = true;
                }
            }
        }
        return transformedEnableTexture && transformedDisableTexture && transformedEnableLightmap && transformedDisableLightmap;
    }
}