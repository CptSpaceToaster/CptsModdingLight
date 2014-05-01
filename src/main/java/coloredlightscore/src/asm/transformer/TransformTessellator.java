package coloredlightscore.src.asm.transformer;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.base.Throwables;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
import coloredlightscore.src.asm.transformer.core.ExtendedClassWriter;
import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import coloredlightscore.src.interfaces.CLTessellatorInterface;
import cpw.mods.fml.common.FMLLog;

public class TransformTessellator extends HelperMethodTransformer {
	String unObfBrightness = "hasBrightness";
	String obfBrightness = "field_78414_p"; //It could also be field_147580_e
	
	
	// These methods will be replaced by statics in CLTessellatorHelper
	String methodsToReplace[] = {
			"addVertex (DDD)V"
	};
	
	String drawSignature = "draw ()I";
	
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
				
				clazz.interfaces.add(CLTessellatorInterface.appliedInterface);
				
				//Don't mind this.  Just cramming a getter and setter into the Tesellator for later use
				//getter
				MethodNode getter = new MethodNode(Opcodes.ACC_PUBLIC, CLTessellatorInterface.getterName, "()" + CLTessellatorInterface.fieldDescriptor, null, null);
				getter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				getter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, clazz.name, CLTessellatorInterface.fieldName, CLTessellatorInterface.fieldDescriptor));
				getter.instructions.add(new InsnNode(Opcodes.IRETURN));
				clazz.methods.add(getter);
				//setter
				MethodNode setter = new MethodNode(Opcodes.ACC_PUBLIC, CLTessellatorInterface.setterName, "(" + CLTessellatorInterface.fieldDescriptor + ")V", null, null);
				setter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				setter.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
				setter.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, clazz.name, CLTessellatorInterface.fieldName, CLTessellatorInterface.fieldDescriptor));
				setter.instructions.add(new InsnNode(Opcodes.RETURN));
				clazz.methods.add(setter);
				
				
				if (transform(clazz, transformedName))
				{
					FMLLog.info("Finished Transforming class " + transformedName);
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
		
		if ((methodNode.name + " " + methodNode.desc).equals(drawSignature))
			return true;

		
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
		
		if ((methodNode.name + " " + methodNode.desc).equals(drawSignature))
			return transformDraw(methodNode);
		
		return false;
	}
	
	/*
	 * There isn't a good way around this that I can see, but we'll look for the reference to hasBrightness,  
	 * and then we'll blindly change the next instance of a 2... to a 3...
	 * 
	 * GL11.glTexCoordPointer( **3** , 32, this.shortBuffer); on line 140
	 */
	protected boolean transformDraw(MethodNode methodNode)
	{	
		boolean hasFoundBrightness = false;
		boolean replacedTwoWithThree = false;
		
	    for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext();) {
	        AbstractInsnNode insn = it.next();
	        if (insn.getOpcode() == Opcodes.GETFIELD && !hasFoundBrightness) {
		        try {
		        	if (((FieldInsnNode)insn).name.equals(obfBrightness) || ((FieldInsnNode)insn).name.equals(unObfBrightness)) {
		        		hasFoundBrightness = true;
		        	}
		        } catch (ClassCastException e) {
		        	FMLLog.severe("There was an issue casting the Instruction to a FieldInsnNode for some reason?");
		        	e.printStackTrace();
		        }
	        }
	        
	        if (hasFoundBrightness && insn.getOpcode() == Opcodes.ICONST_2) {
	        	it.set(new InsnNode(Opcodes.ICONST_3));
	        	replacedTwoWithThree = true;
	        }
	        
	        if (insn.getOpcode() == Opcodes.BIPUSH) {
	        	if (((IntInsnNode)insn).operand == 32) {
	        		((IntInsnNode)insn).operand = 40;
	        	}
	        }
	    }
	    
	    if (!hasFoundBrightness) {
	    	FMLLog.severe("Could not find " + unObfBrightness + " or " + obfBrightness + " while transforming Tessellator.draw!");
	    } else if (!replacedTwoWithThree) {
	    	FMLLog.severe("Reached the end of the list without finding a 2 to replace while transforming Tessellator.draw!");
	    }
	    
		return (hasFoundBrightness && replacedTwoWithThree);
	}
}
