package kovukore.asm.transformer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

public class ASMFieldCollector extends FieldNode
{
	public ASMFieldCollector(List<FieldNode> nf, int i, java.lang.String s, java.lang.String s1, java.lang.String s2, java.lang.Object o)
	{
		super(Opcodes.ASM4, i, s, s1, s2, o);
		newFields = nf;
	}

	public List<FieldNode> newFields = null;

	boolean isWriting = false;

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		if (desc.contains("ASMAddField"))
		{
			newFields.add(this);
		}

		return super.visitAnnotation(desc, visible);
	}

}
