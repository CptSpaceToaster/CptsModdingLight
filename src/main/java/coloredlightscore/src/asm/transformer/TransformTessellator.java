package coloredlightscore.src.asm.transformer;

import java.util.ListIterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import coloredlightscore.src.asm.transformer.core.ASMUtils;
import coloredlightscore.src.asm.transformer.core.ExtendedClassWriter;
import coloredlightscore.src.asm.transformer.core.HelperMethodTransformer;
import coloredlightscore.src.asm.transformer.core.NameMapper;
import coloredlightscore.src.types.CLTessellatorInterface;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.FMLLog;

public class TransformTessellator extends HelperMethodTransformer {
    String unObfBrightness = "hasBrightness";
    String obfBrightness = "field_78414_p"; //It could also be field_147580_e (trianglecube36: I checked this it is field_78414_p)
    String unObfLightmapTexUnit = "lightmapTexUnit";
    String obfLightmapTexUnit = "field_77476_b";
    String unObfDefaultTexUnit = "defaultTexUnit";
    String obfDefaultTexUnit = "field_77478_a";

    // These methods will be replaced by statics in CLTessellatorHelper
    String methodsToReplace[] = { "addVertex (DDD)V" };
    

    public TransformTessellator() {
        super("net/minecraft/client/renderer/Tessellator");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes != null && transforms(transformedName)) {
            FMLLog.info("Class %s is a candidate for transforming", transformedName);

            try {
                ClassNode clazz = ASMUtils.getClassNode(bytes);

                if (transform(clazz, transformedName)) {
                    FMLLog.info("Finished Transforming class " + transformedName);
                    ClassWriter writer = new ExtendedClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                    clazz.accept(writer);
                    bytes = writer.toByteArray();
                } else
                    FMLLog.warning("Did not transform %s", transformedName);
            } catch (Exception e) {
                FMLLog.severe("Exception during transformation of class " + transformedName);
                e.printStackTrace();
                Throwables.propagate(e);
            }
        }
        return bytes;
    }
    
    @Override
    public boolean preTransformClass(ClassNode classNode)
    {
        classNode.interfaces.add(CLTessellatorInterface.appliedInterface);

        //Don't mind this.  Just cramming a getter and setter into the Tesellator for later use
        //getter
        MethodNode getter = new MethodNode(Opcodes.ACC_PUBLIC, CLTessellatorInterface.getterName, "()" + CLTessellatorInterface.fieldDescriptor, null, null);
        getter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        getter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, CLTessellatorInterface.fieldName, CLTessellatorInterface.fieldDescriptor));
        getter.instructions.add(new InsnNode(Opcodes.IRETURN));
        classNode.methods.add(getter);
        //setter
        MethodNode setter = new MethodNode(Opcodes.ACC_PUBLIC, CLTessellatorInterface.setterName, "(" + CLTessellatorInterface.fieldDescriptor + ")V", null, null);
        setter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        setter.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        setter.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, classNode.name, CLTessellatorInterface.fieldName, CLTessellatorInterface.fieldDescriptor));
        setter.instructions.add(new InsnNode(Opcodes.RETURN));
        classNode.methods.add(setter);
        
        return true;
    }

    @Override
    protected Class<?> getHelperClass() {
        return coloredlightscore.src.helper.CLTessellatorHelper.class;
    }

    @Override
    protected boolean transforms(ClassNode classNode, MethodNode methodNode) {

        for (String name : methodsToReplace) {
            //System.out.println(" : " + (methodNode.name + " " + methodNode.desc));
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name))
                return true;
        }

        if ((methodNode.name + " " + methodNode.desc).equals(NameMapper.drawSignature) || (methodNode.name + " " + methodNode.desc).equals(NameMapper.obfDrawSignature))
            return true;

        if ((methodNode.name + " " + methodNode.desc).equals(NameMapper.getVertexStateSignature) || (methodNode.name + " " + methodNode.desc).equals(NameMapper.obfGetVertexStateSignature))
            return true;

        return false;
    }

    @Override
    protected boolean transform(ClassNode classNode, MethodNode methodNode) {

        for (String name : methodsToReplace) {
            if (NameMapper.getInstance().isMethod(methodNode, super.className, name)) {
                return redefineMethod(classNode, methodNode, name);
            }
        }

        if ((methodNode.name + " " + methodNode.desc).equals(NameMapper.drawSignature)){
        	return transformDraw(methodNode);
        }else if((methodNode.name + " " + methodNode.desc).equals(NameMapper.obfDrawSignature)){
            return transformSrgDraw(methodNode);
        }

        if ((methodNode.name + " " + methodNode.desc).equals(NameMapper.getVertexStateSignature)){
        	return transformGetVertexState(methodNode);
        }else if((methodNode.name + " " + methodNode.desc).equals(NameMapper.obfGetVertexStateSignature)){
            return transformSrgGetVertexState(methodNode);
    	}

        return false;
    }

    /*
     * This does stuff...
     */
    protected boolean transformDraw(MethodNode methodNode) {
        boolean fixedFive = false;
        boolean replacedShift = false;
        boolean hasFoundBrightness = false;
        boolean findInstanceOfTwo = false;
        boolean addedTexture2 = false;
        boolean hasFoundBrightnessAgain = false;
        boolean addedDisable2 = false;

        int replace32 = 0;
        int replace8 = 0;
        int newStride = 40;
        int newInts = 10;

        for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext();) {
            AbstractInsnNode insn = it.next();

            //replace the only instance of '>> 5' with '/ 40'
            if (!fixedFive && insn.getOpcode() == Opcodes.ICONST_5) {
                FMLLog.info("Replaced a 5 with 40");
                it.set(new IntInsnNode(Opcodes.BIPUSH, 40));
                fixedFive = true;
            }
            if (!replacedShift && fixedFive && insn.getOpcode() == Opcodes.ISHR) {
                FMLLog.info("Dividing instead of shifting");
                it.set(new InsnNode(Opcodes.IDIV));
                replacedShift = true;
            }

            //mark that we we've found the brightness descriptions
            if (!hasFoundBrightness && replacedShift && fixedFive && insn.getOpcode() == Opcodes.GETFIELD && insn instanceof FieldInsnNode) {
                if (((FieldInsnNode) insn).name.equals(obfBrightness) || ((FieldInsnNode) insn).name.equals(unObfBrightness)) {
                    hasFoundBrightness = true;
                    it.next();  //Move so we don't find it again
                }
            }
            //mark when we find the instance of 2
            if (hasFoundBrightness && insn.getOpcode() == Opcodes.ICONST_2) {
                FMLLog.info("Found that 2");
                it.set(new InsnNode(Opcodes.ICONST_3));
                findInstanceOfTwo = true;
            }
            if (!addedTexture2 && hasFoundBrightness && insn.getOpcode() == Opcodes.INVOKESTATIC) {
                if (((MethodInsnNode)insn).name.equals("glTexCoordPointer")) {
                    it.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_COORD_ARRAY)));
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/GL11", "glEnableClientState", "(I)V"));
                    
                    //OpenGlHelper.setClientActiveTexture(GL13.GL_TEXTURE2); 
                    it.add(new LdcInsnNode(GL13.GL_TEXTURE2));
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/OpenGlHelper", "setClientActiveTexture", "(I)V"));
                    
                    //this.shortBuffer.position(15);
                    //TODO: Figure out how to bump this pointer over 8 bits, but still have it reference the short buffer...
                    it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    it.add(new InsnNode(Opcodes.POP));
                    it.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/Tessellator", "shortBuffer", "Ljava/nio/ShortBuffer;"));
                    it.add(new IntInsnNode(Opcodes.BIPUSH, 16)); //should really be 15.5
                    it.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/nio/ShortBuffer", "position", "(I)Ljava/nio/Buffer;"));
                    //it.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/Tessellator", "byteBuffer", "Ljava/nio/ByteBuffer;"));
                    //it.add(new IntInsnNode(Opcodes.BIPUSH, 31));
                    //it.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/nio/ByteBuffer", "position", "(I)Ljava/nio/Buffer;"));
                    it.add(new InsnNode(Opcodes.POP));
                    
                    //GL11.glTexCoordPointer(3, 40, this.shortBuffer);
                    it.add(new InsnNode(Opcodes.ICONST_3));
                    it.add(new IntInsnNode(Opcodes.BIPUSH, newStride));
                    it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    it.add(new InsnNode(Opcodes.POP));
                    it.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/Tessellator", "shortBuffer", "Ljava/nio/ShortBuffer;"));
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/GL11", "glTexCoordPointer", "(IILjava/nio/ShortBuffer;)V"));
                    addedTexture2 = true;
                }
            }
            if (!hasFoundBrightnessAgain && addedTexture2 && insn.getOpcode() == Opcodes.GETFIELD && insn instanceof FieldInsnNode) {
                if (((FieldInsnNode) insn).name.equals(obfBrightness) || ((FieldInsnNode) insn).name.equals(unObfBrightness)) {
                    hasFoundBrightnessAgain = true;
                }
            }
            
            if (!addedDisable2 && hasFoundBrightnessAgain && insn.getOpcode() == Opcodes.INVOKESTATIC) {
                if (((MethodInsnNode)insn).name.equals("glDisableClientState")) {
                    //OpenGlHelper.setClientActiveTexture(GL13.GL_TEXTURE2);
                    it.add(new LdcInsnNode(GL13.GL_TEXTURE2));
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/OpenGlHelper", "setClientActiveTexture", "(I)V"));
                    //GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    it.add(new LdcInsnNode(GL11.GL_TEXTURE_COORD_ARRAY));
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/GL11", "glDisableClientState", "(I)V"));
                    addedDisable2 = true;
                }
            }
            //replace the short(14) with short(17)
            //replace all instances of 32 with 40 and replace all instances of 8 with 10
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                if (((IntInsnNode) insn).operand == 32) {
                    ((IntInsnNode) insn).operand = newStride;
                    replace32++;
                }
                if (((IntInsnNode) insn).operand == 8) {
                    ((IntInsnNode) insn).operand = newInts;
                    replace8++;
                }
            }
        }

        if (!hasFoundBrightness) {
            FMLLog.severe("Could not find " + unObfBrightness + " or " + obfBrightness + " while transforming Tessellator.draw!");
        } else if (!findInstanceOfTwo) {
            FMLLog.severe("Reached the end of the list without finding a 2 to replace while transforming Tessellator.draw!");
        }
        FMLLog.info("Replaced " + replace32 + " instances of 32 with " + newStride + ".");
        FMLLog.info("Replaced " + replace8 + " instances of 8 with " + newInts + ".");

        return (fixedFive && replacedShift && hasFoundBrightness && findInstanceOfTwo && addedTexture2 && addedDisable2);
    }

    /*
     * This does some more stuff...
     */
    boolean transformGetVertexState(MethodNode methodNode) {
        for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext();) {
            AbstractInsnNode insn = it.next();
            //replace the only instance of 32 with 40
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                if (((IntInsnNode) insn).operand == 32) {
                    ((IntInsnNode) insn).operand = 40;
                    return true;
                }
            }
        }
        FMLLog.severe("Could not find a 32 to transform to a 40");
        return false;
    }
    
    /* QUICK FIX PAST THIS COMMENT...
     * (look at EPIC IDEA in NameMapper form more info)
     * */
    
    protected boolean transformSrgDraw(MethodNode methodNode) {
        boolean fixedFive = false;
        boolean replacedShift = false;
        boolean hasFoundBrightness = false;
        boolean findInstanceOfTwo = false;
        boolean addedTexture2 = false;
        boolean hasFoundBrightnessAgain = false;
        boolean addedDisable2 = false;

        int replace32 = 0;
        int replace8 = 0;
        int newStride = 40;
        int newInts = 10;

        for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext();) {
            AbstractInsnNode insn = it.next();

            //replace the only instance of '>> 5' with '/ 40'
            if (!fixedFive && insn.getOpcode() == Opcodes.ICONST_5) {
                FMLLog.info("Replaced a 5 with 40");
                it.set(new IntInsnNode(Opcodes.BIPUSH, 40));
                fixedFive = true;
            }
            if (!replacedShift && fixedFive && insn.getOpcode() == Opcodes.ISHR) {
                FMLLog.info("Dividing instead of shifting");
                it.set(new InsnNode(Opcodes.IDIV));
                replacedShift = true;
            }

            //mark that we we've found the brightness descriptions
            if (!hasFoundBrightness && replacedShift && fixedFive && insn.getOpcode() == Opcodes.GETFIELD && insn instanceof FieldInsnNode) {
                if (((FieldInsnNode) insn).name.equals(obfBrightness) || ((FieldInsnNode) insn).name.equals(unObfBrightness)) {
                    hasFoundBrightness = true;
                    it.next();  //Move so we don't find it again
                }
            }
            //mark when we find the instance of 2
            if (hasFoundBrightness && insn.getOpcode() == Opcodes.ICONST_2) {
                FMLLog.info("Found that 2");
                it.set(new InsnNode(Opcodes.ICONST_3));
                findInstanceOfTwo = true;
            }
            if (!addedTexture2 && hasFoundBrightness && insn.getOpcode() == Opcodes.INVOKESTATIC) {
                if (((MethodInsnNode)insn).name.equals("glTexCoordPointer")) {
                    it.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_COORD_ARRAY)));
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/GL11", "glEnableClientState", "(I)V"));
                    
                    //OpenGlHelper.setClientActiveTexture(GL13.GL_TEXTURE2); 
                    it.add(new LdcInsnNode(GL13.GL_TEXTURE2));
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/OpenGlHelper", "func_77472_b", "(I)V")); //was setClientActiveTexture
                    
                    //this.shortBuffer.position(15);
                    //TODO: Figure out how to bump this pointer over 8 bits, but still have it reference the short buffer...
                    it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    it.add(new InsnNode(Opcodes.POP));
                    it.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/Tessellator", "field_147567_e", "Ljava/nio/ShortBuffer;")); //was shortBuffer
                    it.add(new IntInsnNode(Opcodes.BIPUSH, 16)); //should really be 15.5
                    it.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/nio/ShortBuffer", "position", "(I)Ljava/nio/Buffer;"));
                    //it.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/Tessellator", "byteBuffer", "Ljava/nio/ByteBuffer;"));
                    //it.add(new IntInsnNode(Opcodes.BIPUSH, 31));
                    //it.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/nio/ByteBuffer", "position", "(I)Ljava/nio/Buffer;"));
                    it.add(new InsnNode(Opcodes.POP));
                    
                    //GL11.glTexCoordPointer(3, 40, this.shortBuffer);
                    it.add(new InsnNode(Opcodes.ICONST_3));
                    it.add(new IntInsnNode(Opcodes.BIPUSH, newStride));
                    it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    it.add(new InsnNode(Opcodes.POP));
                    it.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/Tessellator", "field_147567_e", "Ljava/nio/ShortBuffer;")); //was shortBuffer
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/GL11", "glTexCoordPointer", "(IILjava/nio/ShortBuffer;)V"));
                    addedTexture2 = true;
                }
            }
            if (!hasFoundBrightnessAgain && addedTexture2 && insn.getOpcode() == Opcodes.GETFIELD && insn instanceof FieldInsnNode) {
                if (((FieldInsnNode) insn).name.equals(obfBrightness) || ((FieldInsnNode) insn).name.equals(unObfBrightness)) {
                    hasFoundBrightnessAgain = true;
                }
            }
            
            if (!addedDisable2 && hasFoundBrightnessAgain && insn.getOpcode() == Opcodes.INVOKESTATIC) {
                if (((MethodInsnNode)insn).name.equals("glDisableClientState")) {
                    //OpenGlHelper.setClientActiveTexture(GL13.GL_TEXTURE2);
                    it.add(new LdcInsnNode(GL13.GL_TEXTURE2));
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/OpenGlHelper", "func_77472_b", "(I)V")); //was setClientActiveTexture
                    //GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    it.add(new LdcInsnNode(GL11.GL_TEXTURE_COORD_ARRAY));
                    it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/GL11", "glDisableClientState", "(I)V"));
                    addedDisable2 = true;
                }
            }
            //replace the short(14) with short(17)
            //replace all instances of 32 with 40 and replace all instances of 8 with 10
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                if (((IntInsnNode) insn).operand == 32) {
                    ((IntInsnNode) insn).operand = newStride;
                    replace32++;
                }
                if (((IntInsnNode) insn).operand == 8) {
                    ((IntInsnNode) insn).operand = newInts;
                    replace8++;
                }
            }
        }

        if (!hasFoundBrightness) {
            FMLLog.severe("Could not find " + unObfBrightness + " or " + obfBrightness + " while transforming Tessellator.draw!");
        } else if (!findInstanceOfTwo) {
            FMLLog.severe("Reached the end of the list without finding a 2 to replace while transforming Tessellator.draw!");
        }
        FMLLog.info("Replaced " + replace32 + " instances of 32 with " + newStride + ".");
        FMLLog.info("Replaced " + replace8 + " instances of 8 with " + newInts + ".");

        return (fixedFive && replacedShift && hasFoundBrightness && findInstanceOfTwo && addedTexture2 && addedDisable2);
    }

    /*
     * This does some more stuff...
     */
    boolean transformSrgGetVertexState(MethodNode methodNode) {
        for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext();) {
            AbstractInsnNode insn = it.next();
            //replace the only instance of 32 with 40
            if (insn.getOpcode() == Opcodes.BIPUSH) {
                if (((IntInsnNode) insn).operand == 32) {
                    ((IntInsnNode) insn).operand = 40;
                    return true;
                }
            }
        }
        FMLLog.severe("Could not find a 32 to transform to a 40");
        return false;
    }
}
