package coloredlightscore.src.asm.transformer.core;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;
import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLASSLOADER;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.launchwrapper.IClassNameTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

/**
 * The ASMUtils.class was written by diesieben07, 
 * who has given express permission for its use in our code.
 * 
 * diesieben07's code had not been classified under the GPL license at the time
 * before we had obtained a copy.
 * 
 * Source: https://github.com/diesieben07/SevenCommons/blob/master/src/main/java/de/take_weiland/mods/commons/asm/ASMUtils.java
 * 
 * @author diesieben07
 */

public final class ASMUtils {

    private ASMUtils() {
    }

    /**
     * Given a deobfuscated class name, returns the appropriate obfuscated/deobfuscated name ready to use
     * in transformer code.
     * 
     * @author heaton84
     * 
     * @param deobfuscatedClassName Eg: "net.minecraft.block.Block"
     * @return Deobfuscated name if running in eclipse. Obfuscated if running in production.
     * /
    public static final String getClassName(String deobfuscatedClassName)
    {
    	String internalName = ASMUtils.makeNameInternal(deobfuscatedClassName);
    	
    	return MCP_ENVIRONMENT ? internalName :  FMLDeobfuscatingRemapper.INSTANCE.unmap(internalName);
    }
    
    public static final String getClassField(String deobfuscatedClassName, String deobfuscatedFieldName, String deobfuscatedFieldDescriptor)
    {
    	String internalName = ASMUtils.makeNameInternal(deobfuscatedClassName);
    	
    	return MCP_ENVIRONMENT ? deobfuscatedFieldName :  FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(internalName, deobfuscatedFieldName, deobfuscatedFieldDescriptor);
    }
    
    / **
     * Given a deobfuscated type, returns the appropriate obfuscated/deobfuscated name ready to use
     * in transformer code.
     * 
     * @author heaton84
     * 
     * @param deobfuscatedTypeDescriptor Eg: "net.minecraft.world.chunk.NibbleArray"
     * @return Deobfuscated name if running in eclipse. Obfuscated if running in production.
     * /
    public static final Type getType(String deobfuscatedTypeDescriptor)
    {
    	String internalName = ASMUtils.makeNameInternal(deobfuscatedTypeDescriptor);

    	return Type.getType(MCP_ENVIRONMENT ? internalName : FMLDeobfuscatingRemapper.INSTANCE.mapType(internalName));
    }
    
    public static final String getMethod(String owner, String method, String desc)
    {
    	String internalOwner = ASMUtils.makeNameInternal(owner);
    	String internalMethod = ASMUtils.makeNameInternal(method);
    	String internalDesc = ASMUtils.makeNameInternal(desc);

    	if (!MCP_ENVIRONMENT)
    		return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(internalOwner, internalMethod, internalDesc);
    	else
    		return internalMethod + " " + internalDesc;
    }
    
    public static final boolean isMethod(MethodNode m, String owner, String method, String desc)
    {
    	String internalOwner = ASMUtils.makeNameInternal(owner);
    	String internalMethod = ASMUtils.makeNameInternal(method);
    	String internalDesc = ASMUtils.makeNameInternal(desc);
    	
    	String descriptor = MCP_ENVIRONMENT ? internalMethod + " " + internalDesc : FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(internalOwner, internalMethod, internalDesc);
    	
    	CLLog.info("TQ:{} ^ {} {}", descriptor, m.name, m.desc);
    	
    	return descriptor.equals(m.name + " " + m.desc);
    }

    public static final boolean isMethod(MethodNode m, String owner, String methodWithDescriptor)
    {
    	String internalOwner = ASMUtils.makeNameInternal(owner);
    	String internalMethod = ASMUtils.makeNameInternal(methodWithDescriptor);

    	String methodParts[] = internalMethod.split("\\s+");
    	
    	String descriptor = MCP_ENVIRONMENT ? internalMethod : FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(internalOwner, methodParts[0], methodParts[1]);
    	
    	return descriptor.equals(m.name + " " + m.desc);
    }	
    
    public static final String getMethodName(String methodAndDescriptor)
    {
    	String methodParts[] = methodAndDescriptor.split("\\s+");
    	
    	return methodParts[0];
    }

    public static final String getMethodDescriptor(String methodAndDescriptor)
    {
    	String methodParts[] = methodAndDescriptor.split("\\s+");
    	
    	return methodParts[1];
    }
    
    /**
     * Given a JVM method/type descriptor such as "(Ljava/util/ArrayList;Lnet/minecraft/entity/player/EntityPlayerMP;)V",
     * returns an obfuscated descriptor like "(Ljava/util/ArrayList;Lmm;)V". Note: This method will return the
     * deobfuscated descriptor if running in Eclipse (MCP_ENVIRONMENT=true) 
     * 
     * @param descriptor The descriptor to decode
     * @return The proper descriptor
     * /
    public static final String getJVMTypeObfuscated(String descriptor)
    {
    	int i = 0, j;
    	String className = "";
    	String result = "";
    	
    	while (i < descriptor.length())
    	{
    		if (descriptor.substring(i, i + 1).equals("L"))
    		{
    			// Extract class name
    			j = i;
    			
    			while (!descriptor.substring(j, j + 1).equals(";"))
    				j++;
    			
    			className = descriptor.substring(i + 1, j);
    			
    			//CLLog.info("getMethodDescriptorObfuscated - Found class {}", className);
    			
    			result = result + "L" + ASMUtils.getClassName(className) + ";";
    			
    			// Skip to end of class name
    			i = j;
    		}
    		else
    			result = result + descriptor.substring(i, i + 1);
    		
    		i++;
    	}
    	
    	return result;
    }*/

