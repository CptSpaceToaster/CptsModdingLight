package coloredlightscore.src.asm.transformer;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin;
import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import cpw.mods.fml.common.FMLLog;

public class TransformEntityRenderer extends HelperMethodTransformer {

    // These methods will be replaced by statics in CLRenderBlocksHelper
    String methodsToReplace[] = { "updateLightmap (F)V", "enableLightmap (D)V", "disableLightmap (D)V" };

    //TODO: Is this obfuscated later?  If it is, then add both entries to the NameMapper class instead
    String entityRendererConstructor = "<init> (Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V";
    String obfEntityRendererConstructor = "<init> (Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V";
    String oldLightmapDesc = "net/minecraft/client/renderer/texture/DynamicTexture";
    String obfOldLightmapDesc = "net/minecraft/client/renderer/texture/DynamicTexture";
    String entityRenderClass = "net/minecraft/client/renderer/EntityRenderer";
    String obfEntityRenderClass = "net/minecraft/client/renderer/EntityRenderer";
    
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

        if ((methodNode.name + " " + methodNode.desc).equals(entityRendererConstructor) ||
            (methodNode.name + " " + methodNode.desc).equals(obfEntityRendererConstructor))
            return true;

        return false;
    }

    @Override
    public boolean preTransformClass(ClassNode classNode)
    {
        if (!ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT) {
            oldLightmapDesc = obfOldLightmapDesc;
            entityRenderClass = obfEntityRenderClass;
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
        
        if ((methodNode.name + " " + methodNode.desc).equals(entityRendererConstructor) ||
            (methodNode.name + " " + methodNode.desc).equals(obfEntityRendererConstructor)) {
            return transformConstructor(methodNode);
        }
        
        return false;
    }
    
    protected boolean transformConstructor(MethodNode methodNode) {
        if (!ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT) {
            oldLightmapDesc = obfOldLightmapDesc;
            entityRenderClass = obfEntityRenderClass;
        }
        //Actions
        boolean found2DLightmap = false;
        boolean add3DLightmap = false;
        boolean removeTextureLocation = false;

        for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext();) {
            AbstractInsnNode insn = it.next();

            if (insn.getOpcode() == Opcodes.NEW && !found2DLightmap) {
                if (((TypeInsnNode) insn).desc.equals(oldLightmapDesc)) {
                    
                    FMLLog.info("Replacing 2D lightmap texture");
                    insn = it.next(); //DUP
                    insn = it.next(); //BIPUSH 16
                    it.set(new LdcInsnNode(256));
                    insn = it.next(); //BIPUSH 16
                    it.set(new LdcInsnNode(256));
                    insn = it.next(); //Constructor call to the DynamicTexture - INVOKESPECIAL
                    insn = it.next(); //Storing the value to the local field - PUTFIELD

                    found2DLightmap = true;
                }
            }

            /* This is a bit lazy, but the next line is 10 instructions long and 'needs' to be removed */
            if (!removeTextureLocation && found2DLightmap) {
                
                FMLLog.info("Removing locationLightMap");
                for (int i = 0; i < 10; i++) {
                    insn = it.next();
                    //it.remove();
                }
                
                removeTextureLocation = true;
            }

            if (!add3DLightmap && removeTextureLocation && found2DLightmap) {
                
                add3DLightmap = true;
            }
        }
        return (found2DLightmap && removeTextureLocation && add3DLightmap); // && fixGetTextureData
    }
    
}
