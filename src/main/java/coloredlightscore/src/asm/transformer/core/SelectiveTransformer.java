package coloredlightscore.src.asm.transformer.core;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.base.Throwables;

/**
 * The SelectiveTransformer.class was based on code written by diesieben07, 
 * who has given express permission for its use in our code.
 * 
 * diesieben07's code had not been classified under the GPL license at the time
 * before we had obtained a copy.
 * 
 * Source: https://github.com/diesieben07/SevenCommons/tree/master/src/main/java/de/take_weiland/mods/commons
 */

public abstract class SelectiveTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes != null && transforms(transformedName)) {
            CLLog.info("Class {} is a candidate for transforming", transformedName);

            try {
                ClassNode clazz = ASMUtils.getClassNode(bytes);
                if (transform(clazz, transformedName)) {
                    CLLog.info("Finished Transforming class " + transformedName);
                    ClassWriter writer = new ExtendedClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                    clazz.accept(writer);
                    bytes = writer.toByteArray();
                } else
                    CLLog.warn("Did not transform {}", transformedName);
            } catch (Exception e) {
                CLLog.error("Exception during transformation of class " + transformedName);
                e.printStackTrace();
                Throwables.propagate(e);
            }
        }
        return bytes;
    }

    protected abstract boolean transforms(String className);

    protected abstract boolean transform(ClassNode clazz, String className);
}