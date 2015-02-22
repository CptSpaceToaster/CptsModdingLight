package coloredlightscore.src.asm;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;
import java.util.Arrays;
import java.util.Iterator;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import coloredlightscore.fmlevents.ChunkDataEventHandler;
import coloredlightscore.network.PacketHandler;
import coloredlightscore.src.api.CLApi;
import coloredlightscore.src.helper.CLEntityRendererHelper;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ColoredLightsCoreDummyContainer extends DummyModContainer {
    public ChunkDataEventHandler chunkDataEventHandler;

    // This is picked up and replaced by the build.gradle
    public static final String version = "@VERSION@";

    public ColoredLightsCoreDummyContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "coloredlightscore";
        meta.name = "Colored Lights Core";
        meta.version = version;
        meta.logoFile = "/mod_ColoredLightCore.logo.png";
        meta.url = "http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/1445251-1-7-10-beta-wip-colored-light-progress-and";
        meta.credits = "";
        meta.authorList = Arrays.asList("heaton84", "Murray65536", "Kovu", "Biggerfisch", "CptSpaceToaster");
        meta.description = "The coremod for Colored Lights";
        meta.useDependencyInformation = true;
        chunkDataEventHandler = new ChunkDataEventHandler();
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);

        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent evt) {

        CLLog = evt.getModLog();

        CLLog.info("Starting up ColoredLightsCore");

        // Spin up network handler
        PacketHandler.init();

        // Hook into chunk events
        MinecraftForge.EVENT_BUS.register(chunkDataEventHandler);

    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent evt) {
        if (evt.getSide() == Side.CLIENT) {
            CLEntityRendererHelper.Initialize();
        }

        // Inject RGB values into vanilla blocks		
        Blocks.lava.lightValue = CLApi.makeRGBLightValue(15, 10, 0);

        Blocks.flowing_lava.lightValue = CLApi.makeRGBLightValue(15, 10, 0);
        Blocks.torch.lightValue = CLApi.makeRGBLightValue(14, 13, 10);
        Blocks.fire.lightValue = CLApi.makeRGBLightValue(15, 13, 0);
        Blocks.lit_redstone_ore.lightValue = CLApi.makeRGBLightValue(9, 0, 0);
        Blocks.redstone_torch.lightValue = CLApi.makeRGBLightValue(7, 0, 0);
        Blocks.portal.lightValue = CLApi.makeRGBLightValue(6, 3, 11);
        Blocks.lit_furnace.lightValue = CLApi.makeRGBLightValue(13, 12, 10);
        Blocks.powered_repeater.lightValue = CLApi.makeRGBLightValue(9, 0, 0);
    }
}