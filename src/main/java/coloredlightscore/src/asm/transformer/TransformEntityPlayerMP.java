package coloredlightscore.src.asm.transformer;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
import coloredlightscore.src.asm.transformer.core.MethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;

public class TransformEntityPlayerMP extends MethodTransformer {

    private final String CLASSNAME = "net.minecraft.entity.player.EntityPlayerMP";

    public TransformEntityPlayerMP() {
    }

    @Override
    protected boolean transforms(ClassNode clazz, MethodNode method) {
        return NameMapper.getInstance().isMethod(method, CLASSNAME, "onUpdate ()V");
    }

    @Override
    protected boolean transform(ClassNode clazz, MethodNode method) {

        if (NameMapper.getInstance().isMethod(method, CLASSNAME, "onUpdate ()V")) {
            // invokespecial net.minecraft.network.play.server.S26PacketMapChunkBulk(java.util.List)
            //String S26PacketMapChunkBulk = NameMapper.getInstance().getClassName("net/minecraft/network/play/server/S26PacketMapChunkBulk");
            AbstractInsnNode insertionPoint = ASMUtils.findLastInvoke(method, Opcodes.INVOKESPECIAL, "net/minecraft/network/play/server/S26PacketMapChunkBulk", "<init> (Ljava/util/List;)V", false);
            InsnList helperInvoke = new InsnList();
            String helperDescriptor = NameMapper.getInstance().getJVMTypeObfuscated("(Ljava/util/ArrayList;Lnet/minecraft/entity/player/EntityPlayerMP;)V");

            // +1 (done later to prevent runtime crash)
            //insertionPoint = insertionPoint.getNext();

            helperInvoke.add(new VarInsnNode(Opcodes.ALOAD, 1)); // array list
            helperInvoke.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this

            // Invoke helper function
            helperInvoke.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "coloredlightscore/server/PlayerManagerHelper", "entityPlayerMP_onUpdate", helperDescriptor));

            if (insertionPoint != null) {
                method.instructions.insert(insertionPoint.getNext(), helperInvoke);
                return true;
            } else {
                CLLog.error("TransformEntityPlayerMP.transform()  Could not find INVOKESPECIAL to S26PacketMapChunkBulk constructor!");
                ASMUtils.findLastInvoke(method, Opcodes.INVOKESPECIAL, "net/minecraft/network/play/server/S26PacketMapChunkBulk", "<init> (Ljava/util/List;)V", true);
            }

            return false;
        }
        return false;
    }

    @Override
    protected boolean transforms(String className) {
        //return className.equals(NameMapper.getInstance().getClassName(CLASSNAME).replace('/', '.'));
        return className.equals(CLASSNAME);
    }

}
