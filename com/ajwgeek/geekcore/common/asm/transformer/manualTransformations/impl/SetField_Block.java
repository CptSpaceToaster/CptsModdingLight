package com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.impl;

import net.minecraft.world.ChunkCache;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.TransformerSingleMethod;
import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.TransformerSingleMethodAppend;
import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.UtilsASM;
import cpw.mods.fml.common.FMLCommonHandler;

public final class SetField_Block extends TransformerSingleMethod implements Opcodes
{
	@Override
	protected String getMcpMethod()
	{
		return "<clinit>";
	}

	@Override
	protected String getSrgMethod()
	{
		return "<clinit>";
	}

	@Override
	protected boolean transforms(String className)
	{
		return className.equals("net.minecraft.block.Block");
	}

	@Override
	protected boolean transform(ClassNode clazz, MethodNode mv)
	{
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitIntInsn(BIPUSH, 16);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Float");
		mv.visitInsn(DUP);
		mv.visitInsn(ICONST_0);
		mv.visitInsn(FCONST_0);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitInsn(ICONST_1);
		mv.visitLdcInsn(new Float("0.06666667"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitInsn(ICONST_2);
		mv.visitLdcInsn(new Float("0.13333334"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitInsn(ICONST_3);
		mv.visitLdcInsn(new Float("0.2"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitInsn(ICONST_4);
		mv.visitLdcInsn(new Float("0.26666668"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitInsn(ICONST_5);
		mv.visitLdcInsn(new Float("0.33333334"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitIntInsn(BIPUSH, 6);
		mv.visitLdcInsn(new Float("0.4"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitIntInsn(BIPUSH, 7);
		mv.visitLdcInsn(new Float("0.46666667"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitIntInsn(BIPUSH, 8);
		mv.visitLdcInsn(new Float("0.53333336"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitIntInsn(BIPUSH, 9);
		mv.visitLdcInsn(new Float("0.6"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitIntInsn(BIPUSH, 10);
		mv.visitLdcInsn(new Float("0.6666667"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitIntInsn(BIPUSH, 11);
		mv.visitLdcInsn(new Float("0.73333335"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitIntInsn(BIPUSH, 12);
		mv.visitLdcInsn(new Float("0.8"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitIntInsn(BIPUSH, 13);
		mv.visitLdcInsn(new Float("0.8666667"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitIntInsn(BIPUSH, 14);
		mv.visitLdcInsn(new Float("0.93333334"));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitInsn(DUP);
		mv.visitIntInsn(BIPUSH, 15);
		mv.visitInsn(FCONST_1);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		mv.visitInsn(AASTORE);
		mv.visitFieldInsn(PUTSTATIC, "com/ajwgeek/geekcore/common/asm/transformer/manualTransformations/impl/SetField_Block", "l", "[Ljava/lang/Float;");
		mv.visitMaxs(4, 0);
		mv.visitEnd();
		return true;
	}
}