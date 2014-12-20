package coloredlightscore.src.asm.transformer;

import coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;

import java.util.ListIterator;

public class TransformRenderBlocks extends HelperMethodTransformer {

    // These methods will be replaced by statics in CLRenderBlocksHelper
    String methodsToReplace[] = { "renderStandardBlockWithAmbientOcclusion (Lnet/minecraft/block/Block;IIIFFF)Z",
                                  "renderStandardBlockWithAmbientOcclusionPartial (Lnet/minecraft/block/Block;IIIFFF)Z",
                                  "renderStandardBlockWithColorMultiplier (Lnet/minecraft/block/Block;IIIFFF)Z",
                                  "getAoBrightness (IIII)I"};
    String renderBlockLiquid = "renderBlockLiquid (Lnet/minecraft/block/Block;III)Z";

    private String unobfGetMixedBrightnessForBlock = "getMixedBrightnessForBlock";
    private String obfGetMixedBrightnessForBlock = "func_149677_c";

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

        if (NameMapper.getInstance().isMethod(methodNode, super.className, renderBlockLiquid)) {
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

        if (NameMapper.getInstance().isMethod(methodNode, super.className, renderBlockLiquid)) {
            return transformRenderBlockLiquid(methodNode);
        }

        return false;
    }

    private boolean transformRenderBlockLiquid(MethodNode method) {
        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
        String getMixedBrightnessForBlock = ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT ? unobfGetMixedBrightnessForBlock : obfGetMixedBrightnessForBlock;
        AbstractInsnNode aloadNode = null;
        while (iterator.hasNext()) {
            AbstractInsnNode insn = iterator.next();
            if (insn.getOpcode() == Opcodes.ALOAD && ((VarInsnNode)insn).var == 1) {
                aloadNode = insn;
            }
            if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode)insn).name.equals(getMixedBrightnessForBlock)) {
                AbstractInsnNode staticInvoke = new MethodInsnNode(Opcodes.INVOKESTATIC, "coloredlightscore/src/helper/CLBlockHelper", "getMixedBrightnessForBlockWithColor", "(Lnet/minecraft/world/IBlockAccess;III)I");
                iterator.set(staticInvoke);
                method.instructions.remove(aloadNode); // Drop ALOAD 1 since the replacement method is static
            }
        }
        return true;
    }
}
