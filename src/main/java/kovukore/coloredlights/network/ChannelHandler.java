package kovukore.coloredlights.network;

import java.util.EnumMap;

import kovukore.coloredlights.src.api.CLStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<IPacket> {

	public static ChannelHandler INSTANCE = null;
	
	public EnumMap<Side, FMLEmbeddedChannel> channels;
	public final String CHANNEL_NAME = "ColoredLightsCore";
	WorldClient world;
	
	public void registerNetwork()
	{
		channels = NetworkRegistry.INSTANCE.newChannel(CHANNEL_NAME, this);
	}
	
	public void registerWorldClient()
	{
		world = Minecraft.getMinecraft().theWorld;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IPacket msg, ByteBuf target) throws Exception {
		
		msg.writeBytes(target);
		
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, IPacket msg) {
		
		msg.readBytes(source);
		
		if (msg.getPacketId() == ChunkColorDataPacket.PACKET_ID)
			ProcessColorDataPacket(msg);
	}

	// TODO: Figure out player
	public void SendChunkColorData(Chunk chunk, Object player)
	{
		ChunkColorDataPacket packet = new ChunkColorDataPacket();
		NibbleArray[] redColorArray = CLStorage.getRedColorArrays(chunk);
		NibbleArray[] greenColorArray = CLStorage.getGreenColorArrays(chunk);
		NibbleArray[] blueColorArray = CLStorage.getBlueColorArrays(chunk);
		
		if (redColorArray == null || greenColorArray == null || blueColorArray == null)
			return;
		
		packet.packetId = ChunkColorDataPacket.PACKET_ID;
		packet.chunkXPosition = chunk.xPosition;
		packet.chunkZPosition = chunk.zPosition;
		packet.arraySize = redColorArray.length;
		packet.RedColorArray = redColorArray;
		packet.GreenColorArray = greenColorArray;
		packet.BlueColorArray = blueColorArray;
		
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		channels.get(Side.SERVER).writeOutbound(packet);		
	}
	
	private void ProcessColorDataPacket(IPacket packet)
	{
		ChunkColorDataPacket ccdPacket = (ChunkColorDataPacket)packet;
		Chunk targetChunk;
		
		targetChunk = world.getChunkFromChunkCoords(ccdPacket.chunkXPosition, ccdPacket.chunkZPosition);
		
		if (targetChunk != null)
			CLStorage.loadColorData(targetChunk, ccdPacket.arraySize, ccdPacket.RedColorArray, ccdPacket.GreenColorArray, ccdPacket.BlueColorArray);
		else
			FMLLog.warning("ProcessColorDataPacket()  Chunk located at (%s, %s) could not be found in the local world!", ccdPacket.chunkXPosition, ccdPacket.chunkZPosition);
	}
}
