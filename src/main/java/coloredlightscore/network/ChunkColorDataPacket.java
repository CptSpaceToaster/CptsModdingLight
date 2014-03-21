package coloredlightscore.network;

import io.netty.buffer.ByteBuf;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import net.minecraft.world.chunk.NibbleArray;
import cpw.mods.fml.common.FMLLog;

public class ChunkColorDataPacket implements IPacket {

	public static int PACKET_ID = 0;
		
	// In order of packet layout:
	//public int packetId;
	public int chunkXPosition;
	public int chunkZPosition;	
	public int arraySize;
	
	// The following are stored as raw byte data:
	public NibbleArray[] RedColorArray;
	public NibbleArray[] GreenColorArray;
	public NibbleArray[] BlueColorArray;
		
	private final boolean USE_COMPRESSION = true;
	
	public ChunkColorDataPacket() {
	}

	public int getPacketId()
	{
		return PACKET_ID;
	}
	
	@Override
	public void readBytes(ByteBuf bytes) {
		
		try
		{
			byte[] rawColorData = new byte[2048 * 16 * 3];
			byte[] compressedColorData = new byte[32000];
			byte[] nibbleData = new byte[2048];
			int compressedSize;		
			int arraysPresent;
				
			chunkXPosition = bytes.readInt();
			chunkZPosition = bytes.readInt();
			arraySize = bytes.readInt();
			arraysPresent = bytes.readInt();
	
			RedColorArray = new NibbleArray[arraySize];
			GreenColorArray = new NibbleArray[arraySize];
			BlueColorArray = new NibbleArray[arraySize];
			
			if (USE_COMPRESSION)
			{
				compressedSize = bytes.readInt();
				bytes.readBytes(compressedColorData, 0, compressedSize);
	
				Inflater inflater = new Inflater();
				inflater.setInput(compressedColorData);
				
		        try
		        {
		            inflater.inflate(rawColorData);
		        } catch (DataFormatException e) {
					FMLLog.warning("ChunkColorDataPacket()  ", e);
				}
		        finally
		        {
		            inflater.end();
		        }
			}
			else // !USE_COMPRESSION
				bytes.readBytes(rawColorData);
	
			for (int i=0;i<arraySize;i++)
			{
				if ((arraysPresent & (1 << i)) != 0)
				{
					System.arraycopy(rawColorData, i * (2048 * 3), nibbleData, 0, 2048);
					RedColorArray[i] = new NibbleArray(nibbleData, 4);
		
					System.arraycopy(rawColorData, i * (2048 * 3) + 2048, nibbleData, 0, 2048);
					GreenColorArray[i] = new NibbleArray(nibbleData, 4);
		
					System.arraycopy(rawColorData, i * (2048 * 3)  + 4096, nibbleData, 0, 2048);
					BlueColorArray[i] = new NibbleArray(nibbleData, 4);
				}
			}
		}
		catch (Exception e)
		{
			FMLLog.getLogger().error("readBytes ", e);
		}
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		
		try
		{
			byte[] rawColorData = new byte[2048 * 16 * 3];
			byte[] compressedColorData = new byte[32000];
			int compressedSize;
			int arraysPresent = 0;
					
			bytes.writeInt(chunkXPosition);
			bytes.writeInt(chunkZPosition);
			bytes.writeInt(arraySize);
					
			// Crank out nibble arrays
			for (int i=0;i<arraySize;i++)
			{
				if (RedColorArray[i] != null || GreenColorArray[i] != null || BlueColorArray[i] != null)
					arraysPresent |= (1 << i);
							
				if (RedColorArray[i] != null)
					System.arraycopy(RedColorArray[i].data, 0, rawColorData, i * (2048 * 3), 2048);
				
				if (GreenColorArray[i] != null)
					System.arraycopy(GreenColorArray[i].data, 0, rawColorData, i * (2048 * 3) + 2048, 2048);
	
				if (BlueColorArray[i] != null)
					System.arraycopy(BlueColorArray[i].data, 0, rawColorData, i * (2048 * 3) + 4096, 2048);
			}
			
			bytes.writeInt(arraysPresent);
	
			if (USE_COMPRESSION)
			{
				Deflater deflate = new Deflater(-1);
				deflate.setInput(rawColorData);
				deflate.finish();
				
				compressedSize = deflate.deflate(compressedColorData);
				
				if (compressedSize == 0)
					FMLLog.warning("writePacket compression failed");
				
				bytes.writeInt(compressedSize);
				bytes.writeBytes(compressedColorData, 0, compressedSize);
			}
			else // !USE_COMPRESSION
				bytes.writeBytes(rawColorData, 0, rawColorData.length);
		}
		catch (Exception e)
		{
			FMLLog.getLogger().error("writeBytes  ", e);
		}
	}
}
