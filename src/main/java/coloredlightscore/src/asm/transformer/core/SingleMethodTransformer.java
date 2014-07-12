package coloredlightscore.src.asm.transformer.core;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * The SingleMethodTransformer.class was based on code written by diesieben07, 
 * who has given express permission for its use in our code.
 * 
 * diesieben07's code had not been classified under the GPL license at the time
 * before we had obtained a copy.
 * 
 * Source: https://github.com/diesieben07/SevenCommons/tree/master/src/main/java/de/take_weiland/mods/commons
 */

public abstract class SingleMethodTransformer extends MethodTransformer {
    @Override
    protected final boolean transforms(ClassNode clazz, MethodNode method) {
        //return method.name.equals(getMcpMethod()) || ASMUtils.deobfuscate(clazz.name, method).equals(getSrgMethod());
        return NameMapper.getInstance().isMethod(method, getClassName(), getMcpMethod());
    }

    protected abstract String getClassName();

    protected abstract String getMcpMethod();
}