package kovukore.coloredlights.fmlevents;

import kovukore.coloredlights.src.api.CLStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChunkDataEventHandler {

	
	public ChunkDataEventHandler() {
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

	@SubscribeEvent	
	public void UnloadChunk(ChunkWatchEvent.UnWatch event)
	{
		//FMLLog.info("UnloadChunk at (%s,%s) for %s", event.chunk.chunkXPos, event.chunk.chunkZPos, event.player.getDisplayName());
	}		
	
}
