package coloredlightscore.src.asm.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.base.Throwables;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
import coloredlightscore.src.asm.transformer.core.ExtendedClassWriter;
import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import cpw.mods.fml.common.FMLLog;

public class TransformTessellator extends HelperMethodTransformer {

	// These methods will be replaced by statics in CLRenderBlocksHelper
	String appliedInterface = "coloredlightscore.src.interfaces.CLTessellatorInterface";
	String fieldName = "rawBufferSize";
	String fieldDescriptor = "I";
	
	String methodsToReplace[] = {
			"addVertex (DDD)V"
	};
	
	public TransformTessellator()
	{
		super("net/minecraft/client/renderer/Tessellator");
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (bytes != null && transforms(transformedName))
		{
			FMLLog.info("Class %s is a candidate for transforming", transformedName);
			
			try
			{
				ClassNode clazz = ASMUtils.getClassNode(bytes);
				
				clazz.interfaces.add(appliedInterface);
				
				MethodNode getter = new MethodNode(Opcodes.ACC_PUBLIC, fieldName, "()" + fieldDescriptor, null, null);
				getter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				getter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, clazz.name, fieldName, fieldDescriptor));
				getter.instructions.add(new InsnNode(Opcodes.IRETURN));
				clazz.methods.add(getter);
				
				MethodNode setter = new MethodNode(Opcodes.ACC_PUBLIC, fieldName, "(" + fieldDescriptor + ")V", null, null);
				setter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				setter.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
				setter.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, clazz.name, fieldName, fieldDescriptor));
				setter.instructions.add(new InsnNode(Opcodes.RETURN));
				clazz.methods.add(setter);
				
				
				if (transform(clazz, transformedName))
				{
					FMLLog.info("Transforming class " + transformedName);
					ClassWriter writer = new ExtendedClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
					clazz.accept(writer);
					bytes = writer.toByteArray();
				}
				else
					FMLLog.warning("Did not transform %s", transformedName);
			}
			catch (Exception e)
			{
				FMLLog.severe("Exception during transformation of class " + transformedName);
				e.printStackTrace();
				Throwables.propagate(e);
			}
		}
		return bytes;
	}
	
	@Override
	protected Class<?> getHelperClass() {
		return coloredlightscore.src.helper.CLTessellatorHelper.class;
	}

	
	@Override
	protected boolean transforms(ClassNode classNode, MethodNode methodNode) {
		
		for(String name : methodsToReplace)
		{
			if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
				return true;
		}
		
		return false;
	}	

	@Override
	protected boolean transform(ClassNode classNode, MethodNode methodNode) {
		
		for(String name : methodsToReplace)
		{
			if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
			{
				return redefineMethod(classNode, methodNode, name);
			}
		}		
		
		return false;
	}
}
