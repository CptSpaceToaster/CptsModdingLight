package coloredlightscore.src.asm.transformer;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
import coloredlightscore.src.asm.transformer.core.MethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;

public class TransformPlayerInstance extends MethodTransformer {

    private final String PLAYER_INSTANCE_CLASSNAME = "net.minecraft.server.management.PlayerManager$PlayerInstance";
    private final String SEND_TO_ALL_PLAYERS_WATCHING_CHUNK = "sendToAllPlayersWatchingChunk (Lnet/minecraft/network/Packet;)V";

    public TransformPlayerInstance() {
    }

    @Override
    protected boolean transforms(ClassNode clazz, MethodNode method) {
        return NameMapper.getInstance().isMethod(method, PLAYER_INSTANCE_CLASSNAME, SEND_TO_ALL_PLAYERS_WATCHING_CHUNK);
    }

    @Override
    protected boolean transform(ClassNode clazz, MethodNode method) {
        if (NameMapper.getInstance().isMethod(method, PLAYER_INSTANCE_CLASSNAME, SEND_TO_ALL_PLAYERS_WATCHING_CHUNK)) {
            transformNewSendToAllPlayersWatchingChunk(method);
            return true;
        } else
            return false;
    }

    @Override
    protected boolean transforms(String className) {
        //return className.equals(NameMapper.getInstance().getClassName(PLAYER_INSTANCE_CLASSNAME).replace('/', '.'));
        return className.equals(PLAYER_INSTANCE_CLASSNAME);
    }

    protected void transformNewSendToAllPlayersWatchingChunk(MethodNode method) {
        // insert after     50  invokevirtual net.minecraft.network.NetHandlerPlayServer.sendPacket(net.minecraft.network.Packet) : void [38]
        MethodInsnNode insertionPoint = ASMUtils.findLastInvoke(method, Opcodes.INVOKEVIRTUAL, "net/minecraft/network/NetHandlerPlayServer", "sendPacket (Lnet/minecraft/network/Packet;)V", false);
        InsnList helperInvoke = new InsnList();

        String playerInstance = NameMapper.getInstance().getClassName(PLAYER_INSTANCE_CLASSNAME);
        String chunkLocation = NameMapper.getInstance().getClassField(PLAYER_INSTANCE_CLASSNAME, "chunkLocation");
        String chunkLocationDescriptor = NameMapper.getInstance().getJVMTypeObfuscated("Lnet/minecraft/world/ChunkCoordIntPair;");
        String helperDescriptor = NameMapper.getInstance().getJVMTypeObfuscated("(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/world/ChunkCoordIntPair;)V");

        CLLog.debug("debug->field name is {}", chunkLocation);

        // Push EntityPlayerMP
        helperInvoke.add(new VarInsnNode(Opcodes.ALOAD, 3));

        // Push this.ChunkCoordIntPair
        helperInvoke.add(new VarInsnNode(Opcodes.ALOAD, 0));
        helperInvoke.add(new FieldInsnNode(Opcodes.GETFIELD, playerInstance, chunkLocation, chunkLocationDescriptor));

        // Invoke helper function
        helperInvoke.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "coloredlightscore/server/PlayerManagerHelper", "sendToPlayerWatchingChunk", helperDescriptor));

        if (insertionPoint != null)
            method.instructions.insert(insertionPoint, helperInvoke);
        else {
            CLLog.error("TransformPlayerInstance.transformNewSendToAllPlayersWatchingChunk()  Could not find last invoke of sendPacket!");
            ASMUtils.findLastInvoke(method, Opcodes.INVOKEVIRTUAL, "net/minecraft/network/NetHandlerPlayServer", "sendPacket (Lnet/minecraft/network/Packet;)V", true);
        }
    }

}
