package coloredlightscore.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;

/**
 * Methods for loading/saving RGB data to/from world save
 * 
 * @author heaton84
 */
public class ChunkStorageRGB {

    private static String EVENT_SOURCE = "coloredlightscore.server.ChunkStorageRGB";

    private static Method methodSetRedColorArray = null;
    private static Method methodSetGreenColorArray = null;
    private static Method methodSetBlueColorArray = null;
    private static Method methodGetRedColorArray = null;
    private static Method methodGetGreenColorArray = null;
    private static Method methodGetBlueColorArray = null;
    private static Method methodGetValueArray = null;

    /**
     * Builds references to the getter/setter methods for each RGB Color Array.
     * These methods are built at runtime using ASM transformation.
     */
    private static void getReflectionData() {
        if (ChunkStorageRGB.methodSetRedColorArray == null) {
            // We must use reflection for coremod-defined methods
            for (Method m : ExtendedBlockStorage.class.getMethods()) {
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

            try {
                methodGetValueArray = NibbleArray.class.getMethod("getValueArray");
            } catch (NoSuchMethodException e) {
                FMLLog.info("Unable to hook getValueArray, Ignore if not running cauldron");
            } catch (SecurityException e) {
                e.printStackTrace();
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

    /**
     * Constructs a NibbleArray from a raw stream of byte data. If the
     * byte data is incomplete, returns an empty array.
     * 
     * @param rawdata The raw bytestream to build an array from.
     * @return Instance of a NibbleArray.
     */
    private static NibbleArray checkedGetNibbleArray(byte[] rawdata) {
        if (rawdata.length == 0) {
            return new NibbleArray(4096, 4);
        } else if (rawdata.length < 2048) {
            FMLLog.warning("checkedGetNibbleArray: rawdata is too short: %s, expected 2048", rawdata.length);
            return new NibbleArray(4096, 4);
        } else
            return new NibbleArray(rawdata, 4);
    }

    /**
     * Loads RGB color data from a world store, if present.
     * 
     * @param chunk The chunk to populate with data
     * @param data Top-level NBTTag, must contain "Level" tag.
     * @return true if color data was loaded, false if not present or an error was encountered
     */
    public static boolean loadColorData(Chunk chunk, NBTTagCompound data) {
        NibbleArray rColorArray;
        NibbleArray gColorArray;
        NibbleArray bColorArray;
        ExtendedBlockStorage[] chunkStorageArrays = chunk.getBlockStorageArray();
        NBTTagCompound level = data.getCompoundTag("Level");
        NBTTagList nbttaglist = level.getTagList("Sections", 10);
        boolean foundColorData = false;

        getReflectionData();

        for (int k = 0; k < nbttaglist.tagCount(); ++k) {
            NBTTagCompound nbtYCompound = nbttaglist.getCompoundTagAt(k);

            if (chunkStorageArrays[k] != null) {
                if (nbtYCompound.hasKey("RedColorArray")) //, 7))
                {
                    rColorArray = checkedGetNibbleArray(nbtYCompound.getByteArray("RedColorArray"));
                    gColorArray = checkedGetNibbleArray(nbtYCompound.getByteArray("GreenColorArray"));
                    bColorArray = checkedGetNibbleArray(nbtYCompound.getByteArray("BlueColorArray"));

                    try {
                        // Set color arrays on chunk.storageArrays

                        methodSetRedColorArray.invoke(chunkStorageArrays[k], rColorArray);
                        methodSetGreenColorArray.invoke(chunkStorageArrays[k], gColorArray);
                        methodSetBlueColorArray.invoke(chunkStorageArrays[k], bColorArray);

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
                //else
                //FMLLog.warning("NO NIBBLE ARRAY EXISTS FOR %s %s %s", chunk.xPosition, chunk.zPosition, k);
            }
        }

        return foundColorData;
    }

    /**
     * Loads RGB color data from a world store, if present.
     * 
     * @param chunk The chunk to populate with data
     * @param data Top-level NBTTag, must contain "Level" tag.
     * @return true if color data was loaded, false if not present or an error was encountered
     */
    public static boolean loadColorData(Chunk chunk, int arraySize, int[] yLocation, NibbleArray[] redColorData, NibbleArray[] greenColorData, NibbleArray[] blueColorData) {
        NibbleArray rColorArray;
        NibbleArray gColorArray;
        NibbleArray bColorArray;
        ExtendedBlockStorage[] chunkStorageArrays = chunk.getBlockStorageArray();
        boolean foundColorData = false;

        getReflectionData();

        for (int k = 0; k < arraySize; ++k) {
            if (chunkStorageArrays[k] != null) {
                if (chunkStorageArrays[k].getYLocation() != yLocation[k])
                    FMLLog.severe("EBS DATA OUT OF SEQUENCE. Expected %s, got %s", chunkStorageArrays[k].getYLocation(), yLocation[k]);

                try {
                    rColorArray = redColorData[k];
                    gColorArray = greenColorData[k];
                    bColorArray = blueColorData[k];

                    // Set color arrays on chunk.storageArrays

                    methodSetRedColorArray.invoke(chunkStorageArrays[k], rColorArray);
                    methodSetGreenColorArray.invoke(chunkStorageArrays[k], gColorArray);
                    methodSetBlueColorArray.invoke(chunkStorageArrays[k], bColorArray);

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
                } catch (Exception e) {
                    FMLLog.getLogger().error("loadColorData()  ", e);
                }

                //FMLLog.info("Loaded nibble array for %s %s %s", chunk.xPosition, chunk.zPosition, k);
            }
            //else
            //FMLLog.warning("NO NIBBLE ARRAY EXISTS FOR %s %s %s", chunk.xPosition, chunk.zPosition, k);
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
    public static boolean saveColorData(Chunk chunk, NBTTagCompound data) {
        NibbleArray rColorArray;
        NibbleArray gColorArray;
        NibbleArray bColorArray;
        ExtendedBlockStorage[] chunkStorageArrays = chunk.getBlockStorageArray();
        NBTTagCompound level = data.getCompoundTag("Level");
        NBTTagList nbttaglist = level.getTagList("Sections", 10);

        getReflectionData();

        for (int k = 0; k < chunkStorageArrays.length; k++) {
            if (chunkStorageArrays[k] != null) {
                NBTTagCompound nbtYCompound = nbttaglist.getCompoundTagAt(k);

                // Add our RGB arrays to it
                try {
                    rColorArray = (NibbleArray) methodGetRedColorArray.invoke(chunkStorageArrays[k]);
                    gColorArray = (NibbleArray) methodGetGreenColorArray.invoke(chunkStorageArrays[k]);
                    bColorArray = (NibbleArray) methodGetBlueColorArray.invoke(chunkStorageArrays[k]);

                    if (FMLCommonHandler.instance().getModName().contains("cauldron")) {//cauldron has a patch that will cause a crash if we don't do this instead..
                        nbtYCompound.setByteArray("RedColorArray", (byte[]) methodGetValueArray.invoke(rColorArray));
                        nbtYCompound.setByteArray("GreenColorArray", (byte[]) methodGetValueArray.invoke(gColorArray));
                        nbtYCompound.setByteArray("BlueColorArray", (byte[]) methodGetValueArray.invoke(bColorArray));
                    } else {//run as base forge/mc
                        nbtYCompound.setByteArray("RedColorArray", rColorArray.data);
                        nbtYCompound.setByteArray("GreenColorArray", gColorArray.data);
                        nbtYCompound.setByteArray("BlueColorArray", bColorArray.data);
                    }

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

    /**
     * Extracts all the red color arrays from a chunk's extended block storage
     * 
     * @param chunk
     * @return An array of NibbleArrays containing red color data for the chunk
     */
    public static NibbleArray[] getRedColorArrays(Chunk chunk) {
        ExtendedBlockStorage[] chunkStorageArrays = chunk.getBlockStorageArray();
        NibbleArray[] redColorArrays;

        getReflectionData();

        redColorArrays = new NibbleArray[chunkStorageArrays.length];

        for (int i = 0; i < chunkStorageArrays.length; i++) {
            try {

                if (chunkStorageArrays[i] != null)
                    redColorArrays[i] = (NibbleArray) methodGetRedColorArray.invoke(chunkStorageArrays[i]);
                else
                    redColorArrays[i] = null;

            } catch (IllegalAccessException e) {
                FMLLog.severe("%s.getRedColorArrays()   Unexpected IllegalAccessException while getting RGB color data!", EVENT_SOURCE);
                return null;
            } catch (IllegalArgumentException e) {
                FMLLog.severe("%s.getRedColorArrays()   Unexpected IllegalArgumentException while getting RGB color data!", EVENT_SOURCE);
                return null;
            } catch (InvocationTargetException e) {
                FMLLog.severe("%s.getRedColorArrays()   Unexpected InvocationTargetException while getting RGB color data!", EVENT_SOURCE);
                return null;
            } catch (Exception e) {
                FMLLog.severe("%s.getRedColorArrays()   Unexpected Exception while getting RGB color data!", EVENT_SOURCE);
                return null;
            }
        }

        return redColorArrays;
    }

    /**
     * Extracts all the green color arrays from a chunk's extended block storage
     * 
     * @param chunk
     * @return An array of NibbleArrays containing green color data for the chunk
     */

    public static NibbleArray[] getGreenColorArrays(Chunk chunk) {
        ExtendedBlockStorage[] chunkStorageArrays = chunk.getBlockStorageArray();
        NibbleArray[] greenColorArrays;

        getReflectionData();

        greenColorArrays = new NibbleArray[chunkStorageArrays.length];

        for (int i = 0; i < chunkStorageArrays.length; i++) {
            try {

                if (chunkStorageArrays[i] != null)
                    greenColorArrays[i] = (NibbleArray) methodGetGreenColorArray.invoke(chunkStorageArrays[i]);
                else
                    greenColorArrays[i] = null;

            } catch (IllegalAccessException e) {
                FMLLog.severe("%s.getGreenColorArrays()   Unexpected IllegalAccessException while getting RGB color data!", EVENT_SOURCE);
                return null;
            } catch (IllegalArgumentException e) {
                FMLLog.severe("%s.getGreenColorArrays()   Unexpected IllegalArgumentException while getting RGB color data!", EVENT_SOURCE);
                return null;
            } catch (InvocationTargetException e) {
                FMLLog.severe("%s.getGreenColorArrays()   Unexpected InvocationTargetException while getting RGB color data!", EVENT_SOURCE);
                return null;
            } catch (Exception e) {
                FMLLog.severe("%s.getGreenColorArrays()   Unexpected Exception while getting RGB color data!", EVENT_SOURCE);
                return null;
            }
        }

        return greenColorArrays;
    }

    /**
     * Extracts all the blue color arrays from a chunk's extended block storage
     * 
     * @param chunk
     * @return An array of NibbleArrays containing blue color data for the chunk
     */

    public static NibbleArray[] getBlueColorArrays(Chunk chunk) {
        ExtendedBlockStorage[] chunkStorageArrays = chunk.getBlockStorageArray();
        NibbleArray[] blueColorArrays;

        getReflectionData();

        blueColorArrays = new NibbleArray[chunkStorageArrays.length];

        for (int i = 0; i < chunkStorageArrays.length; i++) {
            try {

                if (chunkStorageArrays[i] != null)
                    blueColorArrays[i] = (NibbleArray) methodGetBlueColorArray.invoke(chunkStorageArrays[i]);
                else
                    blueColorArrays[i] = null;

            } catch (IllegalAccessException e) {
                FMLLog.severe("%s.getBlueColorArrays()   Unexpected IllegalAccessException while getting RGB color data!", EVENT_SOURCE);
                return null;
            } catch (IllegalArgumentException e) {
                FMLLog.severe("%s.getBlueColorArrays()   Unexpected IllegalArgumentException while getting RGB color data!", EVENT_SOURCE);
                return null;
            } catch (InvocationTargetException e) {
                FMLLog.severe("%s.getBlueColorArrays()   Unexpected InvocationTargetException while getting RGB color data!", EVENT_SOURCE);
                return null;
            } catch (Exception e) {
                FMLLog.severe("%s.getBlueColorArrays()   Unexpected Exception while getting RGB color data!", EVENT_SOURCE);
                return null;
            }
        }

        return blueColorArrays;
    }

    public static int[] getYLocationArray(Chunk chunk) {
        ExtendedBlockStorage[] ebs = chunk.getBlockStorageArray();
        int y[] = new int[ebs.length];

        for (int i = 0; i < ebs.length; i++) {
            if (ebs[i] == null)
                y[i] = -1;
            else
                y[i] = ebs[i].getYLocation();
        }

        return y;
    }
}
