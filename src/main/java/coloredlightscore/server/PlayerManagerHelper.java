package coloredlightscore.server;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.FMLLog;

public class PlayerManagerHelper {

    public PlayerManagerHelper() {
    }

    /**
     * Invoked for each player in net.minecraft.server.management.PlayerManager.sendToAllPlayersWatchingChunk
     * 
     * Happens when a server is sending chunk data to a player
     * 
     * @param player
     * @param chunkLocation
     */
    public static void sendToPlayerWatchingChunk(EntityPlayerMP player, ChunkCoordIntPair chunkLocation) {
        //FMLLog.info("Server just sent chunk (%s, %s) to player %s", chunkLocation.chunkXPos, chunkLocation.chunkZPos, player.getDisplayName());

        // TODO: Load chunk from server
        //sendChunkRGBDataToPlayer(player, chunkLocation.chunkXPos, chunkLocation.chunkZPos, null);
    }

    public static void entityPlayerMP_onUpdate(ArrayList<Chunk> chunks, EntityPlayerMP player) {
        for (Chunk c : chunks) {
            //FMLLog.info("S26: Server just sent chunk (%s, %s) to player %s", c.xPosition, c.zPosition, player.getDisplayName());

            sendChunkRGBDataToPlayer(player, c.xPosition, c.zPosition, c);
        }
    }

    public static void sendChunkRGBDataToPlayer(EntityPlayerMP player, int chunkX, int chunkZ, Chunk chunk) {
        if (chunk == null) {
            // Pick out chunk from world
            // TODO: This kills the server
            //chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(chunkX, chunkZ);

            if (chunk == null) {
                FMLLog.warning("Could not load chunk (%s, %s) for RGB color data!", chunkX, chunkZ);
                return;
            }
        }

        coloredlightscore.network.PacketHandler.sendChunkColorData(chunk, player);
    }
}
