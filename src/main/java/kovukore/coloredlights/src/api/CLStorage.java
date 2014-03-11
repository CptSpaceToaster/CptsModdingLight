package kovukore.coloredlights.src.api;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import cpw.mods.fml.common.FMLLog;

/**
 * Methods for loading/saving RGB data to/from world save
 * 
 * @author heaton84
 */
public class CLStorage {

	private static String EVENT_SOURCE = "kovukore.coloredlights.src.api.CLStorage";
	
	private static Field fieldStorageArrays = null;
	private static Method methodSetRedColorArray = null;
	private static Method methodSetGreenColorArray = null;
	private static Method methodSetBlueColorArray = null;
	private static Method methodGetRedColorArray = null;
	private static Method methodGetGreenColorArray = null;
	private static Method methodGetBlueColorArray = null;
	
	
	private static void getReflectionData()	
	{
		if (CLStorage.fieldStorageArrays == null)
		{
			// TODO: Use access transformers instead
			try {
				CLStorage.fieldStorageArrays = Chunk.class.getDeclaredField("storageArrays");
				CLStorage.fieldStorageArrays.setAccessible(true);;
				
			} catch (NoSuchFieldException e) {
				FMLLog.severe("%s.getReflectionData()   Field \"storageArrays\" not found in Minecraft Chunk class!", EVENT_SOURCE);
				e.printStackTrace();
			} catch (SecurityException e) {
				FMLLog.severe("%s.getReflectionData()   Field \"storageArrays\" could not be made accessable!", EVENT_SOURCE);
				e.printStackTrace();
			}
			
			// We must use reflection for coremod-defined fields
			for (Method m : ExtendedBlockStorage.class.getMethods())
			{
				if (m.getName().equals("setRedColorArray"))
					methodSetRedColorArray = m;
				else if (m.getName().equals("setGreenColorArray"))
					methodSetGreenColorArray = m;
				else if (m.getName().equals("setBlueColorArray"))
					methodSetBlueColorArray = m;
				else if (m.getName().equals("getRedColorArray"))
					methodGetRedColorArray = m;
				else if (m.getName().equals("getGreenColorArray"))
					methodGetGreenColorArray = m;
				else if (m.getName().equals("getBlueColorArray"))
					methodGetBlueColorArray = m;
			}
			
			if (methodSetRedColorArray == null)
				FMLLog.severe("%s.getReflectionData()   Unable to locate method setRedColorArray in ExtendedBlockStorage!", EVENT_SOURCE);
			if (methodSetGreenColorArray == null)
				FMLLog.severe("%s.getReflectionData()   Unable to locate method setGreenColorArray in ExtendedBlockStorage!", EVENT_SOURCE);
			if (methodSetBlueColorArray == null)
				FMLLog.severe("%s.getReflectionData()   Unable to locate method setBlueColorArray in ExtendedBlockStorage!", EVENT_SOURCE);
			if (methodGetRedColorArray == null)
				FMLLog.severe("%s.getReflectionData()   Unable to locate method getRedColorArray in ExtendedBlockStorage!", EVENT_SOURCE);
			if (methodGetGreenColorArray == null)
				FMLLog.severe("%s.getReflectionData()   Unable to locate method getGreenColorArray in ExtendedBlockStorage!", EVENT_SOURCE);
			if (methodGetBlueColorArray == null)
				FMLLog.severe("%s.getReflectionData()   Unable to locate method getBlueColorArray in ExtendedBlockStorage!", EVENT_SOURCE);			
		}
	}

	private static NibbleArray checkedGetNibbleArray(byte[] rawdata)
	{
		if (rawdata.length == 0)
		{
			return new NibbleArray(4096, 4);
		}
		else
			return new NibbleArray(rawdata, 4);
	}	
	
