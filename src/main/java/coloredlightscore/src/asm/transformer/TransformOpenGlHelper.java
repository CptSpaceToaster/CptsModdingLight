package coloredlightscore.src.asm.transformer;

import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.MethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

/**
 * Created by Murray on 11/30/2014.
 */
public class TransformOpenGlHelper extends MethodTransformer {
    @Override
    protected boolean transforms(ClassNode clazz, MethodNode method) {
        return NameMapper.getInstance().isMethod(method, clazz.name, "setLightmapTextureCoords (IFF)V");
    }

    @Override
    protected boolean transform(ClassNode clazz, MethodNode method) {
        method.localVariables.clear();
        method.instructions.clear();
        InsnList list = method.instructions;

        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
        list.add(new VarInsnNode(Opcodes.FLOAD, 1));
        list.add(new VarInsnNode(Opcodes.FLOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "coloredlightscore/src/helper/CLOpenGlHelper", "setLightmapTextureCoords", "(IFF)V"));
        list.add(new InsnNode(Opcodes.RETURN));

        return true;
    }

    @Override
    protected boolean transforms(String className) {
        return "net.minecraft.client.renderer.OpenGlHelper".equals(className);
    }
}