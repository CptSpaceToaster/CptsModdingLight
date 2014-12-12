package coloredlightscore.src.asm.transformer.core;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * The MethodTransformer.class was based on code written by diesieben07, 
 * who has given express permission for its use in our code.
 * 
 * diesieben07's code had not been classified under the GPL license at the time
 * before we had obtained a copy.
 * 
 * Source: https://github.com/diesieben07/SevenCommons/tree/master/src/main/java/de/take_weiland/mods/commons
 */

public abstract class MethodTransformer extends SelectiveTransformer {
    @Override
    protected boolean transform(ClassNode clazz, String className) {
        // 03-06-2014 heaton84: Made so that it will transform more than one method
        boolean result = preTransformClass(clazz);

        for (MethodNode method : clazz.methods) {
            if (transforms(clazz, method)) {
                CLLog.info("Transforming method " + method.name);
                result &= transform(clazz, method);
            }
        }

        result &= postTransformClass(clazz);

        return result;
    }

    protected abstract boolean transforms(ClassNode clazz, MethodNode method);

    protected abstract boolean transform(ClassNode clazz, MethodNode method);

    protected boolean preTransformClass(ClassNode clazz) {
        return true;
    }

    protected boolean postTransformClass(ClassNode clazz) {
        return true;
    }

}