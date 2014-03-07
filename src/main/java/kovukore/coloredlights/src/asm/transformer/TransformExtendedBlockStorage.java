package kovukore.coloredlights.src.asm.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.xml.internal.ws.org.objectweb.asm.Type;

import cpw.mods.fml.common.FMLLog;
import kovukore.coloredlights.src.asm.transformer.core.MethodTransformer;

public class TransformExtendedBlockStorage extends MethodTransformer {

	private boolean addedFields = false;
	private FieldNode rColorArray;
	private FieldNode gColorArray;
	private FieldNode bColorArray;
	private FieldNode blockLSBArray;
			
	@Override
	protected boolean transforms(ClassNode clazz, MethodNode method) {
		
		return method.name.equals("setExtBlocklightValue") | method.name.equals("getExtBlocklightValue") | method.name.equals("<init>");
	}

	@Override
	protected boolean transform(ClassNode clazz, MethodNode method) {
		
		if (!addedFields)
			addRGBNibbleArrays(clazz);
		
		if (method.name.equals("setExtBlocklightValue"))
			transformSetExtBlocklightValue(clazz, method);

		if (method.name.equals("getExtBlocklightValue"))
			transformGetExtBlocklightValue(clazz, method);

		if (method.name.equals("<init>"))
			transformConstructor(clazz, method);
		
		return false;
	}

	@Override
	protected boolean transforms(String className) {
		return className.equals("net.minecraft.world.chunk.storage.ExtendedBlockStorage");
	}

	private void addRGBNibbleArrays(ClassNode clazz)
	{
		for (FieldNode f : clazz.fields)
			if (f.name.equals("blockLSBArray"))
				blockLSBArray = f;
		
		rColorArray = new FieldNode(Opcodes.ACC_PUBLIC, "rColorArray", "[Lnet/minecraft/world/chunk/NibbleArray;", null, null);
		gColorArray = new FieldNode(Opcodes.ACC_PUBLIC, "gColorArray", "[Lnet/minecraft/world/chunk/NibbleArray;", null, null);
		bColorArray = new FieldNode(Opcodes.ACC_PUBLIC, "bColorArray", "[Lnet/minecraft/world/chunk/NibbleArray;", null, null);
		
		clazz.fields.add(rColorArray);
		clazz.fields.add(gColorArray);
		clazz.fields.add(bColorArray);			
		
		addedFields = true;
	}
	
