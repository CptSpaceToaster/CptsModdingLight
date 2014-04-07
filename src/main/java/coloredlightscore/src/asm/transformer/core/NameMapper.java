package coloredlightscore.src.asm.transformer.core;

import java.util.HashMap;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.FMLLog;

public class NameMapper {
	
	private HashMap<String, SeargeData> m_NameList;
	private static NameMapper INSTANCE = null;
	private boolean MCP_ENVIRONMENT = false;;
	
	public static NameMapper getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new NameMapper();
		
		return INSTANCE;
	}
	
	public NameMapper() {
		m_NameList = new HashMap<String, SeargeData>();
		
		// ***********************************************
		// CLASSES (CL) AND METHODS (MD) SHOULD COME FROM MCP-NOTCH.SRG
		// FIELDS (FD) SHOULD COME FROM MCP-SRG.SRG
		
		// Block
		registerSrgName("CL: net/minecraft/block/Block ahu");
		registerSrgName("MD: net/minecraft/block/Block/setLightLevel (F)Lnet/minecraft/block/Block; ahu/a (F)Lahu;");

		// ChunkCache		
		registerSrgName("CL: net/minecraft/world/ChunkCache agd");
		registerSrgName("MD: net/minecraft/world/ChunkCache/getLightBrightnessForSkyBlocks (IIII)I agd/c (IIII)I");
		
		// EntityPlayerMP
		registerSrgName("CL: net/minecraft/entity/player/EntityPlayerMP mm");
		registerSrgName("MD: net/minecraft/entity/player/EntityPlayerMP/onUpdate ()V mm/h ()V");
		
		// EntityRenderer
		registerSrgName("CL: net/minecraft/client/renderer/EntityRenderer bll");
		registerSrgName("MD: net/minecraft/client/renderer/EntityRenderer/updateLightmap (F)V bll/h (F)V");
		registerSrgName("MD: net/minecraft/client/renderer/EntityRenderer/disableLightmap (D)V bll/a (D)V");
		registerSrgName("MD: net/minecraft/client/renderer/EntityRenderer/enableLightmap (D)V bll/b (D)V");
		
		// ExtendedBlockStorage
		registerSrgName("CL: net/minecraft/world/chunk/storage/ExtendedBlockStorage aol");
		registerSrgName("MD: net/minecraft/world/chunk/storage/ExtendedBlockStorage/setExtBlocklightValue (IIII)V aol/c (IIII)V");
		registerSrgName("MD: net/minecraft/world/chunk/storage/ExtendedBlockStorage/getExtBlocklightValue (III)I aol/d (III)I");
		registerSrgName("FD: net/minecraft/world/chunk/storage/ExtendedBlockStorage/blockLSBArray net/minecraft/world/chunk/storage/ExtendedBlockStorage/field_76680_d");

		// PlayerInstance
		registerSrgName("CL: net/minecraft/server/management/PlayerManager$PlayerInstance mh");
		registerSrgName("MD: net/minecraft/server/management/PlayerManager$PlayerInstance/sendToAllPlayersWatchingChunk (Lnet/minecraft/network/Packet;)V mh/a (Lfk;)V");
		registerSrgName("FD: net/minecraft/server/management/PlayerManager$PlayerInstance/chunkLocation net/minecraft/server/management/PlayerManager$PlayerInstance/field_73264_c");
		
		// RenderBlocks
		registerSrgName("CL: net/minecraft/client/renderer/RenderBlocks ble");
		registerSrgName("MD: net/minecraft/client/renderer/RenderBlocks/renderStandardBlockWithAmbientOcclusion (Lnet/minecraft/block/Block;IIIFFF)Z ble/a (Lahu;IIIFFF)Z");
		registerSrgName("MD: net/minecraft/client/renderer/RenderBlocks/renderStandardBlockWithColorMultiplier (Lnet/minecraft/block/Block;IIIFFF)Z ble/d (Lahu;IIIFFF)Z");
		
		// Tessellator
		registerSrgName("CL: net/minecraft/client/renderer/Tessellator blz");
		registerSrgName("MD: net/minecraft/client/renderer/Tessellator/setBrightness (I)V blz/b (I)V");
		registerSrgName("MD: net/minecraft/client/renderer/Tessellator/draw ()I blz/a ()I");
		
		// World
		registerSrgName("CL: net/minecraft/world/World afn");
		registerSrgName("MD: net/minecraft/world/World/getBlockLightValue_do (IIIZ)I afn/b (IIIZ)I");
		registerSrgName("MD: net/minecraft/world/World/getLightBrightnessForSkyBlocks (IIII)I afn/c (IIII)I");
		registerSrgName("MD: net/minecraft/world/World/getLightBrightness (III)F afn/n (III)F");
		registerSrgName("MD: net/minecraft/world/World/computeLightValue (IIILnet/minecraft/world/EnumSkyBlock;)I afn/a (IIILafz;)I");
		registerSrgName("MD: net/minecraft/world/World/updateLightByType (Lnet/minecraft/world/EnumSkyBlock;III)Z afn/c (Lafz;III)Z");
		
		// NetHandlerPlayServer
		registerSrgName("CL: net/minecraft/network/NetHandlerPlayServer mx");
		registerSrgName("MD: net/minecraft/network/NetHandlerPlayServer/sendPacket (Lnet/minecraft/network/Packet;)V mx/a (Lfk;)V");
		
		// NibbleArray
		registerSrgName("CL: net/minecraft/world/chunk/NibbleArray aoh");
		registerSrgName("MD: net/minecraft/world/chunk/NibbleArray/get (III)I aoh/a (III)I");
		registerSrgName("MD: net/minecraft/world/chunk/NibbleArray/set (IIII)V aoh/a (IIII)V");

		// DynamicTexture
		registerSrgName("CL: net/minecraft/client/renderer/texture/DynamicTexture bpi");
				
		// S26PacketMapChunkBulk
		registerSrgName("CL: net/minecraft/network/play/server/S26PacketMapChunkBulk gq");
		
		// ***********************************************
	
		MCP_ENVIRONMENT = (this.getClass().getClassLoader().getResource("net/minecraft/world/World.class") != null);
		
		FMLLog.info("ColoredLightsCore: MCP_ENVIORNMENT=%s", MCP_ENVIRONMENT);
	}
	
	public boolean isObfuscated()
	{
		return !MCP_ENVIRONMENT;
	}
	
	/**
	 * Returns the appropriate obfuscated or unobfuscated class name for a Minecraft class
	 * 
	 * @param className De-obfuscated class name, in either dot or internal format.
	 * @return
	 */
	public String getClassName(String className)
	{
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
	public String getMethodName(String ownerName, String methodName, String methodSignature)
	{
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
	public String getMethodName(String ownerName, String methodNameAndDescriptor)
	{
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
	public String getMethodDescriptor(String ownerName, String methodName, String methodSignature)
	{
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
	public String getMethodDescriptor(String ownerName, String methodNameAndDescriptor)
	{
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
	public boolean isMethod(MethodNode methodNode, String ownerName, String methodNameWithSignature)
	{
		String indexer = internalizeName(ownerName + "/" + methodNameWithSignature);
		SeargeData srg = m_NameList.get(indexer);
		
		String methodName = "/" + internalizeName(methodNode.name);
		String methodSig = internalizeName(methodNode.desc);
		
		// BROKE: methodName=/a methodSig=(Lahu;)Z srgNameObf=amm/a srgSigObf=(Lahu;)Z
		//FMLLog.info("MethodIs: methodName=" + methodName + " methodSig=" + methodSig + " srgNameObf=" + srg.objectNameObfuscated + " srgSigObf=" + srg.objectSignatureObfuscated);
						
		if (srg != null)
		{
			// srg.objectName = net/path/to/class/myMethodName
			// methodName = /myMethodName
						
			if (MCP_ENVIRONMENT)
				return srg.objectName.endsWith(methodName) && methodSig.equals(srg.objectSignature);
			else
				return srg.objectNameObfuscated.endsWith(methodName) && methodSig.equals(srg.objectSignatureObfuscated);
		}
		else
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
	public String getJVMTypeObfuscated(String descriptor)
	{
		int i = 0, j;
		String className = "";
		String result = "";
		
		while (i < descriptor.length())
		{
			if (descriptor.substring(i, i + 1).equals("L"))
			{
				// Extract class name
				j = i;
				
				while (!descriptor.substring(j, j + 1).equals(";"))
					j++;
				
				className = descriptor.substring(i + 1, j);
				
				//FMLLog.info("getMethodDescriptorObfuscated - Found class %s", className);
				
				result = result + "L" + getClassName(className) + ";";
				
				// Skip to end of class name
				i = j;
			}
			else
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
	public Type getType(String deobfuscatedTypeDescriptor)
	{
		String internalName = internalizeName(deobfuscatedTypeDescriptor);

		if (internalName.startsWith("L") && internalName.endsWith(";"))
			internalName = internalName.substring(1, internalName.length() - 1);
		
		return Type.getType("L" + (MCP_ENVIRONMENT ? internalName : getClassName(internalName)) + ";");
	}	
	
	public String getClassField(String deobfuscatedClassName, String deobfuscatedFieldName)
	{
		String indexer = internalizeName(deobfuscatedClassName + "/" + deobfuscatedFieldName);
		SeargeData srg = m_NameList.get(indexer);
		
		String fieldName = (MCP_ENVIRONMENT ? deobfuscatedFieldName : (srg == null ? null : srg.objectNameObfuscated));
		
		if (fieldName.lastIndexOf('/') > -1)
			return fieldName.substring(fieldName.lastIndexOf('/') + 1);
		else
			return fieldName;
		
	}	
	
	private void registerSrgName(String data)
	{
		SeargeData item = new SeargeData(data);
		m_NameList.put(item.uniqueName, item);		
	}	
		
	private String internalizeName(String name)
	{
		return name.replace('.', '/');
	}	
	
	private class SeargeData
	{
		//public String objectType;
		
		public String uniqueName; // Used to index object in array. Namely: Methods must include a signature!
		
		public String objectName;
		public String objectNameObfuscated;
		
		public String objectSignature;
		public String objectSignatureObfuscated;
		
		public SeargeData(String src)
		{
			String[] srcToken = src.split("\\s+");
			
			if (srcToken[0].equals("PK:"))
			{
				// Ignore package types for now
			}
			else if (srcToken[0].equals("CL:"))
			{
				// Class descriptor
				//objectType = "CL";
				
				if (srcToken.length == 3)
				{
					objectName = srcToken[1];
					objectNameObfuscated = srcToken[2];
					
					uniqueName = objectName;
				}
				else
				{
					// TODO
				}
			}
			else if (srcToken[0].equals("MD:"))
			{
				// Method descriptor
				//objectType = "MD";
				
				if (srcToken.length == 5)
				{
					objectName = srcToken[1];
					objectNameObfuscated = srcToken[3];

					objectSignature = srcToken[2];
					objectSignatureObfuscated = srcToken[4];
					
					uniqueName = objectName + " " + objectSignature;
				}
				else
				{
					// TODO
				}
			}			
			else if (srcToken[0].equals("FD:"))
			{
				// Field descriptor
				
				if (srcToken.length == 3)
				{
					objectName = srcToken[1];
					objectNameObfuscated = srcToken[2];
					
					uniqueName = objectName;
				}
				else
				{
					// TODO
				}
			}
		}
	}
	
}
