package coloredlightscore.src.asm.transformer.core;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import java.util.HashMap;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

//TODO: trianglecube36: less string operations, more support for more types
//EPIC IDEA: 1. create a dummy function that calls EVERYTHING that we need to override/modify with asm
//           2. let mcp do all the work of remapping (no need to look up mappings every MC update :D)
//           3. use asm to read the runtime mappings form that function!
//           WILL WORK IN FORGE DEV ENVIRONMENT AND NORMAL MINECRAFT FORGE ENVIRONMENT :D
public class NameMapper {

    private HashMap<String, SeargeData> m_NameList;
    private static NameMapper INSTANCE = null;
    private boolean MCP_ENVIRONMENT = false;

    public static NameMapper getInstance() {
        if (INSTANCE == null){
            INSTANCE = new NameMapper();
        }

        return INSTANCE;
    }
    
    /* Used when directly transforming some values */
    public static String drawSignature = "draw ()I";
    public static String obfDrawSignature = "func_78381_a ()I";
    public static String getVertexStateSignature = "getVertexState (FFF)Lnet/minecraft/client/shader/TesselatorVertexState;";
    public static String obfGetVertexStateSignature = "func_147564_a (FFF)Lnet/minecraft/client/shader/TesselatorVertexState;";

    public NameMapper() {
        m_NameList = new HashMap<String, SeargeData>();

        // ***********************************************
        // CLASSES (CL) AND METHODS (MD) SHOULD COME FROM MCP-NOTCH.SRG
        // FIELDS (FD) SHOULD COME FROM MCP-SRG.SRG

        // Block
        registerSrgName("CL: net/minecraft/block/Block net/minecraft/block/Block");
        registerSrgName("MD: net/minecraft/block/Block/setLightLevel (F)Lnet/minecraft/block/Block; net/minecraft/block/Block/func_149715_a (F)Lnet/minecraft/block/Block;");

        // Chunk
        registerSrgName("CL: net/minecraft/world/chunk/Chunk net/minecraft/world/chunk/Chunk");
        registerSrgName("MD: net/minecraft/world/chunk/Chunk/getBlockLightValue (IIII)I net/minecraft/world/chunk/Chunk/func_76629_c (IIII)I");

        // ChunkCache
        registerSrgName("CL: net/minecraft/world/ChunkCache net/minecraft/world/ChunkCache");
        registerSrgName("MD: net/minecraft/world/ChunkCache/getLightBrightnessForSkyBlocks (IIII)I net/minecraft/world/ChunkCache/func_72802_i (IIII)I");

        // EntityPlayerMP
        registerSrgName("CL: net/minecraft/entity/player/EntityPlayerMP net/minecraft/entity/player/EntityPlayerMP");
        registerSrgName("MD: net/minecraft/entity/player/EntityPlayerMP/onUpdate ()V net/minecraft/entity/player/EntityPlayerMP/func_70071_h_ ()V");

        // EntityRenderer
        registerSrgName("CL: net/minecraft/client/renderer/EntityRenderer net/minecraft/client/renderer/EntityRenderer");
        registerSrgName("MD: net/minecraft/client/renderer/EntityRenderer/updateLightmap (F)V net/minecraft/client/renderer/EntityRenderer/func_78472_g (F)V");
        registerSrgName("MD: net/minecraft/client/renderer/EntityRenderer/disableLightmap (D)V net/minecraft/client/renderer/EntityRenderer/func_78483_a (D)V");
        registerSrgName("MD: net/minecraft/client/renderer/EntityRenderer/enableLightmap (D)V net/minecraft/client/renderer/EntityRenderer/func_78463_b (D)V");

        // ExtendedBlockStorage
        registerSrgName("CL: net/minecraft/world/chunk/storage/ExtendedBlockStorage net/minecraft/world/chunk/storage/ExtendedBlockStorage");
        registerSrgName("MD: net/minecraft/world/chunk/storage/ExtendedBlockStorage/setExtBlocklightValue (IIII)V net/minecraft/world/chunk/storage/ExtendedBlockStorage/func_76677_d (IIII)V");
        registerSrgName("MD: net/minecraft/world/chunk/storage/ExtendedBlockStorage/getExtBlocklightValue (III)I net/minecraft/world/chunk/storage/ExtendedBlockStorage/func_76674_d (III)I");
        registerSrgName("FD: net/minecraft/world/chunk/storage/ExtendedBlockStorage/blockLSBArray net/minecraft/world/chunk/storage/ExtendedBlockStorage/field_76680_d");

        // FontRenderer
        registerSrgName("CL: net/minecraft/client/gui/FontRenderer net/minecraft/client/gui/FontRenderer");
        registerSrgName("MD: net/minecraft/client/gui/FontRenderer/renderDefaultChar (IZ)F net/minecraft/client/gui/FontRenderer/func_78266_a (IZ)F");
        registerSrgName("MD: net/minecraft/client/gui/FontRenderer/renderUnicodeChar (CZ)F net/minecraft/client/gui/FontRenderer/func_78277_a (CZ)F");

        // OpenGlHelper
        registerSrgName("CL: net/minecraft/client/renderer/OpenGlHelper net/minecraft/client/renderer/OpenGlHelper");
        registerSrgName("MD: net/minecraft/client/renderer/OpenGlHelper/setLightmapTextureCoords (IFF)V net/minecraft/client/renderer/OpenGlHelper/func_77475_a (IFF)V");

        // PlayerInstance
        registerSrgName("CL: net/minecraft/server/management/PlayerManager$PlayerInstance net/minecraft/server/management/PlayerManager$PlayerInstance");
        registerSrgName("MD: net/minecraft/server/management/PlayerManager$PlayerInstance/sendToAllPlayersWatchingChunk (Lnet/minecraft/network/Packet;)V net/minecraft/server/management/PlayerManager$PlayerInstance/func_151251_a (Lnet/minecraft/network/Packet;)V");
        registerSrgName("FD: net/minecraft/server/management/PlayerManager$PlayerInstance/chunkLocation net/minecraft/server/management/PlayerManager$PlayerInstance/field_73264_c");

        registerSrgName("CL: net/minecraft/client/renderer/entity/Render net/minecraft/client/renderer/entity/Render");
        registerSrgName("MD: net/minecraft/client/renderer/entity/Render/renderEntityOnFire (Lnet/minecraft/entity/Entity;DDDF)V net/minecraft/client/renderer/entity/Render/func_76977_a (Lnet/minecraft/entity/Entity;DDDF)V");

        // RenderBlocks
        registerSrgName("CL: net/minecraft/client/renderer/RenderBlocks net/minecraft/client/renderer/RenderBlocks");
        registerSrgName("MD: net/minecraft/client/renderer/RenderBlocks/renderStandardBlockWithAmbientOcclusion (Lnet/minecraft/block/Block;IIIFFF)Z net/minecraft/client/renderer/RenderBlocks/func_147751_a (Lnet/minecraft/block/Block;IIIFFF)Z");
        registerSrgName("MD: net/minecraft/client/renderer/RenderBlocks/renderStandardBlockWithAmbientOcclusionPartial (Lnet/minecraft/block/Block;IIIFFF)Z net/minecraft/client/renderer/RenderBlocks/func_147808_b (Lnet/minecraft/block/Block;IIIFFF)Z");
        registerSrgName("MD: net/minecraft/client/renderer/RenderBlocks/renderStandardBlockWithColorMultiplier (Lnet/minecraft/block/Block;IIIFFF)Z net/minecraft/client/renderer/RenderBlocks/func_147736_d (Lnet/minecraft/block/Block;IIIFFF)Z");
        registerSrgName("MD: net/minecraft/client/renderer/RenderBlocks/renderBlockLiquid (Lnet/minecraft/block/Block;III)Z net/minecraft/client/renderer/RenderBlocks/func_147721_p (Lnet/minecraft/block/Block;III)Z");
        registerSrgName("MD: net/minecraft/client/renderer/RenderBlocks/getAoBrightness (IIII)I net/minecraft/client/renderer/RenderBlocks/func_147778_a (IIII)I");

        // RendererLivingEntity
        registerSrgName("CL: net/minecraft/client/renderer/entity/RendererLivingEntity net/minecraft/client/renderer/entity/RendererLivingEntity");
        registerSrgName("MD: net/minecraft/client/renderer/entity/RendererLivingEntity/doRender (Lnet/minecraft/entity/EntityLivingBase;DDDFF)V net/minecraft/client/renderer/entity/RendererLivingEntity/func_76986_a (Lnet/minecraft/entity/EntityLivingBase;DDDFF)V");

        // Tessellator
        registerSrgName("CL: net/minecraft/client/renderer/Tessellator net/minecraft/client/renderer/Tessellator");
        registerSrgName("MD: net/minecraft/client/renderer/Tessellator/setBrightness (I)V net/minecraft/client/renderer/Tessellator/func_78380_c (I)V");
        registerSrgName("MD: net/minecraft/client/renderer/Tessellator/addVertex (DDD)V net/minecraft/client/renderer/Tessellator/func_78377_a (DDD)V");
        registerSrgName("MD: net/minecraft/client/renderer/Tessellator/draw ()I net/minecraft/client/renderer/Tessellator/func_78381_a ()I");
        
        // World
        registerSrgName("CL: net/minecraft/world/World net/minecraft/world/World");
        registerSrgName("MD: net/minecraft/world/World/getBlockLightValue_do (IIIZ)I net/minecraft/world/World/func_72849_a (IIIZ)I");
        registerSrgName("MD: net/minecraft/world/World/getLightBrightnessForSkyBlocks (IIII)I net/minecraft/world/World/func_72802_i (IIII)I");
        registerSrgName("MD: net/minecraft/world/World/getLightBrightness (III)F net/minecraft/world/World/func_72801_o (III)F");
        registerSrgName("MD: net/minecraft/world/World/computeLightValue (IIILnet/minecraft/world/EnumSkyBlock;)I net/minecraft/world/World/func_98179_a (IIILnet/minecraft/world/EnumSkyBlock;)I");
        registerSrgName("MD: net/minecraft/world/World/updateLightByType (Lnet/minecraft/world/EnumSkyBlock;III)Z net/minecraft/world/World/func_147463_c (Lnet/minecraft/world/EnumSkyBlock;III)Z");

        // NetHandlerPlayServer
        registerSrgName("CL: net/minecraft/network/NetHandlerPlayServer net/minecraft/network/NetHandlerPlayServer");
        registerSrgName("MD: net/minecraft/network/NetHandlerPlayServer/sendPacket (Lnet/minecraft/network/Packet;)V net/minecraft/network/NetHandlerPlayServer/func_147359_a (Lnet/minecraft/network/Packet;)V");

        // NibbleArray
        registerSrgName("CL: net/minecraft/world/chunk/NibbleArray net/minecraft/world/chunk/NibbleArray");
        registerSrgName("MD: net/minecraft/world/chunk/NibbleArray/get (III)I net/minecraft/world/chunk/NibbleArray/func_76582_a (III)I");
        registerSrgName("MD: net/minecraft/world/chunk/NibbleArray/set (IIII)V net/minecraft/world/chunk/NibbleArray/func_76581_a (IIII)V");


        // S26PacketMapChunkBulk
        registerSrgName("CL: net/minecraft/network/play/server/S26PacketMapChunkBulk net/minecraft/network/play/server/S26PacketMapChunkBulk");

        // ***********************************************

        //This should be set in the plugin, where we have this information!
        //MCP_ENVIRONMENT = (this.getClass().getClassLoader().getResource("net/minecraft/world/World.class") != null);

        //CLLog.info("ColoredLightsCore: MCP_ENVIRONMENT={}", MCP_ENVIRONMENT);
    }

