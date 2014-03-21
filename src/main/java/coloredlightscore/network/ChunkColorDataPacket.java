package coloredlightscore.network;

import io.netty.buffer.ByteBuf;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import net.minecraft.world.chunk.NibbleArray;

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
		
	public ChunkColorDataPacket() {
	}

	public int getPacketId()
	{
		return PACKET_ID;
	}
	
	@Override
	public void readBytes(ByteBuf bytes) {
		
		//packetId = bytes.readInt();
		byte[] rawColorData = new byte[(2048 + 2) * 16 * 3];
		byte[] compressedColorData = new byte[32000];
		int compressedSize;		
		
		chunkXPosition = bytes.readInt();
		chunkZPosition = bytes.readInt();
		arraySize = bytes.readInt();
		compressedSize = bytes.readInt();
		bytes.readBytes(compressedColorData, 0, compressedSize);
		
		RedColorArray = new NibbleArray[arraySize];
		GreenColorArray = new NibbleArray[arraySize];
		BlueColorArray = new NibbleArray[arraySize];
		
		Inflater inflater = new Inflater();
		inflater.setInput(compressedColorData);
		
        try
        {
            inflater.inflate(rawColorData);
        } catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally
        {
            inflater.end();
        }		

		for (int i=0;i<arraySize;i++)
		{
			
			// Write out # of bytes in an int
			// Or, if it's null, write a zero length
			
			if (RedColorArray[i] != null)
				System.arraycopy(rawColorData, i * (2048 * 3), RedColorArray[i], 0, 2048);
			
			if (GreenColorArray[i] != null)
				System.arraycopy(rawColorData, i * (2048 * 3) + 2048, GreenColorArray[i], 0, 2048);

			if (BlueColorArray[i] != null)
				System.arraycopy(rawColorData, i * (2048 * 3) + 4096, BlueColorArray[i], 0, 2048);
		}
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		
		//bytes.writeInt(PACKET_ID);
		
		byte[] rawColorData = new byte[(2048 + 2) * 16 * 3];
		byte[] compressedColorData = new byte[32000];
		int compressedSize;
		
		bytes.writeInt(chunkXPosition);
		bytes.writeInt(chunkZPosition);
		bytes.writeInt(arraySize);
		
		// Crank out nibble arrays
		for (int i=0;i<arraySize;i++)
		{
			
			// Write out # of bytes in an int
			// Or, if it's null, write a zero length
			
			if (RedColorArray[i] != null)
				System.arraycopy(RedColorArray[i], 0, rawColorData, i * (2048 * 3), 2048);
			
			if (GreenColorArray[i] != null)
				System.arraycopy(GreenColorArray[i], 0, rawColorData, i * (2048 * 3) + 2048, 2048);

			if (BlueColorArray[i] != null)
				System.arraycopy(BlueColorArray[i], 0, rawColorData, i * (2048 * 3) + 4096, 2048);
		}
		
		Deflater deflate = new Deflater(-1);
		deflate.setInput(rawColorData);
		deflate.finish();
		
		compressedSize = deflate.deflate(compressedColorData);
		bytes.writeInt(compressedSize);
		bytes.writeBytes(compressedColorData, 0, compressedSize);
	}

}