	/**
	 * Loads RGB color data from a world store, if present.
	 * 
	 * @param chunk The chunk to populate with data
	 * @param data Top-level NBTTag, must contain "Level" tag.
	 * @return true if color data was loaded, false if not present or an error was encountered
	 */
	public static boolean loadColorData(Chunk chunk, NBTTagCompound data)
	{
		NibbleArray rColorArray;
		NibbleArray gColorArray;
		NibbleArray bColorArray;
		ExtendedBlockStorage[] chunkStorageArrays = null;
		NBTTagCompound level = data.getCompoundTag("Level");				
		NBTTagList nbttaglist = level.getTagList("Sections", 10);	
		boolean foundColorData = false;
		
		getReflectionData();
		
		try {
			chunkStorageArrays = (ExtendedBlockStorage[])fieldStorageArrays.get(chunk);						
		} catch (IllegalArgumentException e) {
			FMLLog.severe("%s.loadColorData()   Could not access ExtendedBlockStorage.storageArrays due to IllegalArgumentException", EVENT_SOURCE);
			return false;
		} catch (IllegalAccessException e) {
			FMLLog.severe("%s.loadColorData()   Could not access ExtendedBlockStorage.storageArrays due to IllegalAccessException", EVENT_SOURCE);
			return false;
		}		
		
		for (int k = 0; k < nbttaglist.tagCount(); ++k)
		{
			NBTTagCompound nbtYCompound = nbttaglist.getCompoundTagAt(k);
			
			if (chunkStorageArrays[k] != null)
			{
	            if (nbtYCompound.hasKey("RedColorArray")) //, 7))
	            {
	        		rColorArray = checkedGetNibbleArray(nbtYCompound.getByteArray("RedColorArray"));
	        		gColorArray = checkedGetNibbleArray(nbtYCompound.getByteArray("GreenColorArray"));
	        		bColorArray = checkedGetNibbleArray(nbtYCompound.getByteArray("BlueColorArray"));	          
	       
	      							
	        		try {
						methodSetRedColorArray.invoke(chunkStorageArrays[k], rColorArray);
						methodSetGreenColorArray.invoke(chunkStorageArrays[k], gColorArray);
						methodSetBlueColorArray.invoke(chunkStorageArrays[k], bColorArray);
						
						// TESTING: Pull back the forced value we stored earlier.
						// Should be "LOAD:111101111"
						FMLLog.info("LOAD:%s",  Integer.toBinaryString(chunk.getBlockLightValue(0, 0, 0, 15)));
						
						foundColorData = true;
					} catch (IllegalAccessException e) {
						FMLLog.severe("%s.loadColorData()   Unexpected IllegalAccessException while setting RGB color data!", EVENT_SOURCE);
						return false;
					} catch (IllegalArgumentException e) {
						FMLLog.severe("%s.loadColorData()   Unexpected IllegalArgumentException while setting RGB color data!", EVENT_SOURCE);
						return false;
					} catch (InvocationTargetException e) {
						FMLLog.severe("%s.loadColorData()   Unexpected InvocationTargetException while setting RGB color data!", EVENT_SOURCE);
						return false;
					}
	        		
	        		//FMLLog.info("Loaded nibble array for %s %s %s", chunk.xPosition, chunk.zPosition, k);
	            }
	            else
	            	FMLLog.warning("NO NIBBLE ARRAY EXISTS FOR %s %s %s", chunk.xPosition, chunk.zPosition, k);
			}
		}
		
		return foundColorData;
	}
	
	/**
	 * Saves RGB color data into world store NBTTag data. Should be called before chunk is saved.
	 * 
	 * @param chunk The chunk to extract RGB color data from.
	 * @param data Top-level NBTTag, must contain "Level" tag.
	 * @return true if color data was saved, false if an error was encountered
	 */
	public static boolean saveColorData(Chunk chunk, NBTTagCompound data)
	{
		NibbleArray rColorArray;
		NibbleArray gColorArray;
		NibbleArray bColorArray;
		ExtendedBlockStorage[] chunkStorageArrays = null;
		NBTTagCompound level = data.getCompoundTag("Level");				
		NBTTagList nbttaglist = level.getTagList("Sections", 10);	
		
		getReflectionData();		
		
		try {
			chunkStorageArrays = (ExtendedBlockStorage[])fieldStorageArrays.get(chunk);						
		} catch (IllegalArgumentException e) {
			FMLLog.severe("%s.saveColorData()   Could not access ExtendedBlockStorage.storageArrays due to IllegalArgumentException", EVENT_SOURCE);
			return false;
		} catch (IllegalAccessException e) {
			FMLLog.severe("%s.saveColorData()   Could not access ExtendedBlockStorage.storageArrays due to IllegalAccessException", EVENT_SOURCE);
			return false;
		}			
		
		if (chunk.xPosition == 18 && chunk.zPosition == -56)
			FMLLog.info("SAVING CHUNK OF INTEREST");
		
		for (int k = 0; k < chunkStorageArrays.length; k++)
		{
			if (chunkStorageArrays[k] != null)
			{
				NBTTagCompound nbtYCompound = nbttaglist.getCompoundTagAt(k);
				
				// Add our RGB arrays to it
	    		try {
	    			// TESTING: Force a known block to 100% red, 15 light level
	    			chunk.setLightValue(EnumSkyBlock.Block, 0, 0, 0, CLApi.makeColorLightValue(1.0F, 0, 0, 15));
	    			
					rColorArray = (NibbleArray)methodGetRedColorArray.invoke(chunkStorageArrays[k]);
					gColorArray = (NibbleArray)methodGetGreenColorArray.invoke(chunkStorageArrays[k]);
					bColorArray = (NibbleArray)methodGetBlueColorArray.invoke(chunkStorageArrays[k]);

					// TESTING: Verify what we have. Should be "SAVE:111101111"
					FMLLog.info("SAVE:%s",  Integer.toBinaryString(chunk.getBlockLightValue(0, 0, 0, 15)));
					
					nbtYCompound.setByteArray("RedColorArray", rColorArray.data);
					nbtYCompound.setByteArray("GreenColorArray", gColorArray.data);
					nbtYCompound.setByteArray("BlueColorArray", bColorArray.data);
					
				} catch (IllegalAccessException e) {
					FMLLog.severe("%s.saveColorData()   Unexpected IllegalAccessException while getting RGB color data!", EVENT_SOURCE);
					return false;
				} catch (IllegalArgumentException e) {
					FMLLog.severe("%s.saveColorData()   Unexpected IllegalArgumentException while getting RGB color data!", EVENT_SOURCE);
					return false;
				} catch (InvocationTargetException e) {
					FMLLog.severe("%s.saveColorData()   Unexpected InvocationTargetException while getting RGB color data!", EVENT_SOURCE);
					return false;
				} catch (Exception e) {
					FMLLog.severe("%s.saveColorData()   Unexpected Exception while getting RGB color data!", EVENT_SOURCE);
					return false;
				}
			}
		}				
		
		return true;
	}
}