    public boolean isObfuscated() {
        return !MCP_ENVIRONMENT;
    }

    public void setObfuscated(boolean obfuscated) {
        MCP_ENVIRONMENT = !obfuscated;
        CLLog.debug("ColoredLightsCore: MCP_ENVIRONMENT={}", MCP_ENVIRONMENT);
    }

    /**
     * Returns the appropriate obfuscated or unobfuscated class name for a Minecraft class
     * 
     * @param className De-obfuscated class name, in either dot or internal format.
     * @return
     */
    public String getClassName(String className) {
        String indexer = internalizeName(className);
        SeargeData srg = m_NameList.get(indexer);

        return (srg == null ? className : (MCP_ENVIRONMENT ? srg.objectName : srg.objectNameObfuscated));
    }

    /**
     * Given a method name in path/to/class/methodname or path.to.class.methodname format
     * Returns proper method name in path/to/class/methodname format
     * 
     * @param ownerName The de-obfuscated class that contains the method (dot or internal format OK)
     * @param methodName The de-obfuscated name of the method
     * @param methodSignature The de-obfuscated signature of the method
     * @return The obfuscated/unobfuscated name based on MCP_ENVIRONMENT
     */
    public String getMethodName(String ownerName, String methodName, String methodSignature) {
        // Methods are indexed as "path/to/class/methodName signature"
        String indexer = internalizeName(ownerName + "." + methodName + " " + methodSignature);
        SeargeData srg = m_NameList.get(indexer);

        String obfMethodName = (srg == null ? methodName : (MCP_ENVIRONMENT ? srg.objectName : srg.objectNameObfuscated));

        if (obfMethodName.lastIndexOf('/') > -1)
            return obfMethodName.substring(obfMethodName.lastIndexOf('/') + 1);
        else
            return obfMethodName;
    }

