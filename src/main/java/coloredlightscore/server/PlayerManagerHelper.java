package coloredlightscore.server;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;

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
        //CLLog.info("Server just sent chunk ({}, {}) to player {}", chunkLocation.chunkXPos, chunkLocation.chunkZPos, player.getDisplayName());

        // TODO: Load chunk from server
        //sendChunkRGBDataToPlayer(player, chunkLocation.chunkXPos, chunkLocation.chunkZPos, null);
    }

    public static void entityPlayerMP_onUpdate(ArrayList<Chunk> chunks, EntityPlayerMP player) {
        for (Chunk c : chunks) {
            //CLLog.info("S26: Server just sent chunk ({}, {}) to player {}", c.xPosition, c.zPosition, player.getDisplayName());

            sendChunkRGBDataToPlayer(player, c.xPosition, c.zPosition, c);
        }
    }

    public static void sendChunkRGBDataToPlayer(EntityPlayerMP player, int chunkX, int chunkZ, Chunk chunk) {
        if (chunk == null) {
            // Pick out chunk from world
            // TODO: This kills the server
            //chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(chunkX, chunkZ);

            if (chunk == null) {
                CLLog.warn("Could not load chunk ({}, {}) for RGB color data!", chunkX, chunkZ);
                return;
            }
        }

        coloredlightscore.network.PacketHandler.sendChunkColorData(chunk, player);
    }
}
