package coloredlightscore.src.asm.transformer;

import coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin;
import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import coloredlightscore.src.helper.CLFontRendererHelper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created by Murray on 12/9/2014.
 */
public class TransformFontRenderer extends HelperMethodTransformer {

    String methodsToReplace[] = { "renderDefaultChar (IZ)F", "renderUnicodeChar (CZ)F" };
    private final static String obfCharWidth = "field_78286_d";
    private final static String deobfCharWidth = "charWidth";

    public TransformFontRenderer() {
        super("net.minecraft.client.gui.FontRenderer");
    }

    @Override
    protected Class<?> getHelperClass() {
        return coloredlightscore.src.helper.CLFontRendererHelper.class;
    }

    @Override
    protected boolean transforms(ClassNode clazz, MethodNode methodNode) {
        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
                return true;
        }
        return false;
    }

    @Override
    protected boolean preTransformClass(ClassNode clazz) {
        String charWidth = ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT ? deobfCharWidth : obfCharWidth;
        for (FieldNode field : clazz.fields) {
            if (field.name.equals(charWidth)) {
                CLFontRendererHelper.optifineUpInThisFontRenderer = false;
                return true;
            }
        }
        CLFontRendererHelper.optifineUpInThisFontRenderer = true;
        return true;
    }

    @Override
    protected boolean transform(ClassNode clazz, MethodNode methodNode) {
        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
                return redefineMethod(clazz, methodNode, name);
        }
        return false;
    }

}