    /**
     * Given a method name in path/to/class/methodname or path.to.class.methodname format
     * Returns method descriptor
     * 
     * @param ownerName The de-obfuscated class that contains the method (dot or internal format OK)
     * @param methodNameAndDescriptor The de-obfuscated name of the method with descriptor
     * @return The obfuscated/unobfuscated descriptor based on MCP_ENVIRONMENT
     */
    public String getMethodName(String ownerName, String methodNameAndDescriptor) {
        String rawMaD[] = methodNameAndDescriptor.split("\\s+");
        String indexer = internalizeName(ownerName + "." + methodNameAndDescriptor);
        SeargeData srg = m_NameList.get(indexer);

        String obfMethodName = (srg == null ? rawMaD[0] : (MCP_ENVIRONMENT ? srg.objectName : srg.objectNameObfuscated));

        if (obfMethodName.lastIndexOf('/') > -1)
            return obfMethodName.substring(obfMethodName.lastIndexOf('/') + 1);
        else
            return obfMethodName;
    }

    /**
     * Given a method name in path/to/class/methodname or path.to.class.methodname format
     * Returns method descriptor
     * 
     * @param ownerName The de-obfuscated class that contains the method (dot or internal format OK)
     * @param methodName The de-obfuscated name of the method
     * @param methodSignature The de-obfuscated signature of the method
     * @return The obfuscated/unobfuscated descriptor based on MCP_ENVIRONMENT
     */
    public String getMethodDescriptor(String ownerName, String methodName, String methodSignature) {
        String indexer = internalizeName(ownerName + "." + methodName + " " + methodSignature);
        SeargeData srg = m_NameList.get(indexer);

        return (srg == null ? methodSignature : (MCP_ENVIRONMENT ? srg.objectSignature : srg.objectSignatureObfuscated));
    }

