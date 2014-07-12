package coloredlightscore.network;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import coloredlightscore.server.ChunkStorageRGB;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("ColoredLightsCore");
    private static Method methodGetValueArray = null;//Cauldron Compatibility

    public static void init() {
        INSTANCE.registerMessage(ChunkColorDataPacket.class, ChunkColorDataPacket.class, 0, Side.SERVER);
        INSTANCE.registerMessage(ChunkColorDataPacket.class, ChunkColorDataPacket.class, 0, Side.CLIENT);

        //Cauldron Compatibility
        try {
            methodGetValueArray = NibbleArray.class.getMethod("getValueArray");
        } catch (NoSuchMethodException e) {
            FMLLog.info("Unable to hook getValueArray, Ignore if not running cauldron");
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void sendChunkColorData(Chunk chunk, EntityPlayerMP player) {
        try {
            ChunkColorDataPacket packet = new ChunkColorDataPacket(methodGetValueArray);
            NibbleArray[] redColorArray = ChunkStorageRGB.getRedColorArrays(chunk);
            NibbleArray[] greenColorArray = ChunkStorageRGB.getGreenColorArrays(chunk);
            NibbleArray[] blueColorArray = ChunkStorageRGB.getBlueColorArrays(chunk);

            if (redColorArray == null || greenColorArray == null || blueColorArray == null) {
                return;
            }

            packet.chunkXPosition = chunk.xPosition;
            packet.chunkZPosition = chunk.zPosition;
            packet.arraySize = redColorArray.length;
            packet.yLocation = ChunkStorageRGB.getYLocationArray(chunk);
            packet.RedColorArray = redColorArray;
            packet.GreenColorArray = greenColorArray;
            packet.BlueColorArray = blueColorArray;

            //this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            //this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            //this.channels.get(Side.SERVER).writeOutbound(packet);		

            //Think this is right 
            INSTANCE.sendTo(packet, player);

            //FMLLog.info("SendChunkColorData()  Sent for %s, %s", chunk.xPosition, chunk.zPosition);
        } catch (Exception e) {
            FMLLog.getLogger().warn("SendChunkColorData()  ", e);
        }

    }

}
