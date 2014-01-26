package kovukore.asm.transformer.manualTransformations;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import kovukore.asm.transformer.head.ColoredLightsLoadingPlugin;
import kovukore.asm.util.JavaUtils;

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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class UtilsASM
{
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
		try
		{
			return Class.forName("net.minecraft.world.World") != null;
		}
		catch (ClassNotFoundException e)
		{
			return true;
		}
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
			Iterable<IClassNameTransformer> nameTransformers = Iterables.filter(ColoredLightsLoadingPlugin.classLoader.getTransformers(), IClassNameTransformer.class);
			nameTransformer = Iterables.getOnlyElement(nameTransformers, null);
		}
		return nameTransformer;
	}

	public static String deobfuscateClass(String obfName)
	{
		IClassNameTransformer t = getClassNameTransformer();
		return UtilsASM.makeNameInternal(t == null ? obfName : t.remapClassName(obfName));
	}

	public static String obfuscateClass(String deobfName)
	{
		if (deobfName.startsWith("kovukore.asm") || deobfName.startsWith("yamhaven"))
		{
			return deobfName;
		}
		else
		{
			IClassNameTransformer t = getClassNameTransformer();
			return UtilsASM.makeNameInternal(t == null ? deobfName : t.unmapClassName(deobfName));
		}
	}

	public static ClassNode getClassNode(String name)
	{
		try
		{
			return getClassNode(ColoredLightsLoadingPlugin.classLoader.getClassBytes(obfuscateClass(name)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static boolean hasAnnotation(FieldNode field, Class<? extends Annotation> annotation)
	{
		return hasAnnotation(field, Type.getType(annotation));
	}

	public static boolean hasAnnotation(FieldNode field, Type annotation)
	{
		return containsAnnotation(Iterators.concat(JavaUtils.nullToEmpty(field.visibleAnnotations).iterator(), JavaUtils.nullToEmpty(field.invisibleAnnotations).iterator()), annotation.getDescriptor());
	}

	private static boolean containsAnnotation(Iterator<AnnotationNode> annotations, final String annotationDesc)
	{
		return Iterators.any(annotations, new Predicate<AnnotationNode>()
		{
			@Override
			public boolean apply(AnnotationNode node)
			{
				return node.desc.equals(annotationDesc);
			}
		});
	}

	public static boolean isPrimitive(Type type)
	{
		return type.getSort() != Type.ARRAY && type.getSort() != Type.OBJECT && type.getSort() != Type.METHOD;
	}
}