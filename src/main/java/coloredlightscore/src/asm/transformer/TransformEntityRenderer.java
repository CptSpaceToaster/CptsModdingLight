package coloredlightscore.src.asm.transformer;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin;
import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;

public class TransformEntityRenderer extends HelperMethodTransformer {

    // These methods will be replaced by statics in CLRenderBlocksHelper
    String methodsToReplace[] = { "updateLightmap (F)V", "enableLightmap (D)V", "disableLightmap (D)V" };

    String entityRendererConstructor = "<init> (Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V";
    String oldLightmapDesc = "net/minecraft/client/renderer/texture/DynamicTexture";
    String obfOldLightmapDesc = "net/minecraft/client/renderer/texture/DynamicTexture";
    
    boolean addSetter = false;
    
    public TransformEntityRenderer() {
        super("net.minecraft.client.renderer.EntityRenderer");
    }

    @Override
    protected Class<?> getHelperClass() {
        return coloredlightscore.src.helper.CLEntityRendererHelper.class;
    }
    
    @Override
    protected boolean transforms(ClassNode classNode, MethodNode methodNode) {
        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
                return true;
        }  

        if ((methodNode.name + " " + methodNode.desc).equals(entityRendererConstructor))
            return true;

        return false;
    }

    @Override
    public boolean preTransformClass(ClassNode classNode)
    {
        if (!ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT) {
            oldLightmapDesc = obfOldLightmapDesc;
        }
        if(!addSetter) {
            MethodNode setter = new MethodNode(Opcodes.ACC_PUBLIC, "setLightmapTexture", "([I)V", null, null);
            setter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            if (ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT)
                setter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, "lightmapTexture", "L"+oldLightmapDesc+";"));
            else
                setter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, "field_78513_d", "L"+oldLightmapDesc+";"));
            setter.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, oldLightmapDesc));
            setter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
            if(ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT){
            	setter.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, oldLightmapDesc, "dynamicTextureData", "[I"));
            }else{
            	setter.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, oldLightmapDesc, "field_110566_b", "[I"));
            }
            setter.instructions.add(new InsnNode(Opcodes.RETURN));
            classNode.methods.add(setter);
            
            addSetter = true;
        }
        return true;
    }

    @Override
    protected boolean transform(ClassNode classNode, MethodNode methodNode) {
        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name)) {
                return redefineMethod(classNode, methodNode, name);
            }
        }
        
        if ((methodNode.name + " " + methodNode.desc).equals(entityRendererConstructor)) {
            return transformConstructor(methodNode);
        }
        
        return false;
    }
    
    protected boolean transformConstructor(MethodNode methodNode) {
        if (!ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT) {
            oldLightmapDesc = obfOldLightmapDesc;
        }
        //Actions
        boolean found2DLightmap = false;

        for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext();) {
            AbstractInsnNode insn = it.next();

            if (insn.getOpcode() == Opcodes.NEW && !found2DLightmap) {
                if (((TypeInsnNode) insn).desc.equals(oldLightmapDesc)) {
                    
                    CLLog.debug("Replacing 2D lightmap texture");
                    insn = it.next(); //DUP
                    insn = it.next(); //BIPUSH 16
                    it.set(new LdcInsnNode(256));
                    insn = it.next(); //BIPUSH 16
                    it.set(new LdcInsnNode(256));

                    found2DLightmap = true;
                }
            }
        }
        return found2DLightmap;
    }
    
}
