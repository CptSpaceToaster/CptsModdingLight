package kovukore.coloredlights.network;

import io.netty.buffer.ByteBuf;

public interface IPacket {

		public int getPacketId();
	
	    public void readBytes(ByteBuf bytes);
	    public void writeBytes(ByteBuf bytes);
}
