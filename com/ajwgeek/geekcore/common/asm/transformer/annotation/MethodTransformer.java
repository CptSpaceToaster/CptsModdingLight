package com.ajwgeek.geekcore.common.asm.transformer.annotation;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import cpw.mods.fml.common.FMLLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Level;

public class MethodTransformer
{
	public MethodTransformer(HashMap<String, String> classesToTransform, HashMap<String, String> MethodRepl)
	{
		registeredClassesForTransforming = classesToTransform;
		methodReplacements = MethodRepl;
	}

	HashMap<String, String> registeredClassesForTransforming = null;
	HashMap<String, String> methodReplacements = null;

	public byte[] transform(String s, byte[] bytes)
	{
		FMLLog.log(Level.INFO, "I just got an " + s);
		try
		{
			FMLLog.log(Level.INFO, "Transforming " + s);
			return transformClass(bytes, registeredClassesForTransforming.get(s));
		}
		catch (IOException e)
		{
			FMLLog.log(Level.SEVERE, "He's dead, Jim! The transformer failed to patch " + s);
			return bytes;
		}
	}

	protected byte[] transformClass(byte[] bytes, String clssname) throws IOException
	{
		InputStream replacementClassInput = getClass().getResourceAsStream("/" + clssname.replace('.', '/') + ".class");
		ClassReader originalClassReader = null;
		ClassReader classReaderReplacement = new ClassReader(replacementClassInput);
		ClassNode classNodeReplacement = new ClassNode(Opcodes.ASM4);
		classReaderReplacement.accept(classNodeReplacement, ClassReader.SKIP_FRAMES);
		ClassReader classReaderOriginal = new ClassReader(bytes);
		ClassNode classNodeOriginal = new ClassNode(Opcodes.ASM4);
		classReaderOriginal.accept(classNodeOriginal, ClassReader.SKIP_FRAMES);
		for (Object method : classNodeReplacement.methods)
		{
			MethodNode replacementMethodNode = (MethodNode) method;
			System.out.println("5. Got here: Recieving Orders: Find " + ((MethodNode)method).name);
//			if (hasReplaceMethodAnnotation(replacementMethodNode.visibleAnnotations))
//			{
//				System.out.println("6. Got here: ");
//				MethodNode methodNodeOriginal = findTheMethod(classNodeOriginal.methods, replacementMethodNode);
//				System.out.println("7. Got here: ");
//				if (methodNodeOriginal != null)
//				{
//					System.out.println("8. Got here: ");
//					classNodeOriginal.methods.remove(methodNodeOriginal);
//					classNodeOriginal.methods.add(classNodeOriginal.methods.size(), fullyCleanMethod(classNodeOriginal, classNodeReplacement, replacementMethodNode));
//					System.out.println("9. Got here: ");
//				}
//			}
//			else if (hasCreateMethodAnnotation(replacementMethodNode.visibleAnnotations))
//			{
//				System.out.println("10. Got here: ");
//				classNodeOriginal.methods.add(classNodeOriginal.methods.size(), fullyCleanMethod(classNodeOriginal, classNodeReplacement, replacementMethodNode));
//				System.out.println("11. Got here: ");
//			}
			
			if(((MethodNode)method).name.contains("<init>")) {
				System.out.println("6. Target avoided");
			} else {
				MethodNode methodNodeOriginal = findTheMethod(classNodeOriginal.methods, replacementMethodNode);
				System.out.println("6. Target aquired: ");
				classNodeOriginal.methods.remove(methodNodeOriginal);
				System.out.println("7. Nuking: ");
				classNodeOriginal.methods.add(classNodeOriginal.methods.size(), fullyCleanMethod(classNodeOriginal, classNodeReplacement, replacementMethodNode));
				System.out.println("8. Rebuilding: ");
			}
			
		}
		ClassWriter cwNew = new ClassWriter(0);
		classNodeOriginal.accept(cwNew);
		System.out.println("Mission Accomplished");
		return cwNew.toByteArray();
	}

	protected MethodNode fullyCleanMethod(ClassNode originalClassNode, ClassNode replacementClassNode, MethodNode methodNode)
	{
		if (methodReplacements.containsKey(methodNode.name))
		{
			methodNode.name = methodReplacements.get(methodNode.name);
		}
		for (Object methodInstruction : methodNode.instructions.toArray())
		{
			if (methodInstruction instanceof MethodInsnNode)
			{
				if (((MethodInsnNode) methodInstruction).owner.equals(replacementClassNode.name))
				{
					((MethodInsnNode) methodInstruction).owner = originalClassNode.name;
				}
				if (methodReplacements.containsKey(((MethodInsnNode) methodInstruction).name))
				{
					((MethodInsnNode) methodInstruction).name = methodReplacements.get(((MethodInsnNode) methodInstruction).name);
				}
			}
		}
		return methodNode;
	}

	private MethodNode findTheMethod(List allMethods, MethodNode replacementMethodNode)
	{
		String methodToFind = replacementMethodNode.name;
		if (methodReplacements.containsKey(replacementMethodNode.name))
		{
			methodToFind = methodReplacements.get(replacementMethodNode.name);
		}
		for (Object originalMethod : allMethods)
		{
			MethodNode newMethodNode = (MethodNode) originalMethod;
			if (newMethodNode.name.equals(methodToFind) && newMethodNode.desc.equals(replacementMethodNode.desc))
			{
				return newMethodNode;
			}
		}
		return null;
	}

	protected boolean hasReplaceMethodAnnotation(List<AnnotationNode> nodes)
	{
		boolean hasAddAnnotation = false;
		for (AnnotationNode annotation : nodes)
		{
			if (annotation.desc != null && annotation.desc.contains("MethodReplace"))
			{
				hasAddAnnotation = true;
			}
		}
		return false;
	}

	protected boolean hasCreateMethodAnnotation(List<AnnotationNode> nodes)
	{
		boolean hasAddAnnotation = false;
		for (AnnotationNode annotation : nodes)
		{
			if (annotation.desc != null && annotation.desc.contains("MethodCreate"))
			{
				hasAddAnnotation = true;
			}
		}
		return false;
	}
}