    /**
     * Given a method name in path/to/class/methodname or path.to.class.methodname format
     * Returns method descriptor
     * 
     * @param ownerName The de-obfuscated class that contains the method (dot or internal format OK)
     * @param methodNameAndDescriptor The de-obfuscated name of the method with descriptor
     * @return The obfuscated/unobfuscated descriptor based on MCP_ENVIRONMENT
     */
    public String getMethodDescriptor(String ownerName, String methodNameAndDescriptor) {
        String rawMaD[] = methodNameAndDescriptor.split("\\s+");
        String indexer = internalizeName(ownerName + "." + methodNameAndDescriptor);
        SeargeData srg = m_NameList.get(indexer);

        return (srg == null ? rawMaD[1] : (MCP_ENVIRONMENT ? srg.objectSignature : srg.objectSignatureObfuscated));
    }

    /**
     * Determines if methodNode matches the specified method name and signature
     * 
     * @param methodNode Node to test
     * @param ownerName The de-obfuscated owner class of the method to test for
     * @param methodNameWithSignature The de-obfuscated method WITH signature to test for (eg: "setLightLevel (F)Lnet/minecraft/block/Block;") 
     * @return
     */
    public boolean isMethod(MethodNode methodNode, String ownerName, String methodNameWithSignature) {
        String indexer = internalizeName(ownerName + "/" + methodNameWithSignature);
        SeargeData srg = m_NameList.get(indexer);

        String methodName = "/" + internalizeName(methodNode.name);
        String methodSig = internalizeName(methodNode.desc);

        // BROKE: methodName=/a methodSig=(Lahu;)Z srgNameObf=amm/a srgSigObf=(Lahu;)Z
        //CLLog.info("MethodIs: methodName=" + methodName + " methodSig=" + methodSig + " srgNameObf=" + srg.objectNameObfuscated + " srgSigObf=" + srg.objectSignatureObfuscated);

        if (srg != null) {
            // srg.objectName = net/path/to/class/myMethodName
            // methodName = /myMethodName

            if (MCP_ENVIRONMENT)
                return srg.objectName.endsWith(methodName) && methodSig.equals(srg.objectSignature);
            else
                return srg.objectNameObfuscated.endsWith(methodName) && methodSig.equals(srg.objectSignatureObfuscated);
        } else
            return false;
    }

