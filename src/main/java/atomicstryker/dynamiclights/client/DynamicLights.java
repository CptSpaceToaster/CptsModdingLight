package atomicstryker.dynamiclights.client;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DynamicLights {
    private Minecraft mcinstance;

    private static DynamicLights instance;

    /**
     * Optimization - instead of repeatedly getting the same List for the same World,
     * just check once for World being equal.
     */
    private IBlockAccess lastWorld;
    private ConcurrentLinkedQueue<DynamicLightSourceContainer> lastList;

    /**
     * This Map contains a List of DynamicLightSourceContainer for each World. Since the client can only
     * be in a single World, the other Lists just float idle when unused.
     */
    private ConcurrentHashMap<World, ConcurrentLinkedQueue<DynamicLightSourceContainer>> worldLightsMap;

    /**
     * Keeps track of the toggle button.
     */
    private boolean globalLightsOff;

    /**
     * The Keybinding instance to monitor
     */
    private KeyBinding toggleButton;
    private long nextKeyTriggerTime;


    public void preInit(FMLPreInitializationEvent evt) {
        return;
    }

    public void load(FMLInitializationEvent evt) {
        return;
    }

    public void onTick(TickEvent.ClientTickEvent tick) {
        return;
    }

    /**
     * Used not only to toggle the Lights, but any Ticks in the sub-modules
     * @return true when all computation and tracking should be suspended, false otherwise
     */
    public static boolean globalLightsOff() {
        return false;
    }

    /**
     * Exposed method which is called by the transformed World.computeBlockLightValue method instead of
     * Block.blocksList[blockID].getLightValue. Loops active Dynamic Light Sources and if it finds
     * one for the exact coordinates asked, returns the Light value from that source if higher.
     *
     * @param world World queried
     * @param block Block instance of target coords
     * @param x coordinate queried
     * @param y coordinate queried
     * @param z coordinate queried
     * @return Block.blocksList[blockID].getLightValue or Dynamic Light value, whichever is higher
     */
    public static int getLightValue(IBlockAccess world, Block block, int x, int y, int z) {
        return 0    ;
    }

    /**
     * Exposed method to register active Dynamic Light Sources with. Does all the necessary
     * checks, prints errors if any occur, creates new World entries in the worldLightsMap
     * @param lightToAdd IDynamicLightSource to register
     */
    public static void addLightSource(IDynamicLightSource lightToAdd) {
        return;
    }

    /**
     * Exposed method to remove active Dynamic Light sources with. If it fails for whatever reason,
     * it does so quietly.
     * @param lightToRemove IDynamicLightSource you want removed.
     */
    public static void removeLightSource(IDynamicLightSource lightToRemove) {
        return;
    }
}
