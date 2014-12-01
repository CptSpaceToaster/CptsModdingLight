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
            // int i1 = ... & 0xf
            while (iterator.hasNext() && (insn.getOpcode() != Opcodes.ISTORE || ((VarInsnNode)insn).var != 6)) {
                insn = iterator.next();
            }
            iterator.set(new IntInsnNode(Opcodes.BIPUSH, 0xf));
            iterator.add(new InsnNode(Opcodes.IAND));
            iterator.add(insn); // ISTORE 6

            // int j1 = ... & 0xf
            while (iterator.hasNext() && (insn.getOpcode() != Opcodes.ISTORE || ((VarInsnNode)insn).var != 7)) {
                insn = iterator.next();
            }
            iterator.set(new IntInsnNode(Opcodes.BIPUSH, 0xf));
            iterator.add(new InsnNode(Opcodes.IAND));
            iterator.add(insn); // ISTORE 7
        }
        return true;
    }

    @Override
    protected boolean transforms(String className) {
        return "net.minecraft.world.chunk.Chunk".equals(className);
    }
}