    /**
     * Given a JVM method/type descriptor such as "(Ljava/util/ArrayList;Lnet/minecraft/entity/player/EntityPlayerMP;)V",
     * returns an obfuscated descriptor like "(Ljava/util/ArrayList;Lmm;)V". Note: This method will return the
     * deobfuscated descriptor if running in Eclipse (MCP_ENVIRONMENT=true) 
     * 
     * @param descriptor The descriptor to decode
     * @return The proper descriptor
     */
    public String getJVMTypeObfuscated(String descriptor) {
        int i = 0, j;
        String className = "";
        String result = "";

        while (i < descriptor.length()) {
            if (descriptor.substring(i, i + 1).equals("L")) {
                // Extract class name
                j = i;

                while (!descriptor.substring(j, j + 1).equals(";"))
                    j++;

                className = descriptor.substring(i + 1, j);

                //CLLog.info("getMethodDescriptorObfuscated - Found class {}", className);

                result = result + "L" + getClassName(className) + ";";

                // Skip to end of class name
                i = j;
            } else
                result = result + descriptor.substring(i, i + 1);

            i++;
        }

        return result;
    }

    /**
     * Given a deobfuscated type, returns the appropriate obfuscated/deobfuscated name ready to use
     * in transformer code.
     * 
     * @author heaton84
     * 
     * @param deobfuscatedTypeDescriptor Eg: "net.minecraft.world.chunk.NibbleArray"
     * @return Deobfuscated name if running in eclipse. Obfuscated if running in production.
     */
    public Type getType(String deobfuscatedTypeDescriptor) {
        String internalName = internalizeName(deobfuscatedTypeDescriptor);

        if (internalName.startsWith("L") && internalName.endsWith(";"))
            internalName = internalName.substring(1, internalName.length() - 1);

        return Type.getType("L" + (MCP_ENVIRONMENT ? internalName : getClassName(internalName)) + ";");
    }

    public String getClassField(String deobfuscatedClassName, String deobfuscatedFieldName) {
        String indexer = internalizeName(deobfuscatedClassName + "/" + deobfuscatedFieldName);
        SeargeData srg = m_NameList.get(indexer);

        String fieldName = (MCP_ENVIRONMENT ? deobfuscatedFieldName : (srg == null ? null : srg.objectNameObfuscated));

        if (fieldName.lastIndexOf('/') > -1)
            return fieldName.substring(fieldName.lastIndexOf('/') + 1);
        else
            return fieldName;

    }

    private void registerSrgName(String data) {
        SeargeData item = new SeargeData(data);
        m_NameList.put(item.uniqueName, item);
    }

    private String internalizeName(String name) {
        return name.replace('.', '/');
    }

    private class SeargeData {
        //public String objectType;

        public String uniqueName; // Used to index object in array. Namely: Methods must include a signature!

        public String objectName;
        public String objectNameObfuscated;

        public String objectSignature;
        public String objectSignatureObfuscated;

        public SeargeData(String src) {
            String[] srcToken = src.split("\\s+");

            if (srcToken[0].equals("PK:")) {
                // Ignore package types for now
            } else if (srcToken[0].equals("CL:")) {
                // Class descriptor
                //objectType = "CL";

                if (srcToken.length == 3) {
                    objectName = srcToken[1];
                    objectNameObfuscated = srcToken[2];

                    uniqueName = objectName;
                } else {
                    // TODO
                }
            } else if (srcToken[0].equals("MD:")) {
                // Method descriptor
                //objectType = "MD";

                if (srcToken.length == 5) {
                    objectName = srcToken[1];
                    objectNameObfuscated = srcToken[3];

                    objectSignature = srcToken[2];
                    objectSignatureObfuscated = srcToken[4];

                    uniqueName = objectName + " " + objectSignature;
                } else {
                    // TODO
                }
            } else if (srcToken[0].equals("FD:")) {
                // Field descriptor

                if (srcToken.length == 3) {
                    objectName = srcToken[1];
                    objectNameObfuscated = srcToken[2];

                    uniqueName = objectName;
                } else {
                    // TODO
                }
            }
        }
    }

}
