package kovukore.asm.transformer.manualTransformations;

import java.util.Arrays;
import java.util.Collection;

import kovukore.asm.transformer.head.ColoredLightsLoadingPlugin;
import kovukore.asm.util.JavaUtils;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class ExtendedClassWriter extends ClassWriter implements Opcodes
{
	public ExtendedClassWriter(int flags)
	{
		super(flags);
	}

	@Override
	protected String getCommonSuperClass(String type1, String type2)
	{
		ClassInfo cl1 = getClassInfo(type1);
		ClassInfo cl2 = getClassInfo(type2);

		if (isAssignableFrom(cl1, cl2))
		{
			return type1;
		}
		if (isAssignableFrom(cl2, cl1))
		{
			return type2;
		}
		if (cl1.isInterface() || cl2.isInterface())
		{
			return "java/lang/Object";
		}
		else
		{
			do
			{
				cl1 = getClassInfo(cl1.superName());
			}
			while (!isAssignableFrom(cl1, cl2));
			return cl1.internalName();
		}
	}

	private static ClassInfo getClassInfo(String className)
	{
		try
		{
			byte[] bytes = ColoredLightsLoadingPlugin.classLoader.getClassBytes(className);
			if (bytes != null)
			{
				return new ClassInfoFromNode(UtilsASM.getClassNode(bytes));
			}
			else
			{
				return new ClassInfoFromClazz(Class.forName(UtilsASM.undoInternalName(className)));
			}
		}
		catch (Exception e)
		{
			throw JavaUtils.throwUnchecked(e);
		}
	}

	private static interface ClassInfo
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
			return s == null ? null : UtilsASM.makeNameInternal(s.getCanonicalName());
		}

		@Override
		public String internalName()
		{
			return UtilsASM.makeNameInternal(clazz.getCanonicalName());
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
			return UtilsASM.makeNameInternal(input.getCanonicalName());
		}
	}

	private static boolean isAssignableFrom(ClassInfo parent, ClassInfo child)
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
}