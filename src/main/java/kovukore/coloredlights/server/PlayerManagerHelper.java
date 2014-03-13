package kovukore.coloredlights.server;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.ChunkCoordIntPair;

public class PlayerManagerHelper {

	public PlayerManagerHelper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Invoked for each player in net.minecraft.server.management.PlayerManager.sendToAllPlayersWatchingChunk
	 * 
	 * Happens when a server is sending chunk data to a player
	 * 
	 * @param player
	 * @param chunkLocation
	 */
    public static void sendToPlayerWatchingChunk(EntityPlayerMP player, ChunkCoordIntPair chunkLocation)
    {
    	FMLLog.info("Server just sent chunk (%s, %s) to player %s", chunkLocation.chunkXPos, chunkLocation.chunkZPos, player.getDisplayName());
        /*for (int i = 0; i < instance.playersWatchingChunk.size(); ++i)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)instance.playersWatchingChunk.get(i);

            if (!entityplayermp.loadedChunks.contains(instance.chunkLocation))
            {
                entityplayermp.playerNetServerHandler.sendPacket(p_151251_1_);
            }
        }*/
    }	
}
