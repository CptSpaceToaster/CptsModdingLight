package kovukore.coloredlights.src.asm.transformer.core;

import static kovukore.coloredlights.src.asm.ColoredLightsCoreLoadingPlugin.CLASSLOADER;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

import kovukore.coloredlights.src.asm.ColoredLightsCoreLoadingPlugin;
import net.minecraft.launchwrapper.IClassNameTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public final class ASMUtils
{

	private ASMUtils()
	{
	}

	public static final String deobfuscate(String className, FieldNode field)
	{
		return deobfuscateField(className, field.name, field.desc);
	}

	public static final String deobfuscateField(String className, String fieldName, String desc)
	{
		return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(className, fieldName, desc);
	}

	public static final String deobfuscate(String className, MethodNode method)
	{
		return deobfuscateMethod(className, method.name, method.desc);
	}

	public static final String deobfuscateMethod(String className, String methodName, String desc)
	{
		return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, methodName, desc);
	}

	public static final String getFieldDescriptor(ClassNode clazz, String fieldName)
	{
		for (FieldNode field : clazz.fields)
		{
			if (field.name.equals(fieldName))
			{
				return field.desc;
			}
		}
		return null;
	}

	public static final boolean useMcpNames()
	{
		return ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT;
	}

	public static final AbstractInsnNode findLastReturn(MethodNode method)
	{
		int searchFor = Type.getReturnType(method.desc).getOpcode(Opcodes.IRETURN);
		AbstractInsnNode found = null;
		for (int i = 0; i < method.instructions.size(); i++)
		{
			AbstractInsnNode insn = method.instructions.get(i);
			if (insn.getOpcode() == searchFor)
			{
				found = insn;
			}
		}
		return found;
	}

	public static final String makeNameInternal(String name)
	{
		return name.replace('.', '/');
	}

	public static final String undoInternalName(String name)
	{
		return name.replace('/', '.');
	}

	public static final MethodInsnNode generateMethodCall(String targetClass, String methodName, Type returnType, Type... params)
	{
		return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, makeNameInternal(targetClass), methodName, Type.getMethodDescriptor(returnType, params));
	}

	public static final MethodInsnNode generateStaticMethodCall(String targetClass, String methodName, Type returnType, Type... params)
	{
		return new MethodInsnNode(Opcodes.INVOKESTATIC, makeNameInternal(targetClass), methodName, Type.getMethodDescriptor(returnType, params));
	}

	public static final MethodInsnNode generateMethodCall(Method method)
	{
		int opcode = Modifier.isStatic(method.getModifiers()) ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL;
		return new MethodInsnNode(opcode, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method));
	}

	public static ClassNode getClassNode(byte[] bytes)
	{
		ClassReader reader = new ClassReader(bytes);
		ClassNode clazz = new ClassNode();
		reader.accept(clazz, 0);
		return clazz;
	}

	private static IClassNameTransformer nameTransformer;
	private static boolean nameTransChecked = false;

	public static IClassNameTransformer getClassNameTransformer()
	{
		if (!nameTransChecked)
		{
			Iterable<IClassNameTransformer> nameTransformers = Iterables.filter(ColoredLightsCoreLoadingPlugin.CLASSLOADER.getTransformers(), IClassNameTransformer.class);
			nameTransformer = Iterables.getOnlyElement(nameTransformers, null);
		}
		return nameTransformer;
	}

	public static String deobfuscateClass(String obfName)
	{
		IClassNameTransformer t = getClassNameTransformer();
		return ASMUtils.makeNameInternal(t == null ? obfName : t.remapClassName(obfName));
	}

	public static String obfuscateClass(String deobfName)
	{
		IClassNameTransformer t = getClassNameTransformer();
		return ASMUtils.makeNameInternal(t == null ? deobfName : t.unmapClassName(deobfName));
	}

	public static ClassNode getClassNode(String name)
	{
		try
		{
			return getClassNode(ColoredLightsCoreLoadingPlugin.CLASSLOADER.getClassBytes(obfuscateClass(name)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static AnnotationNode getAnnotation(FieldNode field, Class<? extends Annotation> ann)
	{
		return getAnnotation(JavaUtils.concatNullable(field.visibleAnnotations, field.invisibleAnnotations), ann);
	}

	public static AnnotationNode getAnnotation(ClassNode clazz, Class<? extends Annotation> ann)
	{
		return getAnnotation(JavaUtils.concatNullable(clazz.visibleAnnotations, clazz.invisibleAnnotations), ann);
	}

	public static AnnotationNode getAnnotation(MethodNode method, Class<? extends Annotation> ann)
	{
		return getAnnotation(JavaUtils.concatNullable(method.visibleAnnotations, method.invisibleAnnotations), ann);
	}

	public static AnnotationNode getAnnotation(Iterable<AnnotationNode> annotations, Class<? extends Annotation> ann)
	{
		String desc = Type.getDescriptor(ann);
		for (AnnotationNode node : annotations)
		{
			if (node.desc.equals(desc))
			{
				return node;
			}
		}
		return null;
	}

	public static boolean hasAnnotation(FieldNode field, Class<? extends Annotation> annotation)
	{
		return getAnnotation(field, annotation) != null;
	}

	public static boolean hasAnnotation(ClassNode clazz, Class<? extends Annotation> annotation)
	{
		return getAnnotation(clazz, annotation) != null;
	}

	public static boolean hasAnnotation(MethodNode method, Class<? extends Annotation> annotation)
	{
		return getAnnotation(method, annotation) != null;
	}

	public static boolean isPrimitive(Type type)
	{
		return type.getSort() != Type.ARRAY && type.getSort() != Type.OBJECT && type.getSort() != Type.METHOD;
	}

	public static ClassInfo getClassInfo(Class<?> clazz)
	{
		return new ClassInfoFromClazz(clazz);
	}

	public static ClassInfo getClassInfo(ClassNode clazz)
	{
		return new ClassInfoFromNode(clazz);
	}

	public static ClassInfo getClassInfo(String className)
	{
		try
		{
			byte[] bytes = CLASSLOADER.getClassBytes(className);
			if (bytes != null)
			{
				return new ClassInfoFromNode(ASMUtils.getClassNode(bytes));
			}
			else
			{
				return new ClassInfoFromClazz(Class.forName(ASMUtils.undoInternalName(className)));
			}
		}
		catch (Exception e)
		{
			throw JavaUtils.throwUnchecked(e);
		}
	}

	public static interface ClassInfo
	{

		Collection<String> interfaces();

		String superName();

		String internalName();

		boolean isInterface();

	}

	private static final class ClassInfoFromClazz implements ClassInfo
	{

		private final Class<?> clazz;
		private final Collection<String> interfaces;

		ClassInfoFromClazz(Class<?> clazz)
		{
			this.clazz = clazz;
			interfaces = Collections2.transform(Arrays.asList(clazz.getInterfaces()), ClassToNameFunc.INSTANCE);
		}

		@Override
		public Collection<String> interfaces()
		{
			return interfaces;
		}

		@Override
		public String superName()
		{
			Class<?> s = clazz.getSuperclass();
			return s == null ? null : ASMUtils.makeNameInternal(s.getCanonicalName());
		}

		@Override
		public String internalName()
		{
			return ASMUtils.makeNameInternal(clazz.getCanonicalName());
		}

		@Override
		public boolean isInterface()
		{
			return clazz.isInterface();
		}

	}

	private static final class ClassInfoFromNode implements ClassInfo
	{

		private final ClassNode clazz;

		ClassInfoFromNode(ClassNode clazz)
		{
			this.clazz = clazz;
		}

		@Override
		public Collection<String> interfaces()
		{
			return clazz.interfaces;
		}

		@Override
		public String superName()
		{
			return clazz.superName;
		}

		@Override
		public String internalName()
		{
			return clazz.name;
		}

		@Override
		public boolean isInterface()
		{
			return (clazz.access & ACC_INTERFACE) == ACC_INTERFACE;
		}

	}

	private static enum ClassToNameFunc implements Function<Class<?>, String>
	{
		INSTANCE;

		@Override
		public String apply(Class<?> input)
		{
			return ASMUtils.makeNameInternal(input.getCanonicalName());
		}
	}

	public static boolean isAssignableFrom(ClassInfo parent, ClassInfo child)
	{
		if (parent.internalName().equals(child.internalName()) || parent.internalName().equals(child.superName()) || child.interfaces().contains(parent.internalName()))
		{
			return true;
		}
		else if (child.superName() != null && !child.superName().equals("java/lang/Object"))
		{
			return isAssignableFrom(parent, getClassInfo(child.superName()));
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Given a type, returns the java keyword for it. Used to assemble
	 * exception messages by the Transformer classes (I expected "int blah(float blah)").
	 * Not tested on arrays.
	 * 
	 * @param type
	 * @return
	 */
	public static String getTypeKeyword(Type type)
	{
		// Surely there must be a better way...
		// Also not sure how this reacts with arrays...
		
		if (type == Type.BOOLEAN_TYPE)
			return "boolean";
		if (type == Type.BYTE_TYPE)
			return "byte";
		if (type == Type.CHAR_TYPE)
			return "char";
		if (type == Type.DOUBLE_TYPE)
			return "double";
		if (type == Type.FLOAT_TYPE)
			return "float";
		if (type == Type.INT_TYPE)
			return "int";
		if (type == Type.LONG_TYPE)
			return "long";
		if (type == Type.SHORT_TYPE)
			return "short";
		if (type == Type.VOID_TYPE)
			return "void";
				
		String internalName = type.getInternalName();
		
		int lastSlash = internalName.lastIndexOf('/');
		
		if (lastSlash > -1)
			return internalName.substring(lastSlash + 1);
		else
			return internalName;
	}

	/**
	 * Tests for the presence of a method with the given name and descriptor in a target
	 * class. If the method is not found, throws an IllegalArgumentException.
	 * 
	 * @author heaton84
	 * 
	 * @param className The name of the class to search
	 * @param methodName The name of the method to look for
	 * @param methodDescriptor The descriptor of the method to look for
	 * @throws IOException When the class cannot be found as named
	 * @throws IllegalArgumentException When the method is not found
	 */
	public static void assertClassContainsHelperMethod(String className,
			String methodName, String methodDescriptor) throws IOException, IllegalArgumentException
	{
		String classPath = "/" + ASMUtils.makeNameInternal(className) + ".class";
		ClassReader classReader = new ClassReader(ASMUtils.class.getResourceAsStream(classPath));
		ClassNode classNode = new ClassNode();
		boolean foundStatic = false;
		boolean foundNonstatic = false;

		classReader.accept(classNode, 0);

		for (MethodNode m : classNode.methods) {
			if (m.name.equals(methodName) && m.desc.equals(methodDescriptor))
			{
				// Better make sure it's a static
				if ((m.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC)
					foundStatic = true;
				else
					foundNonstatic = true;
			}
		}

		if (!foundStatic)
		{
			String exceptionMessage;
			String plainTextDescriptor;
			
			Type expectedReturnType = Type.getReturnType(methodDescriptor);
			Type[] expectedArgs = Type.getArgumentTypes(methodDescriptor);
			int argNum;
			// Turn methodDescriptor (III)Z into plaintext boolean funcname(int, int, int)
						
			plainTextDescriptor = String.format("%s %s(", ASMUtils.getTypeKeyword(expectedReturnType), methodName); 
			
			for (argNum=0;argNum<expectedArgs.length;argNum++)
			{
				if (argNum > 0 && argNum <= expectedArgs.length - 1)
					plainTextDescriptor += ", ";
				
				plainTextDescriptor += ASMUtils.getTypeKeyword(expectedArgs[argNum]); // getClassNameFromInternalName(expectedArgs[argNum].getInternalName());
			}
			
			if (foundNonstatic)
				exceptionMessage = String.format("Missing STATIC modifier on helper method %s.%s %s!", className, methodName, methodDescriptor);
			else
				exceptionMessage = String.format("Unable to locate helper method \"static %s)\" in class %s!", plainTextDescriptor, className);
			
			throw new IllegalArgumentException(exceptionMessage);
		}
	}
	
}
