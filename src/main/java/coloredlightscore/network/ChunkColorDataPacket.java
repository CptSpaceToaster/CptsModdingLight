package coloredlightscore.network;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import io.netty.buffer.ByteBuf;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.world.chunk.NibbleArray;
import coloredlightscore.server.ChunkStorageRGB;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChunkColorDataPacket implements IMessage, IMessageHandler<ChunkColorDataPacket, IMessage> {

    // In order of packet layout:
    //public int packetId;
    public int chunkXPosition;
    public int chunkZPosition;
    public int arraySize;

    // The following are stored as raw byte data:
    public NibbleArray[] RedColorArray;
    public NibbleArray[] GreenColorArray;
    public NibbleArray[] BlueColorArray;
    public int[] yLocation;

    private final boolean USE_COMPRESSION = true;

    @Override
    public IMessage onMessage(ChunkColorDataPacket packet, MessageContext context) {
        if (context.side == Side.CLIENT)
            processColorDataPacket(packet);

        return null;
    }

    @SideOnly(Side.CLIENT)
    private void processColorDataPacket(ChunkColorDataPacket packet) {
        ChunkColorDataPacket ccdPacket = (ChunkColorDataPacket) packet;
        Chunk targetChunk = null;

        targetChunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(ccdPacket.chunkXPosition, ccdPacket.chunkZPosition);

        if (targetChunk != null) {
            ChunkStorageRGB.loadColorData(targetChunk, ccdPacket.arraySize, ccdPacket.yLocation, ccdPacket.RedColorArray, ccdPacket.GreenColorArray, ccdPacket.BlueColorArray);
            //CLLog.info("ProcessColorDataPacket() loaded RGB for ({},{})", ccdPacket.chunkXPosition, ccdPacket.chunkZPosition);
        } else
            CLLog.warn("ProcessColorDataPacket()  Chunk located at ({}, {}) could not be found in the local world!", ccdPacket.chunkXPosition, ccdPacket.chunkZPosition);
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        try {
            byte[] rawColorData = new byte[2048 * 16 * 3];
            byte[] compressedColorData = new byte[32000];
            byte[] nibbleData = new byte[2048];
            int compressedSize;
            int arraysPresent;
            int p = 0;

            chunkXPosition = bytes.readInt();
            chunkZPosition = bytes.readInt();
            arraySize = bytes.readInt();

            yLocation = new int[arraySize];

            for (int i = 0; i < arraySize; i++)
                yLocation[i] = bytes.readInt();

            arraysPresent = bytes.readInt();

            RedColorArray = new NibbleArray[arraySize];
            GreenColorArray = new NibbleArray[arraySize];
            BlueColorArray = new NibbleArray[arraySize];

            if (USE_COMPRESSION) {
                compressedSize = bytes.readInt();
                bytes.readBytes(compressedColorData, 0, compressedSize);

                Inflater inflater = new Inflater();
                inflater.setInput(compressedColorData);

                try {
                    inflater.inflate(rawColorData);
                } catch (DataFormatException e) {
                    CLLog.warn("ChunkColorDataPacket()  ", e);
                } finally {
                    inflater.end();
                }
            } else
                // !USE_COMPRESSION
                bytes.readBytes(rawColorData);

            for (int i = 0; i < arraySize; i++) {
                if ((arraysPresent & (1 << i)) != 0) {
                    nibbleData = new byte[2048];
                    System.arraycopy(rawColorData, p, nibbleData, 0, 2048);
                    RedColorArray[i] = new NibbleArray(nibbleData, 4);

                    p += 2048;

                    nibbleData = new byte[2048];
                    System.arraycopy(rawColorData, p, nibbleData, 0, 2048);
                    GreenColorArray[i] = new NibbleArray(nibbleData, 4);

                    p += 2048;

                    nibbleData = new byte[2048];
                    System.arraycopy(rawColorData, p, nibbleData, 0, 2048);
                    BlueColorArray[i] = new NibbleArray(nibbleData, 4); // 4,59,10 y:3[11]  cx:-16  cz:10

                    p += 2048;

                } else {
                    RedColorArray[i] = new NibbleArray(4096, 4);
                    GreenColorArray[i] = new NibbleArray(4096, 4);
                    BlueColorArray[i] = new NibbleArray(4096, 4);
                }
            }

        } catch (Exception e) {
            CLLog.error("fromBytes ", e);
        }
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        try {
            byte[] rawColorData = new byte[2048 * 16 * 3];
            byte[] compressedColorData = new byte[32000];
            int compressedSize;
            int arraysPresent = 0;
            int p = 0;

            bytes.writeInt(chunkXPosition);
            bytes.writeInt(chunkZPosition);
            bytes.writeInt(arraySize);

            // Crank out nibble arrays
            for (int i = 0; i < arraySize; i++) {
                if (RedColorArray[i] != null || GreenColorArray[i] != null || BlueColorArray[i] != null) {
                    arraysPresent |= (1 << i);
                    if (FMLCommonHandler.instance().getModName().contains("cauldron")) {
                        byte[] localRed = RedColorArray[i].getValueArray();
                        byte[] localGreen = GreenColorArray[i].getValueArray();
                        byte[] localBlue = BlueColorArray[i].getValueArray();
                        System.arraycopy(localRed, 0, rawColorData, p, localRed.length);
                        p += localRed.length;
                        System.arraycopy(localGreen, 0, rawColorData, p, localGreen.length);
                        p += localGreen.length;
                        System.arraycopy(localBlue, 0, rawColorData, p, localBlue.length);
                        p += localBlue.length;

                    } else {
                        System.arraycopy(RedColorArray[i].data, 0, rawColorData, p, RedColorArray[i].data.length);
                        p += RedColorArray[i].data.length;
                        System.arraycopy(GreenColorArray[i].data, 0, rawColorData, p, GreenColorArray[i].data.length);
                        p += GreenColorArray[i].data.length;
                        System.arraycopy(BlueColorArray[i].data, 0, rawColorData, p, BlueColorArray[i].data.length);
                        p += BlueColorArray[i].data.length;
                    }


                    /*
                    arraysPresent |= (1 << i);
                    System.arraycopy(RedColorArray[i].data, 0, rawColorData, p, RedColorArray[i].data.length);
                    p += RedColorArray[i].data.length;
                    System.arraycopy(GreenColorArray[i].data, 0, rawColorData, p, GreenColorArray[i].data.length);
                    p += GreenColorArray[i].data.length;
                    System.arraycopy(BlueColorArray[i].data, 0, rawColorData, p, BlueColorArray[i].data.length);
                    p += BlueColorArray[i].data.length;
                    */
                }

                // Add Y location
                bytes.writeInt(yLocation[i]);
            }

            bytes.writeInt(arraysPresent);

            if (USE_COMPRESSION) {
                Deflater deflate = new Deflater(-1);
                deflate.setInput(rawColorData);
                deflate.finish();

                compressedSize = deflate.deflate(compressedColorData);

                if (compressedSize == 0)
                    CLLog.warn("writePacket compression failed");

                bytes.writeInt(compressedSize);
                bytes.writeBytes(compressedColorData, 0, compressedSize);
            } else
                // !USE_COMPRESSION
                bytes.writeBytes(rawColorData, 0, rawColorData.length);
        } catch (Exception e) {
            CLLog.error("toBytes  ", e);
        }
    }

}
