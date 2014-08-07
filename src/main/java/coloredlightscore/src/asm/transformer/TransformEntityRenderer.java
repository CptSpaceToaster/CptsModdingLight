package coloredlightscore.src.asm.transformer;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin;
import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import coloredlightscore.src.types.CLEntityRendererInterface;
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
    String new3DDesc = "coloredlightscore/src/types/CLDynamicTexture3D";
    String new2DDesc = "coloredlightscore/src/types/CLDynamicTexture2D";
    
    boolean addSetterAndInterface = false;
    
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
        if(!addSetterAndInterface) {
            //implements CLEntityRendererInterface
            classNode.interfaces.add(CLEntityRendererInterface.appliedInterface);
            
            //Add some Dynamic Textures
            classNode.fields.add( new FieldNode(Opcodes.ACC_PUBLIC, CLEntityRendererInterface.fieldName2, "L"+oldLightmapDesc+";", null, null));
            classNode.fields.add( new FieldNode(Opcodes.ACC_PUBLIC, CLEntityRendererInterface.fieldName3, "L"+oldLightmapDesc+";", null, null));

            /* Just cramming a getter and a setter, don't you mind */
            MethodNode getter1 = new MethodNode(Opcodes.ACC_PUBLIC, CLEntityRendererInterface.getterName2, "()L" + new3DDesc + ";", null, null);
            getter1.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            getter1.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, CLEntityRendererInterface.fieldName2, "L"+oldLightmapDesc+";"));
            getter1.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, new3DDesc));
            getter1.instructions.add(new InsnNode(Opcodes.ARETURN));
            classNode.methods.add(getter1);
            
            MethodNode getter2 = new MethodNode(Opcodes.ACC_PUBLIC, CLEntityRendererInterface.getterName3, "()L" + new2DDesc + ";", null, null);
            getter2.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            getter2.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, CLEntityRendererInterface.fieldName3, "L"+oldLightmapDesc+";"));
            getter2.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, new2DDesc));
            getter2.instructions.add(new InsnNode(Opcodes.ARETURN));
            classNode.methods.add(getter2);
            
            MethodNode setter = new MethodNode(Opcodes.ACC_PUBLIC, CLEntityRendererInterface.setterName, "([I)V", null, null);
            setter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            if (ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT)
                setter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, CLEntityRendererInterface.fieldName, "L"+oldLightmapDesc+";"));
            else
                setter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, CLEntityRendererInterface.obfFieldName, "L"+oldLightmapDesc+";"));
            setter.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, new3DDesc));
            setter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
            if(ColoredLightsCoreLoadingPlugin.MCP_ENVIRONMENT){
            	setter.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, new3DDesc, "dynamicTextureData", "[I"));
            }else{
            	setter.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, new3DDesc, "field_110566_b", "[I"));
            }
            setter.instructions.add(new InsnNode(Opcodes.RETURN));
            classNode.methods.add(setter);
            
            addSetterAndInterface = true;
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
                    
                    FMLLog.info("Replacing 2D lighmap texture");
                    ((TypeInsnNode) insn).desc = new3DDesc;
                    insn = it.next(); //DUP
                    insn = it.next(); //BIPUSH 16
                    insn = it.next(); //BIPUSH 16
                    it.add(new IntInsnNode(Opcodes.BIPUSH, 16));
                    FMLLog.info("Setting Entityrenderer.lightmapTexture to a " + new3DDesc);
                    insn = it.next(); //Constructor call to the CLDynamicTexture3D - INVOKESPECIAL
                    ((MethodInsnNode) insn).owner = new3DDesc;
                    ((MethodInsnNode) insn).name = "<init>";
                    ((MethodInsnNode) insn).desc = "(III)V";
                    insn = it.next(); //Storing the value to the local field - PUTFIELD
                    
                    found2DLightmap = true;
                }
            }

            /* This is a bit lazy, but the next line is 10 instructions long and 'needs' to be removed */
            if (!removeTextureLocation && found2DLightmap) {
                
                FMLLog.info("Removing locationLightMap");
                for (int i = 0; i < 10; i++) {
                    insn = it.next();
                    it.remove();
                }
                
                removeTextureLocation = true;
            }

            if (!add3DLightmap && removeTextureLocation && found2DLightmap) {
                FMLLog.info("Adding Second Colormap");
                
                it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                it.add(new TypeInsnNode(Opcodes.NEW, new3DDesc));
                it.add(new InsnNode(Opcodes.DUP));
                it.add(new IntInsnNode(Opcodes.BIPUSH, 16));
                it.add(new IntInsnNode(Opcodes.BIPUSH, 16));
                it.add(new IntInsnNode(Opcodes.BIPUSH, 16));
                it.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, new3DDesc, "<init>", "(III)V"));
                it.add(new FieldInsnNode(Opcodes.PUTFIELD, entityRenderClass, CLEntityRendererInterface.fieldName2, "L"+oldLightmapDesc+";"));
                
                FMLLog.info("Adding Third Empty Texture");
                it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                it.add(new TypeInsnNode(Opcodes.NEW, new2DDesc));
                it.add(new InsnNode(Opcodes.DUP));
                it.add(new IntInsnNode(Opcodes.BIPUSH, 16));
                it.add(new IntInsnNode(Opcodes.BIPUSH, 16));
                it.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, new2DDesc, "<init>", "(II)V"));
                it.add(new FieldInsnNode(Opcodes.PUTFIELD, entityRenderClass, CLEntityRendererInterface.fieldName3, "L"+oldLightmapDesc+";"));
                
                add3DLightmap = true;
            }
        }
        return (found2DLightmap && removeTextureLocation && add3DLightmap); // && fixGetTextureData
    }
    
}
