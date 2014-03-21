package kovukore.coloredlights.src.asm.transformer.core;

import java.io.IOException;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.Opcodes;

public abstract class HelperMethodTransformer extends MethodTransformer {
	
	protected String className;
	
	/**
	 * When set, this class will check for the existance of the proper target function.
	 * This is useful during development to help narrow down programming errors that would
	 * otherwise crash the game. Set to false on release.
	 */
	public boolean checkForHelperFunction = true;
	
	public HelperMethodTransformer(String className) {

		this.className = className;
				
	}
	
	protected abstract Class<?> getHelperClass();

	@Override
	protected boolean transforms(String className) {
		
		// TODO: Is the call to deobfuscateClass needed? Not tested yet.
		return className.equals(this.className); // || ASMUtils.deobfuscateClass(className).equals(this.className);
	}	
	
	/**
	 * Replaces the entire target method with a single call to the helper method. This is the more
	 * invasive method of transformation, and should only be used when addReturnMethod is not an
	 * option!
	 * 
	 * Method Descriptor Details: For TargetMethod(args...), HelperMethod must be HelperMethod(targetClass, args...), and MUST MATCH IN RETURN TYPE.
	 * targetClass is the instance of the targetClass that targetMethod is running under.
	 * 
	 * @author heaton84
	 * @param clazz
	 * @param method Reference of the method to transform
	 * @param helperMethod The name of the method in the helper class to invoke. See description for Descriptor details.
	 * @return
	 */
	protected final boolean redefineMethod(ClassNode clazz, MethodNode targetMethod, String helperMethod)
	{		
		Type targetMethodReturnType = Type.getReturnType(targetMethod.desc);
		InsnList staticInvoke = generateHelperInvoke(clazz, targetMethod, helperMethod, false);
		
		int returnOpcode = targetMethodReturnType.getOpcode(Opcodes.IRETURN);		

		staticInvoke.add(new InsnNode(returnOpcode));
				
		// Turns out this bit is important, otherwise we could step on the existing LVT
		targetMethod.localVariables.clear();
		
		targetMethod.instructions.clear();
		targetMethod.instructions.add(staticInvoke);
		
		return true;
	}	
	
	/**
	 * Replaces the return statement of the target method with an invokation to the helper method.
	 * This is the preferred method of transformation, but is very simplistic.
	 *
	 * Method Descriptor Details: For TargetMethod(args...), HelperMethod must be HelperMethod(retVar, targetClass, args...), and MUST MATCH IN RETURN TYPE.
	 * retVar will be the value that *would* have been returned by the target method.
	 * targetClass is the instance of the targetClass that targetMethod is running under.
	 * 
	 * @author heaton84
	 * @param clazz
	 * @param targetMethod
	 * @param helperMethod The name of the method in the helper class to invoke. Refer to method documentation for proper Descriptor!
	 * @return
	 */
	protected final boolean addReturnMethod(ClassNode clazz, MethodNode targetMethod, String helperMethod)
	{
		// NOTE new helper sig: helper(ret, ref, args) for target(args)
		
		InsnList staticInvoke = generateHelperInvoke(clazz, targetMethod, helperMethod, true);

		// NOTE: This logic assumes that there is only ONE return instruction in the method!
		targetMethod.instructions.insertBefore(ASMUtils.findLastReturn(targetMethod), staticInvoke);
		
		return true;
	}		
	
	protected InsnList generateHelperInvoke(ClassNode targetClass, MethodNode targetMethod, String helperMethod, boolean includeReturnVarAsParam)
	{
		InsnList staticInvoke = new InsnList();

		Type targetClassType = Type.getType(targetClass.name);
		Type helperClassType = Type.getType(getHelperClass());		
		Type[] args = Type.getArgumentTypes(targetMethod.desc);

		int argIndex;
		String helperMethodDescriptor;
				
		// Prepare helperMethodDescriptor
		// Refer to add...Method documentation for proper method Descriptors of helper methods (or just read the comments below)
						
		if (includeReturnVarAsParam)
		{
			// Descriptor should be:
			//   Param 0 <Type targetMethodReturnType> - What target method was about to return
			//   Param 1 <Type targetClass> - The instance of the target class
			//   Param 2..n - All parameters that were passed to targetMethod
			helperMethodDescriptor = String.format("(L%s;L%s;%s",
					Type.getReturnType(targetMethod.desc).getInternalName(),
					targetClassType.getInternalName(),
					targetMethod.desc.substring(1)); // Omit leading '(' as we just redefined it			
		}
		else
		{
			// Descriptor should be:
			//   Param 0 <Type targetClass> - The instance of the target class
			//   Param 1..n - All parameters that were passed to targetMethod
			helperMethodDescriptor = String.format("(L%s;%s",
					targetClassType.getInternalName(),
					targetMethod.desc.substring(1)); // Omit leading '(' as we just redefined it
		}
		
		if (checkForHelperFunction)
		{
			// Debugging trap to assert that the helper function exists with
			// the correct method descriptor

			try
			{
				ASMUtils.assertClassContainsHelperMethod(getHelperClass().getName(), helperMethod, helperMethodDescriptor);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		// Push reference to target class onto stack : aload_0 [this]
		staticInvoke.add(new VarInsnNode(Opcodes.ALOAD, 0));
				
		// Push all calling arguments from target method onto stack
		for (argIndex = 0; argIndex < args.length; argIndex++)
		{
			int LoadOpCode = args[argIndex].getOpcode(Opcodes.ILOAD);
			staticInvoke.add(new VarInsnNode(LoadOpCode, argIndex + 1));
		}
				
		// Now prepare a call to a static method (did I mention the helper method should be static?)
		MethodInsnNode invokestatic = new MethodInsnNode(Opcodes.INVOKESTATIC,
				helperClassType.getInternalName(),	// target class of invoke
				helperMethod,						// target method of invoke
				helperMethodDescriptor);			// target Descriptor of invoke
				
		staticInvoke.add(invokestatic);
		
		return staticInvoke;
	}
}
