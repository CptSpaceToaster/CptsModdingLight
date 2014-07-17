package coloredlightscore.src.asm.transformer;

import java.util.ListIterator;

import net.minecraft.client.renderer.texture.DynamicTexture;

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

import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import coloredlightscore.src.types.CLEntityRendererInterface;

import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

import cpw.mods.fml.common.FMLLog;

public class TransformEntityRenderer extends HelperMethodTransformer {

    // These methods will be replaced by statics in CLRenderBlocksHelper
    String methodsToReplace[] = { "updateLightmap (F)V", "enableLightmap (D)V", "disableLightmap (D)V" };

    String constructorSignature = "<init> (Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V";
    String oldLightmapDesc = "net/minecraft/client/renderer/texture/DynamicTexture";
    String new3DDesc = "coloredlightscore/src/types/CLDynamicTexture3D";
    String new1DDesc = "coloredlightscore/src/types/CLDynamicTexture1D";
    
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
        /*   THIS IS BAD, DON'T DO THIS
        for (Iterator<FieldNode> it = classNode.fields.iterator(); it.hasNext();) {
        	FieldNode insn = it.next();
        	if (insn.desc.equals("L" + oldLightmapDesc + ";")) {
        		FMLLog.info("Replaced lightmapTexture field type");
        		insn.desc = "L" + newLightmapDesc + ";";
        	}
        }
        */
        
        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
                return true;
        }

        if ((methodNode.name + " " + methodNode.desc).equals(constructorSignature))
            return true;

        return false;
    }

    @Override
    public boolean preTransformClass(ClassNode classNode)
    {
        if(!addSetterAndInterface) {
            //implements CLEntityRendererInterface
            classNode.interfaces.add(CLEntityRendererInterface.appliedInterface);
            
            //public final DynamicTexture lightmapTexture;
            classNode.fields.add( new FieldNode(Opcodes.ACC_PUBLIC, CLEntityRendererInterface.fieldName, "L"+oldLightmapDesc+";", null, null));

            /* Just cramming a setter, don't you mind */
            MethodNode setter = new MethodNode(org.objectweb.asm.Opcodes.ACC_PUBLIC, CLEntityRendererInterface.setterName, "([I)V", null, null);
            setter.instructions.add(new VarInsnNode(org.objectweb.asm.Opcodes.ALOAD, 0));
            setter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", CLEntityRendererInterface.fieldName, "L"+oldLightmapDesc+";"));
            setter.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, new1DDesc));
            setter.instructions.add(new VarInsnNode(org.objectweb.asm.Opcodes.ALOAD, 1));
            setter.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, new1DDesc, "dynamicTextureData", "[I"));
            setter.instructions.add(new InsnNode(org.objectweb.asm.Opcodes.RETURN));
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

        if ((methodNode.name + " " + methodNode.desc).equals(constructorSignature)) {
            return transformConstructor(methodNode);
        }
        

        return false;
    }
    DynamicTexture testtt;
    protected boolean transformConstructor(MethodNode methodNode) {
        //Actions
        boolean replace2DLightmap = false;
        boolean add1DLightmap = false;
        boolean removeTextureLocation = false;
        boolean fixGetTextureData = true;

        for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext();) {
            AbstractInsnNode insn = it.next();

            if (insn.getOpcode() == Opcodes.NEW && !replace2DLightmap) {
                if (((TypeInsnNode) insn).desc.equals(oldLightmapDesc)) {
                    FMLLog.info("Replacing 2D lighmap texture");
                    ((TypeInsnNode) insn).desc = new3DDesc;
                    FMLLog.info("Fixing Arguments on stack - CLDynamicTexture3D(16, 16, 16)");
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
                    /* MAY NOT NEED THIS
                    ((FieldInsnNode)insn).desc = "L" + newLightmapDesc + ";";
                    */
                    replace2DLightmap = true;
                }
            }

            /* This is a bit lazy, but the next line is 10 instructions long and 'needs' to be removed */
            if (!removeTextureLocation && replace2DLightmap) {
                FMLLog.info("Removing locationLightMap");
                for (int i = 0; i < 10; i++) {
                    insn = it.next();
                    it.remove();
                }
                removeTextureLocation = true;
            }

            /* Still lazy here, after deleting the last line, the next instruction will be the next available GETFIELD instruction */
            /* MAY NOT NEED THIS
            if (insn.getOpcode() == Opcodes.GETFIELD && !fixGetTextureData && removeTextureLocation && replace2DLightmap) {
            	FMLLog.info("Getting texture data from CLDynamicTexture3D instead");
            	((FieldInsnNode)insn).desc = "L" + newLightmapDesc + ";";
            	insn = it.next();
            	((MethodInsnNode)insn).owner = newLightmapDesc;
            	fixGetTextureData = true;
            }
            */

            if (!add1DLightmap && removeTextureLocation && replace2DLightmap) {
                FMLLog.info("Added Second Lightmap");
                it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                it.add(new TypeInsnNode(Opcodes.NEW, new1DDesc));
                it.add(new InsnNode(Opcodes.DUP));
                it.add(new IntInsnNode(Opcodes.BIPUSH, 16));
                it.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, new1DDesc, "<init>", "(I)V"));
                it.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/EntityRenderer", CLEntityRendererInterface.fieldName, "L"+oldLightmapDesc+";"));
                
                add1DLightmap = true;
            }
        }
        return (replace2DLightmap && removeTextureLocation && fixGetTextureData && add1DLightmap); // && fixGetTextureData
    }
    
}
