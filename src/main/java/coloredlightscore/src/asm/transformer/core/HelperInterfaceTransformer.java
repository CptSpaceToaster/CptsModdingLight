package coloredlightscore.src.asm.transformer.core;

import org.objectweb.asm.tree.ClassNode;

public abstract class HelperInterfaceTransformer extends SelectiveTransformer {
    protected String className;
    protected String classNameDeobfuscated;

    public HelperInterfaceTransformer(String className) {
        this.className = className;
        this.classNameDeobfuscated = NameMapper.getInstance().getClassName(className).replace('/', '.');
    }

    @Override
    protected boolean transforms(String className) {
        return className.equals(this.classNameDeobfuscated) || className.equals(this.className);
    }

    @Override
    protected boolean transform(ClassNode clazz, String className) {
        for (String i : this.interfaceList())
            clazz.interfaces.add(i);
        return true;
    }

    /* Return a list of interfaces to apply */
    public abstract String[] interfaceList();
}
