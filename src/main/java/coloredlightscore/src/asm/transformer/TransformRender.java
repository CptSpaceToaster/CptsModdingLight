package coloredlightscore.src.asm.transformer;

import coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin;
import coloredlightscore.src.asm.transformer.core.MethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

/**
 * Created by Murray on 12/13/2014.
 */
public class TransformRender extends MethodTransformer {
    final static String targetClass = "net.minecraft.client.renderer.entity.Render";
    private static final String unobfGetMinecraft = "getMinecraft";
    private static final String obfGetMinecraft = "func_71410_x";
    private static final String unobfEntityRenderer = "entityRenderer";
    private static final String obfEntityRenderer = "field_71460_t";
    private static final String unobfEnableLightmap = "enableLightmap";
    private static final String obfEnableLightmap = "func_78463_b";

    @Override
    protected boolean transforms(ClassNode clazz, MethodNode method) {
        return NameMapper.getInstance().isMethod(method, clazz.name, "renderEntityOnFire (Lnet/minecraft/entity/Entity;DDDF)V");
    }

    @Override
    protected boolean transform(ClassNode clazz, MethodNode method) {
        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
        String getMinecraft = ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT ? unobfGetMinecraft : obfGetMinecraft;
        String entityRenderer = ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT ? unobfEntityRenderer : obfEntityRenderer;
        String enableLightmap = ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT ? unobfEnableLightmap : obfEnableLightmap;
        AbstractInsnNode insn;
        boolean foundLabel = false, foundGLEnable = false;
        while (iterator.hasNext() && !foundLabel) {
            insn = iterator.next();
            if (insn.getType() == AbstractInsnNode.LABEL) {
                foundLabel = true;
            }
        }
        if (foundLabel) {
            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/Minecraft", getMinecraft, "()Lnet/minecraft/client/Minecraft;"));
            iterator.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", entityRenderer, "Lnet/minecraft/client/renderer/EntityRenderer;"));
            iterator.add(new InsnNode(Opcodes.DCONST_0));
            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "coloredlightscore/src/helper/CLTessellatorHelper", "isProgramInUse", "()Z"));
            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "coloredlightscore/src/helper/CLEntityRendererHelper", "disableLightmap", "(Lnet/minecraft/client/renderer/EntityRenderer;DZ)V"));
            while (iterator.hasNext() && !foundGLEnable) {
                insn = iterator.next();
                if (insn.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode)insn).name.equals("glEnable")) {
                    foundGLEnable = true;
                }
            }
            if (foundGLEnable) {
                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/Minecraft", getMinecraft, "()Lnet/minecraft/client/Minecraft;"));
                iterator.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", entityRenderer, "Lnet/minecraft/client/renderer/EntityRenderer;"));
                iterator.add(new InsnNode(Opcodes.DCONST_0));
                iterator.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/EntityRenderer", enableLightmap, "(D)V"));
            }
        }
        return foundLabel && foundGLEnable;
    }

    @Override
    protected boolean transforms(String className) {
        return targetClass.equals(className);
    }
}
