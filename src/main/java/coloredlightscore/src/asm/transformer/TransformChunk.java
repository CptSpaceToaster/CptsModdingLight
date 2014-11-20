package coloredlightscore.src.asm.transformer;

import coloredlightscore.src.asm.transformer.core.MethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

/**
 * Created by Murray on 11/19/2014.
 */
public class TransformChunk extends MethodTransformer {
    @Override
    protected boolean transforms(ClassNode clazz, MethodNode method) {
        return method.name.equals("getBlockLightValue") && method.desc.equals("(IIII)I");
    }

    @Override
    protected boolean transform(ClassNode clazz, MethodNode method) {
        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
        if (iterator.hasNext()) {
            AbstractInsnNode insn = iterator.next();
            while (iterator.hasNext() && (insn.getOpcode() != Opcodes.ILOAD || ((VarInsnNode)insn).var != 7)) {
                insn = iterator.next();
            }
            // if (j1 > i1) => if ((j1 & 0xf) > (i1 & 0xf))
            iterator.add(new IntInsnNode(Opcodes.BIPUSH, 15));
            iterator.add(new InsnNode(Opcodes.IAND));
            insn = iterator.next(); // ILOAD 6
            iterator.add(new IntInsnNode(Opcodes.BIPUSH, 15));
            iterator.add(new InsnNode(Opcodes.IAND));
        }
        return true;
    }

    @Override
    protected boolean transforms(String className) {
        return "net.minecraft.world.chunk.Chunk".equals(className);
    }
}