    public static final String deobfuscate(String className, FieldNode field) {
        return deobfuscateField(className, field.name, field.desc);
    }

    public static final String deobfuscateField(String className, String fieldName, String desc) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(className, fieldName, desc);
    }

    public static final String deobfuscate(String className, MethodNode method) {
        return deobfuscateMethod(className, method.name, method.desc);
    }

    public static final String deobfuscateMethod(String className, String methodName, String desc) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, methodName, desc);
    }

    public static final String getFieldDescriptor(ClassNode clazz, String fieldName) {
        for (FieldNode field : clazz.fields) {
            if (field.name.equals(fieldName)) {
                return field.desc;
            }
        }
        return null;
    }

    public static final boolean useMcpNames() {
        return ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT;
    }

    public static final AbstractInsnNode findLastReturn(MethodNode method) {
        int searchFor = Type.getReturnType(method.desc).getOpcode(Opcodes.IRETURN);

        return ASMUtils.findLastOpcode(method, searchFor);
    }

    public static final AbstractInsnNode findLastOpcode(MethodNode method, int opcode) {
        AbstractInsnNode found = null;
        for (int i = 0; i < method.instructions.size(); i++) {
            AbstractInsnNode insn = method.instructions.get(i);
            if (insn.getOpcode() == opcode) {
                found = insn;
            }
        }
        return found;
    }

    /**
     * Finds last occurance of LDC <ldcArgument>
     * 
     * @param method Method to search
     * @param ldcArgument String literal to be found
     * @return LdcInsnNode instance when found, null if not found
     */
    public static final LdcInsnNode findLastLDC(MethodNode method, String ldcArgument) {
        LdcInsnNode found = null;
        LdcInsnNode candidate = null;

        for (int i = 0; i < method.instructions.size(); i++) {
            AbstractInsnNode insn = method.instructions.get(i);
            if (insn.getOpcode() == Opcodes.LDC) {
                candidate = (LdcInsnNode) insn;

                if (ldcArgument.equals(candidate.cst))
                    found = candidate;
            }
        }
        return found;
    }

    public static final MethodInsnNode findLastInvoke(MethodNode method, int opcode, String className, String methodNameAndDescriptor, boolean dumpCandidates) {
        MethodInsnNode found = null;
        MethodInsnNode candidate = null;

        String obfClass = NameMapper.getInstance().getClassName(className);
        String obfName = NameMapper.getInstance().getMethodName(className, methodNameAndDescriptor);
        String obfDesc = NameMapper.getInstance().getMethodDescriptor(className, methodNameAndDescriptor);

        for (int i = 0; i < method.instructions.size(); i++) {
            AbstractInsnNode insn = method.instructions.get(i);
            if (insn.getOpcode() == opcode) {
                candidate = (MethodInsnNode) insn;

                if (dumpCandidates) {
                    CLLog.debug(String.format("   findLastInvoke@%s is %s/%s %s, looking for [%s|%s]/%s %s", i, candidate.owner, candidate.name, candidate.desc, className, obfClass, obfName, obfDesc));
                }

                if ((candidate.owner.equals(obfClass) | candidate.owner.equals(className)) && candidate.name.equals(obfName) && candidate.desc.equals(obfDesc))
                    found = candidate;
            }
        }
        return found;

    }

    public static final String makeNameInternal(String name) {
        return name.replace('.', '/');
    }

    public static final String undoInternalName(String name) {
        return name.replace('/', '.');
    }

    public static final MethodInsnNode generateMethodCall(Method method) {
        int opcode = Modifier.isStatic(method.getModifiers()) ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL;
        return new MethodInsnNode(opcode, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method));
    }

    public static final MethodNode generateSetterMethod(String targetClass, String setterMethodName, String fieldName, String fieldTypeDescriptor) {
        return generateSetterMethod(targetClass, setterMethodName, fieldName, fieldTypeDescriptor, Opcodes.ACC_PUBLIC);
    }

    public static final MethodNode generateSetterMethod(String targetClass, String setterMethodName, String fieldName, String fieldTypeDescriptor, int methodAccess) {
        Type fieldType = Type.getType(fieldTypeDescriptor);

        MethodNode setter = new MethodNode();
        setter.name = setterMethodName;
        setter.desc = String.format("(%s)V", fieldTypeDescriptor);
        setter.exceptions = new ArrayList<String>();
        setter.access = methodAccess;

        setter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // store [this]
        setter.instructions.add(new VarInsnNode(fieldType.getOpcode(Opcodes.ILOAD), 1)); // store argument
        setter.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, targetClass, fieldName, fieldTypeDescriptor));
        setter.instructions.add(new InsnNode(Opcodes.RETURN));

        return setter;
    }

    public static final MethodNode generateGetterMethod(String targetClass, String setterMethodName, String fieldName, String fieldTypeDescriptor) {
        return generateGetterMethod(targetClass, setterMethodName, fieldName, fieldTypeDescriptor, Opcodes.ACC_PUBLIC);
    }

    public static final MethodNode generateGetterMethod(String targetClass, String setterMethodName, String fieldName, String fieldTypeDescriptor, int methodAccess) {
        Type fieldType = Type.getType(fieldTypeDescriptor);

        MethodNode getter = new MethodNode();
        getter.name = setterMethodName;
        getter.desc = String.format("()%s", fieldTypeDescriptor);
        getter.exceptions = new ArrayList<String>();
        getter.access = methodAccess;

        getter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // store [this]
        getter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, targetClass, fieldName, fieldTypeDescriptor));
        getter.instructions.add(new InsnNode(fieldType.getOpcode(Opcodes.IRETURN)));

        return getter;
    }

    public static ClassNode getClassNode(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode clazz = new ClassNode();
        reader.accept(clazz, 0);
        return clazz;
    }

    private static IClassNameTransformer nameTransformer;
    private static boolean nameTransChecked = false;

    public static IClassNameTransformer getClassNameTransformer() {
        if (!nameTransChecked) {
            Iterable<IClassNameTransformer> nameTransformers = Iterables.filter(ColoredLightsCoreLoadingPlugin.CLASSLOADER.getTransformers(), IClassNameTransformer.class);
            nameTransformer = Iterables.getOnlyElement(nameTransformers, null);
        }
        return nameTransformer;
    }

    public static String deobfuscateClass(String obfName) {
        IClassNameTransformer t = getClassNameTransformer();
        return ASMUtils.makeNameInternal(t == null ? obfName : t.remapClassName(obfName));
    }

    public static String obfuscateClass(String deobfName) {
        IClassNameTransformer t = getClassNameTransformer();
        return ASMUtils.makeNameInternal(t == null ? deobfName : t.unmapClassName(deobfName));
    }

    public static ClassNode getClassNode(String name) {
        try {
            return getClassNode(ColoredLightsCoreLoadingPlugin.CLASSLOADER.getClassBytes(obfuscateClass(name)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AnnotationNode getAnnotation(FieldNode field, Class<? extends Annotation> ann) {
        return getAnnotation(JavaUtils.concatNullable(field.visibleAnnotations, field.invisibleAnnotations), ann);
    }

    public static AnnotationNode getAnnotation(ClassNode clazz, Class<? extends Annotation> ann) {
        return getAnnotation(JavaUtils.concatNullable(clazz.visibleAnnotations, clazz.invisibleAnnotations), ann);
    }

    public static AnnotationNode getAnnotation(MethodNode method, Class<? extends Annotation> ann) {
        return getAnnotation(JavaUtils.concatNullable(method.visibleAnnotations, method.invisibleAnnotations), ann);
    }

    public static AnnotationNode getAnnotation(Iterable<AnnotationNode> annotations, Class<? extends Annotation> ann) {
        String desc = Type.getDescriptor(ann);
        for (AnnotationNode node : annotations) {
            if (node.desc.equals(desc)) {
                return node;
            }
        }
        return null;
    }

    public static boolean hasAnnotation(FieldNode field, Class<? extends Annotation> annotation) {
        return getAnnotation(field, annotation) != null;
    }

    public static boolean hasAnnotation(ClassNode clazz, Class<? extends Annotation> annotation) {
        return getAnnotation(clazz, annotation) != null;
    }

    public static boolean hasAnnotation(MethodNode method, Class<? extends Annotation> annotation) {
        return getAnnotation(method, annotation) != null;
    }

    public static boolean isPrimitive(Type type) {
        return type.getSort() != Type.ARRAY && type.getSort() != Type.OBJECT && type.getSort() != Type.METHOD;
    }

    public static ClassInfo getClassInfo(Class<?> clazz) {
        return new ClassInfoFromClazz(clazz);
    }

    public static ClassInfo getClassInfo(ClassNode clazz) {
        return new ClassInfoFromNode(clazz);
    }

    public static ClassInfo getClassInfo(String className) {
        try {
            // 03-05-2014 heaton84 - Fixed NullPointerException

            byte[] bytes = null;

            try {
                bytes = CLASSLOADER.getClassBytes(className);
            } catch (NullPointerException npe) {

            }

            if (bytes != null) {
                return new ClassInfoFromNode(ASMUtils.getClassNode(bytes));
            } else {
                //heaton84: This is a no-no. Class.forName is a minefield during class transformation
                return null;
                //return new ClassInfoFromClazz(Class.forName(ASMUtils.undoInternalName(className)));
            }
        } catch (Exception e) {
            throw JavaUtils.throwUnchecked(e);
        }
    }

    public static interface ClassInfo {

        Collection<String> interfaces();

        String superName();

        String internalName();

        boolean isInterface();

    }

    private static final class ClassInfoFromClazz implements ClassInfo {

        private final Class<?> clazz;
        private final Collection<String> interfaces;

        ClassInfoFromClazz(Class<?> clazz) {
            this.clazz = clazz;
            interfaces = Collections2.transform(Arrays.asList(clazz.getInterfaces()), ClassToNameFunc.INSTANCE);
        }

        @Override
        public Collection<String> interfaces() {
            return interfaces;
        }

        @Override
        public String superName() {
            Class<?> s = clazz.getSuperclass();
            return s == null ? null : ASMUtils.makeNameInternal(s.getCanonicalName());
        }

        @Override
        public String internalName() {
            return ASMUtils.makeNameInternal(clazz.getCanonicalName());
        }

        @Override
        public boolean isInterface() {
            return clazz.isInterface();
        }

    }

    private static final class ClassInfoFromNode implements ClassInfo {

        private final ClassNode clazz;

        ClassInfoFromNode(ClassNode clazz) {
            this.clazz = clazz;
        }

        @Override
        public Collection<String> interfaces() {
            return clazz.interfaces;
        }

        @Override
        public String superName() {
            return clazz.superName;
        }

        @Override
        public String internalName() {
            return clazz.name;
        }

        @Override
        public boolean isInterface() {
            return (clazz.access & ACC_INTERFACE) == ACC_INTERFACE;
        }

    }

    private static enum ClassToNameFunc implements Function<Class<?>, String> {
        INSTANCE;

        @Override
        public String apply(Class<?> input) {
            return ASMUtils.makeNameInternal(input.getCanonicalName());
        }
    }

    public static boolean isAssignableFrom(ClassInfo parent, ClassInfo child) {
        if (parent.internalName().equals(child.internalName()) || parent.internalName().equals(child.superName()) || child.interfaces().contains(parent.internalName())) {
            return true;
        } else if (child.superName() != null && !child.superName().equals("java/lang/Object")) {
            return isAssignableFrom(parent, getClassInfo(child.superName()));
        } else {
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
    public static String getTypeKeyword(Type type) {
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
    public static void assertClassContainsHelperMethod(String className, String methodName, String methodDescriptor) throws IOException, IllegalArgumentException {
        String classPath = "/" + ASMUtils.makeNameInternal(className) + ".class";
        ClassReader classReader = new ClassReader(ASMUtils.class.getResourceAsStream(classPath));
        ClassNode classNode = new ClassNode();
        boolean foundStatic = false;
        boolean foundNonstatic = false;

        classReader.accept(classNode, 0);

        for (MethodNode m : classNode.methods) {
            if (m.name.equals(methodName) && m.desc.equals(methodDescriptor)) {
                // Better make sure it's a static
                if ((m.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC)
                    foundStatic = true;
                else
                    foundNonstatic = true;
            }
        }

        if (!foundStatic) {
            String exceptionMessage;
            String plainTextDescriptor;

            Type expectedReturnType = Type.getReturnType(methodDescriptor);
            Type[] expectedArgs = Type.getArgumentTypes(methodDescriptor);
            int argNum;
            // Turn methodDescriptor (III)Z into plaintext boolean funcname(int, int, int)

            plainTextDescriptor = String.format("%s %s(", ASMUtils.getTypeKeyword(expectedReturnType), methodName);

            for (argNum = 0; argNum < expectedArgs.length; argNum++) {
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
