package kovukore.coloredlights.src.asm.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import cpw.mods.fml.common.FMLLog;
import kovukore.coloredlights.src.asm.transformer.core.ASMUtils;
import kovukore.coloredlights.src.asm.transformer.core.MethodTransformer;

public class TransformPlayerInstance extends MethodTransformer {

	public TransformPlayerInstance() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean transforms(ClassNode clazz, MethodNode method) {
		return method.name.equals("sendToAllPlayersWatchingChunk");
	}

	@Override
	protected boolean transform(ClassNode clazz, MethodNode method) {
		if (method.name.equals("sendToAllPlayersWatchingChunk"))
		{
			transformNewSendToAllPlayersWatchingChunk(method);
			FMLLog.info("################### Transformed PlayerInstance");
			return true;
		}
		else
			return false;
	}

	@Override
	protected boolean transforms(String className) {
		return className.equals("net.minecraft.server.management.PlayerManager$PlayerInstance");
	}
	
	protected void transformNewSendToAllPlayersWatchingChunk(MethodNode method)
	{
		// insert after     50  invokevirtual net.minecraft.network.NetHandlerPlayServer.sendPacket(net.minecraft.network.Packet) : void [38]
		MethodInsnNode insertionPoint = ASMUtils.findLastInvoke(method, Opcodes.INVOKEVIRTUAL, "net/minecraft/network/NetHandlerPlayServer", "sendPacket");
		InsnList helperInvoke = new InsnList();
		
		// Push EntityPlayerMP
		helperInvoke.add(new VarInsnNode(Opcodes.ALOAD, 3));
		
		// Push this.ChunkCoordIntPair
		helperInvoke.add(new VarInsnNode(Opcodes.ALOAD, 0));
		helperInvoke.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/management/PlayerManager$PlayerInstance", "chunkLocation", "Lnet/minecraft/world/ChunkCoordIntPair;"));

		// Invoke helper function
		helperInvoke.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "kovukore/coloredlights/server/PlayerManagerHelper", "sendToPlayerWatchingChunk", "(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/world/ChunkCoordIntPair;)V"));
		//public static void sendToPlayerWatchingChunk(EntityPlayerMP player, ChunkCoordIntPair chunkLocation)

		method.instructions.insert(insertionPoint, helperInvoke);
	}

}
