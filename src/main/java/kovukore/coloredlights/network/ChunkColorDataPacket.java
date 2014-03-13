package kovukore.coloredlights.network;

import net.minecraft.world.chunk.NibbleArray;
import io.netty.buffer.ByteBuf;

public class ChunkColorDataPacket implements IPacket {

	public static int PACKET_ID = 0;
		
	// In order of packet layout:
	public int packetId;
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
		return packetId;
	}
	
	@Override
	public void readBytes(ByteBuf bytes) {
		
		packetId = bytes.readInt();
		chunkXPosition = bytes.readInt();
		chunkZPosition = bytes.readInt();
		arraySize = bytes.readInt();
		
		RedColorArray = new NibbleArray[arraySize];
		GreenColorArray = new NibbleArray[arraySize];
		BlueColorArray = new NibbleArray[arraySize];

		for (int i=0;i<arraySize;i++)
		{
			int byteSize = bytes.readInt();
			byte[] data;
			
			if (byteSize == 0)
				RedColorArray[i] = null;
			else
			{
				data = new byte[byteSize];				
				bytes.readBytes(data);
				RedColorArray[i] = new NibbleArray(data, 4);
			}
			
			byteSize = bytes.readInt();
			if (byteSize == 0)
				GreenColorArray[i] = null;
			else
			{
				data = new byte[byteSize];				
				bytes.readBytes(data);
				GreenColorArray[i] = new NibbleArray(data, 4);
			}			
			
			byteSize = bytes.readInt();
			if (byteSize == 0)
				BlueColorArray[i] = null;
			else
			{
				data = new byte[byteSize];				
				bytes.readBytes(data);
				BlueColorArray[i] = new NibbleArray(data, 4);
			}					
		}
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		
		bytes.writeInt(chunkXPosition);
		bytes.writeInt(chunkZPosition);
		bytes.writeInt(arraySize);
		
		// Crank out nibble arrays
		for (int i=0;i<arraySize;i++)
		{
			// Write out # of bytes in an int
			// Or, if it's null, write a zero length
			
			if (RedColorArray[i] == null)
				bytes.writeInt(0);
			else
			{
				bytes.writeInt(RedColorArray[i].data.length);
				bytes.writeBytes(RedColorArray[i].data);
			}
			
			if (GreenColorArray[i] == null)
				bytes.writeInt(0);
			else
			{
				bytes.writeInt(GreenColorArray[i].data.length);
				bytes.writeBytes(GreenColorArray[i].data);
			}

			if (BlueColorArray[i] == null)
				bytes.writeInt(0);
			else
			{
				bytes.writeInt(BlueColorArray[i].data.length);
				bytes.writeBytes(BlueColorArray[i].data);
			}

		}		
	}

}