	private void transformConstructor(ClassNode clazz, MethodNode m)
	{
		String ebsInternalName = clazz.name;
		Type typeNibbleArray = Type.getType(net.minecraft.world.chunk.NibbleArray.class);
		
		// Initializes array the same length as blockLSBArray:
//		23  aload_0 [this]
//	    24  getfield net.minecraft.world.chunk.storage.ExtendedBlockStorage.blockLSBArray : byte[] [3]
//	    27  arraylength
//	    28  iconst_4
//	    29  invokespecial net.minecraft.world.chunk.NibbleArray(int, int) [5]
//	    32  putfield net.minecraft.world.chunk.storage.ExtendedBlockStorage.blockMetadataArray : net.minecraft.world.chunk.NibbleArray [6]
		
		// Remove the return, to be re-inserted later
		m.instructions.remove(m.instructions.get(m.instructions.size() - 1));
		
		// Initialize rColorArray
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, ebsInternalName, blockLSBArray.name, blockLSBArray.desc));
		m.instructions.add(new InsnNode(Opcodes.ARRAYLENGTH));
		m.instructions.add(new InsnNode(Opcodes.ICONST_4));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, typeNibbleArray.getInternalName(), "<init>", "(II)"));
		m.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, ebsInternalName, rColorArray.name, rColorArray.desc));

		// Initialize gColorArray
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, ebsInternalName, blockLSBArray.name, blockLSBArray.desc));
		m.instructions.add(new InsnNode(Opcodes.ARRAYLENGTH));
		m.instructions.add(new InsnNode(Opcodes.ICONST_4));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, typeNibbleArray.getInternalName(), "<init>", "(II)"));
		m.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, ebsInternalName, gColorArray.name, gColorArray.desc));
	
		// Initialize bColorArray
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, ebsInternalName, blockLSBArray.name, blockLSBArray.desc));
		m.instructions.add(new InsnNode(Opcodes.ARRAYLENGTH));
		m.instructions.add(new InsnNode(Opcodes.ICONST_4));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, typeNibbleArray.getInternalName(), "<init>", "(II)"));
		m.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, ebsInternalName, bColorArray.name, bColorArray.desc));
		
		m.instructions.add(new InsnNode(Opcodes.RETURN));
	}

	private void transformSetExtBlocklightValue(ClassNode clazz, MethodNode m)
	{
		String ebsInternalName = clazz.name;
		Type typeNibbleArray = Type.getType(net.minecraft.world.chunk.NibbleArray.class);
		
		// Already in stock method:
//		 0  aload_0 [this]
//		 1  getfield net.minecraft.world.chunk.storage.ExtendedBlockStorage.blocklightArray : net.minecraft.world.chunk.NibbleArray [91]
//		 4  iload_1 [x]
//		 5  iload_2 [y]
//		 6  iload_3 [z]
//		 7  iload 4 [lightValue]
//		 9  invokevirtual net.minecraft.world.chunk.NibbleArray.set(int, int, int, int) : void [93]
		// return is there by default - remove for now
		m.instructions.remove(m.instructions.get(m.instructions.size() - 1));
		
		// Colored light mod: Store red value		
//		12  aload_0 [this]
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
//		13  getfield net.minecraft.world.chunk.storage.ExtendedBlockStorage.rColorArray : net.minecraft.world.chunk.NibbleArray [98]
		m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, ebsInternalName, rColorArray.name, rColorArray.desc));
//		16  iload_1 [x]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
//		17  iload_2 [y]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
//		18  iload_3 [z]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
//		19  iload 4 [lightValue]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
//		21  iconst_5
		m.instructions.add(new InsnNode(Opcodes.ICONST_5));
//		22  ishr
		m.instructions.add(new InsnNode(Opcodes.ISHR));
//		23  bipush 15
		m.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 15));
//		25  iand
		m.instructions.add(new InsnNode(Opcodes.IAND));
//		26  invokevirtual net.minecraft.world.chunk.NibbleArray.set(int, int, int, int) : void [93]
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNibbleArray.getInternalName(), "set", "(IIII)V"));
		
		// Colored light mod: Store green value
//		29  aload_0 [this]
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
//		30  getfield net.minecraft.world.chunk.storage.ExtendedBlockStorage.gColorArray : net.minecraft.world.chunk.NibbleArray [100]
		m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, ebsInternalName, gColorArray.name, gColorArray.desc));
//		33  iload_1 [x]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
//		34  iload_2 [y]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
//		35  iload_3 [z]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
//		36  iload 4 [lightValue]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
//		38  bipush 10
		m.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 10));
//		40  ishr
		m.instructions.add(new InsnNode(Opcodes.ISHR));
//		41  bipush 15
		m.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 15));
//		43  iand
		m.instructions.add(new InsnNode(Opcodes.IAND));
//		44  invokevirtual net.minecraft.world.chunk.NibbleArray.set(int, int, int, int) : void [93]
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNibbleArray.getInternalName(), "set", "(IIII)V"));
		
		// Colored light mod: Store blue value
//		47  aload_0 [this]
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
//		48  getfield net.minecraft.world.chunk.storage.ExtendedBlockStorage.bColorArray : net.minecraft.world.chunk.NibbleArray [102]
		m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, ebsInternalName, bColorArray.name, bColorArray.desc));
//		51  iload_1 [x]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
//		52  iload_2 [y]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
//		53  iload_3 [z]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
//		54  iload 4 [lightValue]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
//		56  bipush 15
		m.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 15));
