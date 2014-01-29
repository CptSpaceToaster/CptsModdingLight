package kovukore.coloredlights.src.asm.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.world.chunk.NibbleArray;

public class TransformEBSFields implements IClassTransformer, Opcodes
{
	public byte[] transformBlock(String name, byte[] basicClass, boolean obfuscated)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		boolean hasRedField = false;
		boolean hasGreenField = false;
		boolean hasBlueField = false;
		
		for (FieldNode fn : classNode.fields)
		{
			if (fn.name.equals("rColorArray"))
			{
				hasRedField = true;
			}
			if (fn.name.equals("gColorArray"))
			{
				hasGreenField = true;
			}
			if (fn.name.equals("bColorArray"))
			{
				hasBlueField = true;
			}
		}
		if (!hasRedField)
		{
			writer.visitField(ACC_PUBLIC, "rColorArray", "[Lnet/minecraft/world/chunk/NibbleArray;", null, null);
			FMLLog.info("Added field rColorArray to Block");
		}
		if (!hasGreenField)
		{
			writer.visitField(ACC_PUBLIC, "gColorArray", "[Lnet/minecraft/world/chunk/NibbleArray;", null, null);
			FMLLog.info("Added field gColorArray to Block");
		}
		if (!hasBlueField)
		{
			writer.visitField(ACC_PUBLIC, "bColorArray", "[Lnet/minecraft/world/chunk/NibbleArray;", null, null);
			FMLLog.info("Added field bColorArray to Block");
		}
		else
		{
			FMLLog.info("Skipping the addition of field l to Block. Already exists!");
		}
		writer.visitEnd();
		return writer.toByteArray();
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{

		if (transformedName.equals("net.minecraft.world.chunk.storage.ExtendedBlockStorage"))
		{
			return transformBlock(name, basicClass, false);
		}
		else
		{
			return basicClass;
		}
	}
}