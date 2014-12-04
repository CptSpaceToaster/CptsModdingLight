package coloredlightscore.src.asm.transformer;

import coloredlightscore.src.asm.transformer.core.MethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

/**
 * Created by Murray on 11/19/2014.
 */
public class TransformGuiIngameForge extends MethodTransformer {
    final static String getSavedLightValue = "getSavedLightValue";
    final static String getSavedLightValueObf = "func_76614_a";
    final static String targetClass = "net.minecraftforge.client.GuiIngameForge";

    @Override
    protected boolean transforms(ClassNode clazz, MethodNode method) {
        return method.name.equals("renderHUDText") && method.desc.equals("(II)V");
    }

    @Override
    protected boolean transform(ClassNode clazz, MethodNode method) {
        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode insn = iterator.next();
            if (insn.getOpcode() == Opcodes.LDC && ((LdcInsnNode)insn).cst.equals("lc: %d b: %s bl: %d sl: %d rl: %d")) {
                /*
                    String.format is a variadic function, which means it can take as many parameters as you'd like. The
                    way this is implemented in bytecode is by passing in the format string, then an array of Objects of
                    size appropriate to the actual number of parameters.
                 */
                ((LdcInsnNode)insn).cst = "lc: %d b: %s bl: %d (0x%03x) sl: %d rl: %d";
                insn = iterator.next();
                iterator.set(new IntInsnNode(Opcodes.BIPUSH, 6)); // Change the 5 to a 6 for the new number of parameters
                boolean foundSavedLightValue = false;
                do {
                    insn = iterator.next();
                    if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode mNode = (MethodInsnNode)insn;
                        if (mNode.name.equals(getSavedLightValue) || mNode.name.equals(getSavedLightValueObf)) {
                            foundSavedLightValue = true;
                        }
                    }
                } while (iterator.hasNext() && !foundSavedLightValue);
                iterator.add(new InsnNode(Opcodes.DUP_X2)); // Save calculated brightness under AASTORE params so we can grab rgb later
                iterator.add(new IntInsnNode(Opcodes.BIPUSH, 0xf));
                iterator.add(new InsnNode(Opcodes.IAND)); // AND with 0xf to extract just the l component
                insn = iterator.next(); // INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
                insn = iterator.next(); // AASTORE
                insn = iterator.next(); // DUP

                // Put array reference and ICONST_3 (first two AASTORE params) under brightness
                iterator.set(new InsnNode(Opcodes.DUP2));
                iterator.add(new InsnNode(Opcodes.POP));
                iterator.add(new InsnNode(Opcodes.SWAP));
                insn = iterator.next(); // ICONST_3
                iterator.add(new InsnNode(Opcodes.SWAP)); // Stack now looks like $array/3/$brightness for call to AASTORE

                realignRGB(iterator); // Massage bits to look like RGB
                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;"));
                iterator.add(new InsnNode(Opcodes.AASTORE));
                iterator.add(new InsnNode(Opcodes.DUP));
                iterator.add(new InsnNode(Opcodes.ICONST_4));
                do { insn = iterator.next(); } while (iterator.hasNext() && insn.getOpcode() != Opcodes.ICONST_4);
                iterator.set(new InsnNode(Opcodes.ICONST_5)); // Need to bump up the last array index (4 to 5)
            }
        }
        return true;
    }

    private void realignRGB(ListIterator<AbstractInsnNode> iterator) {
        iterator.add(new InsnNode(Opcodes.ICONST_5));
        iterator.add(new InsnNode(Opcodes.ISHR));       //00BB BB0G GGG0 RRRR
        iterator.add(new InsnNode(Opcodes.DUP));        //Saved!
        iterator.add(new InsnNode(Opcodes.ICONST_1));
        iterator.add(new InsnNode(Opcodes.ISHR));       //000B BBB0 GGGG 0RRR
        iterator.add(new InsnNode(Opcodes.DUP));        //Saved!
        iterator.add(new InsnNode(Opcodes.ICONST_1));
        iterator.add(new InsnNode(Opcodes.ISHR));       //0000 BBBB 0GGG G0RR
        iterator.add(new LdcInsnNode(0xf00));
        iterator.add(new InsnNode(Opcodes.IAND));       //0000 BBBB 0000 0000
        iterator.add(new IntInsnNode(Opcodes.BIPUSH, 8));
        iterator.add(new InsnNode(Opcodes.ISHR));       //0000 0000 0000 BBBB
        iterator.add(new InsnNode(Opcodes.SWAP));       //000B BBB0 GGGG 0RRR
        iterator.add(new LdcInsnNode(0xf0));
        iterator.add(new InsnNode(Opcodes.IAND));       //0000 0000 GGGG 0000
        iterator.add(new InsnNode(Opcodes.IOR));        //0000 0000 GGGG BBBB
        iterator.add(new InsnNode(Opcodes.SWAP));       //00BB BB0G GGG0 RRRR
        iterator.add(new LdcInsnNode(0xf));
        iterator.add(new InsnNode(Opcodes.IAND));       //0000 0000 0000 RRRR
        iterator.add(new IntInsnNode(Opcodes.BIPUSH, 8));
        iterator.add(new InsnNode(Opcodes.ISHL));       //0000 RRRR 0000 0000
        iterator.add(new InsnNode(Opcodes.IOR));        //0000 RRRR GGGG BBBB
    }

    @Override
    protected boolean transforms(String className) {
        return targetClass.equals(className);
    }
}
