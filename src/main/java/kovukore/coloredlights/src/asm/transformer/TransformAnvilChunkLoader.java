package kovukore.coloredlights.src.asm.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

import kovukore.coloredlights.src.asm.transformer.core.ASMUtils;
import kovukore.coloredlights.src.asm.transformer.core.MethodTransformer;

public class TransformAnvilChunkLoader extends MethodTransformer {

	/**
	 * Sets hooks to store RGB color data with world save
	 */
	public TransformAnvilChunkLoader() {

	}

	@Override
	protected boolean transforms(ClassNode clazz, MethodNode method) {
		return method.name.equals("writeChunkToNBT") | method.name.equals("readChunkFromNBT");
	}

	@Override
	protected boolean transform(ClassNode clazz, MethodNode method) {
		
		if (method.name.equals("writeChunkToNBT"))
			return transformWriteChunkToNBT(clazz, method);
		else if (method.name.equals("readChunkFromNBT"))
			return transformReadChunkFromNBT(clazz, method);
		
		return false;
	}

	@Override
	protected boolean transforms(String className) {
		return className.equals("net.minecraft.world.chunk.storage.AnvilChunkLoader");
	}

	protected boolean transformWriteChunkToNBT(ClassNode clazz, MethodNode method)
	{
		// Source line 269: nbttagcompound1.setByteArray("BlockLight", extendedblockstorage.getBlocklightArray().data);
		// Find that and append the equivilant of the following:
		// nbttagcompound1.setByteArray("RedColorArray", extendedblockstorage.getRedColorArray().data);
		// nbttagcompound1.setByteArray("GreenColorArray", extendedblockstorage.getGreenColorArray().data);
		// nbttagcompound1.setByteArray("BlueColorArray", extendedblockstorage.getBlueColorArray().data);
		//
		// The raw ASM source for any of the above lines is as follows:
	    //     aload 9 [nbttagcompound1]        ; 9 = index of nbttagcompound1
		//     ldc <String "BlockLight"> [110]  ; This is the name of the byte array (parameter 1 into setByteArray)
		//     aload 11 [extendedblockstorage]  ; 11 = index of extendedblockstorage
		//     invokevirtual net.minecraft.world.chunk.storage.ExtendedBlockStorage.getBlocklightArray() : net.minecraft.world.chunk.NibbleArray [111]
		//     getfield net.minecraft.world.chunk.NibbleArray.data : byte[] [107]
		//     invokevirtual net.minecraft.nbt.NBTTagCompound.setByteArray(java.lang.String, byte[]) : void [104]
		
		int localVarNBTTagCompound1;
		int localVarExtendedBlockStorage;
		
		Type typeEBS = Type.getType(net.minecraft.world.chunk.storage.ExtendedBlockStorage.class);
		Type typeNBT = Type.getType(net.minecraft.nbt.NBTTagCompound.class);
		Type typeNibbleArray = Type.getType(net.minecraft.world.chunk.NibbleArray.class);
		
		LdcInsnNode ldcBlockLight = ASMUtils.findLastLDC(method, "BlockLight");
		VarInsnNode aloadNBT = (VarInsnNode)ldcBlockLight.getPrevious();
		VarInsnNode aloadEBS = (VarInsnNode)ldcBlockLight.getNext();
		InsnList newOpcodes = new InsnList();
		
		String getterDescriptor = String.format("()%s", typeNibbleArray.getDescriptor());
		
		// Set up insertion point after last "invokevirtual"
		// It is probably best not to assume a fixed instruction length, should revisit this later.
		AbstractInsnNode insertionPoint = ldcBlockLight.getNext().getNext().getNext().getNext().getNext();
		
		localVarNBTTagCompound1 = aloadNBT.var; // Save off index of nbttagcompound1. Likely 9, but it's best not to assume such things.
		localVarExtendedBlockStorage = aloadEBS.var; // Save for extendedblockstorage
		
		// Generate our calls to store RGB color data
		
		// nbttagcompound1.setByteArray("RedColorArray", extendedblockstorage.getRedColorArray().data);
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarNBTTagCompound1));
		newOpcodes.add(new LdcInsnNode("RedColorArray"));
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarExtendedBlockStorage));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeEBS.getInternalName(), "getRedColorArray", getterDescriptor));
		newOpcodes.add(new FieldInsnNode(Opcodes.GETFIELD, typeNibbleArray.getInternalName(), "data", "[B"));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNBT.getInternalName(), "setByteArray", "(Ljava/lang/String;[B)V"));

		// nbttagcompound1.setByteArray("GreenColorArray", extendedblockstorage.getGreenColorArray().data);
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarNBTTagCompound1));
		newOpcodes.add(new LdcInsnNode("GreenColorArray"));
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarExtendedBlockStorage));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeEBS.getInternalName(), "getGreenColorArray", getterDescriptor));
		newOpcodes.add(new FieldInsnNode(Opcodes.GETFIELD, typeNibbleArray.getInternalName(), "data", "[B"));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNBT.getInternalName(), "setByteArray", "(Ljava/lang/String;[B)V"));

		// nbttagcompound1.setByteArray("BlueColorArray", extendedblockstorage.getGreenColorArray().data);
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarNBTTagCompound1));
		newOpcodes.add(new LdcInsnNode("BlueColorArray"));
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarExtendedBlockStorage));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeEBS.getInternalName(), "getBlueColorArray", getterDescriptor));
		newOpcodes.add(new FieldInsnNode(Opcodes.GETFIELD, typeNibbleArray.getInternalName(), "data", "[B"));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNBT.getInternalName(), "setByteArray", "(Ljava/lang/String;[B)V"));
		
		method.instructions.insert(insertionPoint, newOpcodes);
		
		//FMLLog.info("transformWriteChunkToNBT complete");
		
		return true;
	}
	
	protected boolean transformReadChunkFromNBT(ClassNode clazz, MethodNode method)
	{
		// Source line 393: extendedblockstorage.setBlocklightArray(new NibbleArray(nbttagcompound1.getByteArray("BlockLight"), 4));
		// Find that and append the equivilant of the following:
		// if (nbttagcompound1.hasKey("RedColorArray"))
		// {
		// extendedblockstorage.setRedColorArray(new NibbleArray(nbttagcompound1.getByteArray("RedColorArray"), 4));
		// extendedblockstorage.setGreenColorArray(new NibbleArray(nbttagcompound1.getByteArray("GreenColorArray"), 4));
		// extendedblockstorage.setBlueColorArray(new NibbleArray(nbttagcompound1.getByteArray("BlueColorArray"), 4));
		// }
		//
		// The raw ASM for if (nbttagcompound1.hasKey("RedColorArray")) is:
		//   aload 13 ; index of EBS variable
		//   ldc <String "RedColorArray">
		//   invokevirtual net.minecraft.nbt.NBTTagCompound.hasKey
		//   ifeq SKIP_COLOR_LABEL    ; put it after the last bit
		//
		// The raw ASM for extendedblockstorage.setXXXColorArray is:
		//   aload 13 [extendedblockstorage]   ; 13=index of EBS variable
		//   new net.minecraft.world.chunk.NibbleArray [167]
		//   dup
		//   aload 11 [nbttagcompound1] ; 11=index of nbttagcompound1
		//   ldc <String "BlockLight"> [110]   // argument to getByteArray
		//   invokevirtual net.minecraft.nbt.NBTTagCompound.getByteArray(java.lang.String) : byte[] [165]  // argument 1 to NibbleArray
		//   iconst_4 // argument 2 to NibbleArray
		//   invokespecial net.minecraft.world.chunk.NibbleArray(byte[], int) [168]
		//   invokevirtual net.minecraft.world.chunk.storage.ExtendedBlockStorage.setBlocklightArray(net.minecraft.world.chunk.NibbleArray) : void [171]
				
		int localVarNBTTagCompound1;
		int localVarExtendedBlockStorage;
		
		Type typeEBS = Type.getType(net.minecraft.world.chunk.storage.ExtendedBlockStorage.class);
		Type typeNBT = Type.getType(net.minecraft.nbt.NBTTagCompound.class);
		Type typeNibbleArray = Type.getType(net.minecraft.world.chunk.NibbleArray.class);
		
		LabelNode labelSkipColorStuff = new LabelNode();
		LdcInsnNode ldcBlockLight = ASMUtils.findLastLDC(method, "BlockLight");
		VarInsnNode aloadNBT = (VarInsnNode)ldcBlockLight.getPrevious();
		VarInsnNode aloadEBS = (VarInsnNode)ldcBlockLight.getPrevious().getPrevious().getPrevious().getPrevious();
		InsnList newOpcodes = new InsnList();		

		String setterDescriptor = String.format("(%s)V", typeNibbleArray.getDescriptor());
		
		localVarNBTTagCompound1 = aloadNBT.var; // Save off index of nbttagcompound1. Likely 9, but it's best not to assume such things.
		localVarExtendedBlockStorage = aloadEBS.var; // Save for extendedblockstorage
		
		AbstractInsnNode insertionPoint = ldcBlockLight.getNext().getNext().getNext().getNext().getNext();		
		
		// if (nbttagcompound1.hasKey("RedColorArray"))
		//newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarNBTTagCompound1));
		//newOpcodes.add(new LdcInsnNode("RedColorArray"));
		//newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNBT.getInternalName(), "hasKey", "(Ljava/lang/String;)Z"));
		//newOpcodes.add(new JumpInsnNode(Opcodes.IFEQ, labelSkipColorStuff)); // if result is 0, go to labelSkipColorStuff
		// {
		
		//   aload 13 [extendedblockstorage]   ; 13=index of EBS variable
		//   new net.minecraft.world.chunk.NibbleArray [167]
		//   dup
		//   aload 11 [nbttagcompound1] ; 11=index of nbttagcompound1
		//   ldc <String "BlockLight"> [110]   // argument to getByteArray
		//   invokevirtual net.minecraft.nbt.NBTTagCompound.getByteArray(java.lang.String) : byte[] [165]  // argument 1 to NibbleArray
		//   iconst_4 // argument 2 to NibbleArray
		//   invokespecial net.minecraft.world.chunk.NibbleArray(byte[], int) [168]
		//   invokevirtual net.minecraft.world.chunk.storage.ExtendedBlockStorage.setBlocklightArray(net.minecraft.world.chunk.NibbleArray) : void [171]		
		
		// extendedblockstorage.setRedColorArray(new NibbleArray(nbttagcompound1.getByteArray("RedColorArray"), 4));
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarExtendedBlockStorage));
		newOpcodes.add(new TypeInsnNode(Opcodes.NEW, typeNibbleArray.getInternalName()));
		newOpcodes.add(new InsnNode(Opcodes.DUP));
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarNBTTagCompound1));
		newOpcodes.add(new LdcInsnNode("RedColorArray"));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNBT.getInternalName(), "getByteArray", "(Ljava/lang/String;)[B"));
		newOpcodes.add(new InsnNode(Opcodes.ICONST_4));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, typeNibbleArray.getInternalName(), "<init>", "([BI)V"));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeEBS.getInternalName(), "setRedColorArray", setterDescriptor));

		// extendedblockstorage.setGreenColorArray(new NibbleArray(nbttagcompound1.getByteArray("GreenColorArray"), 4));
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarExtendedBlockStorage));
		newOpcodes.add(new TypeInsnNode(Opcodes.NEW, typeNibbleArray.getInternalName()));
		newOpcodes.add(new InsnNode(Opcodes.DUP));
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarNBTTagCompound1));
		newOpcodes.add(new LdcInsnNode("GreenColorArray"));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNBT.getInternalName(), "getByteArray", "(Ljava/lang/String;)[B"));
		newOpcodes.add(new InsnNode(Opcodes.ICONST_4));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, typeNibbleArray.getInternalName(), "<init>", "([BI)V"));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeEBS.getInternalName(), "setGreenColorArray", setterDescriptor));

		// extendedblockstorage.setBlueColorArray(new NibbleArray(nbttagcompound1.getByteArray("BlueColorArray"), 4));
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarExtendedBlockStorage));
		newOpcodes.add(new TypeInsnNode(Opcodes.NEW, typeNibbleArray.getInternalName()));
		newOpcodes.add(new InsnNode(Opcodes.DUP));
		newOpcodes.add(new VarInsnNode(Opcodes.ALOAD, localVarNBTTagCompound1));
		newOpcodes.add(new LdcInsnNode("BlueColorArray"));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNBT.getInternalName(), "getByteArray", "(Ljava/lang/String;)[B"));
		newOpcodes.add(new InsnNode(Opcodes.ICONST_4));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, typeNibbleArray.getInternalName(), "<init>", "([BI)V"));
		newOpcodes.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeEBS.getInternalName(), "setBlueColorArray", setterDescriptor));
				
		// }
		newOpcodes.add(labelSkipColorStuff);
		
		method.instructions.insert(insertionPoint, newOpcodes);
		
		//FMLLog.info("transformWriteChunkToNBT complete");
		
		return true;
	}
}
