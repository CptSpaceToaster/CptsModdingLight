package kovukore.coloredlights.src.asm.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import cpw.mods.fml.common.FMLLog;
import kovukore.coloredlights.src.asm.transformer.core.ASMUtils;
import kovukore.coloredlights.src.asm.transformer.core.MethodTransformer;

public class TransformEntityPlayerMP extends MethodTransformer {

	public TransformEntityPlayerMP() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean transforms(ClassNode clazz, MethodNode method) {
		return method.name.equals("onUpdate");
	}

	@Override
	protected boolean transform(ClassNode clazz, MethodNode method) {
		
		if (method.name.equals("onUpdate"))
		{
			// invokespecial net.minecraft.network.play.server.S26PacketMapChunkBulk(java.util.List)
			AbstractInsnNode insertionPoint = ASMUtils.findLastInvoke(method, Opcodes.INVOKESPECIAL, "net/minecraft/network/play/server/S26PacketMapChunkBulk", "<init>");
			InsnList helperInvoke = new InsnList();
			
			// +1
			insertionPoint = insertionPoint.getNext();
			
			helperInvoke.add(new VarInsnNode(Opcodes.ALOAD, 1)); // array list
			helperInvoke.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
			
			// Invoke helper function
			helperInvoke.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "kovukore/coloredlights/server/PlayerManagerHelper", "entityPlayerMP_onUpdate", "(Ljava/util/ArrayList;Lnet/minecraft/entity/player/EntityPlayerMP;)V"));

			method.instructions.insert(insertionPoint, helperInvoke);
			
			FMLLog.info("Transformed onUpdate");
			
			return true;
		}
		return false;
	}

	@Override
	protected boolean transforms(String className) {
		return className.equals("net.minecraft.entity.player.EntityPlayerMP");
	}

}
