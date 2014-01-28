package kovukore.coloredlightscore.src.asm.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.material.Material;
import net.minecraft.launchwrapper.IClassTransformer;

public class BlockTransformer_Fields implements IClassTransformer, Opcodes
{
	public byte[] transformBlock(String name, byte[] basicClass, boolean obfuscated)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		boolean hasLField = false;
		for (FieldNode fn : classNode.fields)
		{
			if (fn.name.equals("l"))
			{
				hasLField = true;
				continue;
			}
		}
		if (!hasLField)
		{
			writer.visitField(ACC_PUBLIC + ACC_STATIC, "l", "[Ljava/lang/Float;", null, null);
			FMLLog.info("Added field l to Block");
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

		if (transformedName.equals("net.minecraft.block.Block"))
		{
			return transformBlock(name, basicClass, false);
		}
		else
		{
			return basicClass;
		}
	}
}