//		58  ishr
		m.instructions.add(new InsnNode(Opcodes.ISHR));
//		59  bipush 15
		m.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 15));
//		61  iand
		m.instructions.add(new InsnNode(Opcodes.IAND));
//		62  invokevirtual net.minecraft.world.chunk.NibbleArray.set(int, int, int, int) : void [93]
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNibbleArray.getInternalName(), "set", "(IIII)V"));
//		65  return
		m.instructions.add(new InsnNode(Opcodes.RETURN));
		
	}

	private void transformGetExtBlocklightValue(ClassNode clazz, MethodNode m)
	{
		String ebsInternalName = clazz.name;
		Type typeNibbleArray = Type.getType(net.minecraft.world.chunk.NibbleArray.class);
		
		// Already in stock method:
//       0  aload_0 [this]
//       1  getfield net.minecraft.world.chunk.storage.ExtendedBlockStorage.blocklightArray : net.minecraft.world.chunk.NibbleArray [91]
//       4  iload_1 [par1]
//       5  iload_2 [par2]
//       6  iload_3 [par3]
//       7  invokevirtual net.minecraft.world.chunk.NibbleArray.get(int, int, int) : int [110]
		// ireturn is there by default - remove for now
		m.instructions.remove(m.instructions.get(m.instructions.size() - 1));		
		
//      10  aload_0 [this]
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
//      11  getfield net.minecraft.world.chunk.storage.ExtendedBlockStorage.rColorArray : net.minecraft.world.chunk.NibbleArray [98]
		m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, ebsInternalName, rColorArray.name, rColorArray.desc));
//      14  iload_1 [par1]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
//      15  iload_2 [par2]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
//      16  iload_3 [par3]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
//      17  invokevirtual net.minecraft.world.chunk.NibbleArray.get(int, int, int) : int [110]
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNibbleArray.getInternalName(), "get", "(IIII)V"));
//      20  iconst_5
		m.instructions.add(new InsnNode(Opcodes.ICONST_5));
//      21  ishl
		m.instructions.add(new InsnNode(Opcodes.ISHL));
//      22  ior
		m.instructions.add(new InsnNode(Opcodes.IOR));

//      23  aload_0 [this]
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
//      24  getfield net.minecraft.world.chunk.storage.ExtendedBlockStorage.gColorArray : net.minecraft.world.chunk.NibbleArray [100]
		m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, ebsInternalName, gColorArray.name, gColorArray.desc));
//      27  iload_1 [par1]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
//      28  iload_2 [par2]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
//      29  iload_3 [par3]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
//      30  invokevirtual net.minecraft.world.chunk.NibbleArray.get(int, int, int) : int [110]
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNibbleArray.getInternalName(), "get", "(IIII)V"));
//      33  bipush 10
		m.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 10));
//      35  ishl
		m.instructions.add(new InsnNode(Opcodes.ISHL));
//      36  ior
		m.instructions.add(new InsnNode(Opcodes.IOR));
		
//      37  aload_0 [this]
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
//      38  getfield net.minecraft.world.chunk.storage.ExtendedBlockStorage.bColorArray : net.minecraft.world.chunk.NibbleArray [102]
		m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, ebsInternalName, bColorArray.name, bColorArray.desc));
//      41  iload_1 [par1]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
//      42  iload_2 [par2]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
//      43  iload_3 [par3]
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
//      44  invokevirtual net.minecraft.world.chunk.NibbleArray.get(int, int, int) : int [110]
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, typeNibbleArray.getInternalName(), "get", "(IIII)V"));
//      47  bipush 15
		m.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 15));
//      49  ishl
		m.instructions.add(new InsnNode(Opcodes.ISHL));
//      50  ior
		m.instructions.add(new InsnNode(Opcodes.IOR));
//      51  ireturn
		m.instructions.add(new InsnNode(Opcodes.IRETURN));		
	}
}
