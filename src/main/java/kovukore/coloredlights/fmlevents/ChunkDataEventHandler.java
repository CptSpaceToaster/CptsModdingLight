package kovukore.coloredlights.fmlevents;

import kovukore.coloredlights.src.api.CLStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ChunkDataEventHandler {

	public ChunkDataEventHandler() {
		// TODO Auto-generated constructor stub
	}
	
	@SubscribeEvent	
	public void LoadChunk(ChunkDataEvent.Load event)
	{
		Chunk chunk = event.getChunk();
		NBTTagCompound data = event.getData();
		
		if (!CLStorage.loadColorData(chunk, data))
			FMLLog.warning("Failed to load color data for chunk at (%s, %s)", chunk.xPosition, chunk.zPosition);
	}

	@SubscribeEvent	
	public void SaveChunk(ChunkDataEvent.Save event)
	{
		Chunk chunk = event.getChunk();
		NBTTagCompound data = event.getData();

		if (!CLStorage.saveColorData(chunk, data))
			FMLLog.warning("Failed to save color data for chunk at (%s, %s)", chunk.xPosition, chunk.zPosition);
	}		

}
