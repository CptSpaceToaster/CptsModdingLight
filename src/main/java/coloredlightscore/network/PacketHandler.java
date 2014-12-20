package coloredlightscore.network;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import coloredlightscore.server.ChunkStorageRGB;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("ColoredLightsCore");

    public static void init() {
        INSTANCE.registerMessage(ChunkColorDataPacket.class, ChunkColorDataPacket.class, 0, Side.SERVER);
        INSTANCE.registerMessage(ChunkColorDataPacket.class, ChunkColorDataPacket.class, 0, Side.CLIENT);
    }

    public static void sendChunkColorData(Chunk chunk, EntityPlayerMP player) {
        try {
            ChunkColorDataPacket packet = new ChunkColorDataPacket();
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

            //CLLog.info("SendChunkColorData()  Sent for {}, {}", chunk.xPosition, chunk.zPosition);
        } catch (Exception e) {
            CLLog.warn("SendChunkColorData()  ", e);
        }

    }

}
