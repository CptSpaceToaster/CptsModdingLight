package kovukore.asm.transformer;

import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;
import java.util.List;

public class ASMMethodTextReplacementVisitor extends MethodVisitor
{
	public ASMMethodTextReplacementVisitor(int i, MethodVisitor methodVisitor, HashMap<String, String> repl, String methodName)
	{
		super(i, methodVisitor);
		reps = repl;
		mName = methodName;
	}

	String mName = null;
	HashMap<String, String> reps = null;

	@Override
	public void visitTypeInsn(int opcode, String type)
	{
		String newType = type;
		for (String rem : reps.keySet())
		{
			newType = newType.replace(rem, reps.get(rem));
		}
		mv.visitTypeInsn(opcode, type); 
		if (!(type.equals(newType)))
		{
			boolean b = true;
		}
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc)
	{
		String newOwner = owner;
		for (String rem : reps.keySet())
		{
			newOwner = newOwner.replace(rem, reps.get(rem));
		}
		String newDesc = desc;
		for (String rem : reps.keySet())
		{
			newDesc = newDesc.replace(rem, reps.get(rem));
		}
		mv.visitFieldInsn(opcode, owner, name, desc);
		if (!(owner.equals(newOwner)) || !(desc.equals(newDesc)))
		{
			boolean b = true;
		}
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc)
	{
		String newOwner = owner;
		for (String rem : reps.keySet())
		{
			newOwner = newOwner.replace(rem, reps.get(rem));
		}
		String newDesc = desc;
		for (String rem : reps.keySet())
		{
			newDesc = newDesc.replace(rem, reps.get(rem));
		}
		if (name.equals("<init>"))
		{
			System.out.println(opcode);
			System.out.println(newOwner);
			System.out.println(name);
			System.out.println(newDesc);
			mv.visitMethodInsn(opcode, newOwner, name, newDesc);
		}
		else
		{
			mv.visitMethodInsn(opcode, owner, name, newDesc);
		}
		if (!(owner.equals(newOwner)) || !(desc.equals(newDesc)))
		{
			boolean b = true;
		}
	}
}