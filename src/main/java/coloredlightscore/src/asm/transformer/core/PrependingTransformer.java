package coloredlightscore.src.asm.transformer.core;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

/**
 * The PrependingTransformer.class was based on code written by diesieben07, 
 * who has given express permission for its use in our code.
 * 
 * diesieben07's code had not been classified under the GPL license at the time
 * before we had obtained a copy.
 * 
 * Source: https://github.com/diesieben07/SevenCommons/tree/master/src/main/java/de/take_weiland/mods/commons
 */

public abstract class PrependingTransformer extends SingleMethodTransformer {
    @Override
    protected boolean transform(ClassNode clazz, MethodNode method) {
        method.instructions.insert(getPrepends(clazz, method));
        return true;
    }

    protected abstract InsnList getPrepends(ClassNode clazz, MethodNode method);
}