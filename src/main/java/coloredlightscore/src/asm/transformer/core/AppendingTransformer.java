package coloredlightscore.src.asm.transformer.core;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

/**
 * The AppendingTransformer.class was based on code written by diesieben07, who has given 
 * express permission for its use in our code.
 * 
 * diesieben07's code had not been classified under the GPL license at the time
 * before we had obtained a copy.
 * 
 * Please take that into consideration before copying it further.
 * (Meaning you'd need to adopt the GPL before you copy this outright, 
 *  as it's not technically under the WTFPL)
 * 
 * Source: https://github.com/diesieben07/SevenCommons/tree/master/src/main/java/de/take_weiland/mods/commons
 */

public abstract class AppendingTransformer extends SingleMethodTransformer
{
	@Override
	protected final boolean transform(ClassNode clazz, MethodNode method)
	{
		method.instructions.insertBefore(ASMUtils.findLastReturn(method), getAppends(clazz, method));
		return true;
	}

	protected abstract InsnList getAppends(ClassNode clazz, MethodNode method);
}