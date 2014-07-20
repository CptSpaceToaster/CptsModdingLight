package yamhaven.easycoloredlights.blocks;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import yamhaven.easycoloredlights.items.ItemCLBlock;
import yamhaven.easycoloredlights.items.CLDust;
import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;
import cpw.mods.fml.common.registry.GameRegistry;

public class CLBlocksController {
    public static CLLamp CLBlockIdle;
    public static CLLamp CLBlockOn;
    public static CLStone CLStone;
    public static CLDust CLDust;
    
    public static void init() {
        CLBlockIdle = (CLLamp) new CLLamp(false).setBlockName(BlockInfo.CLLamp);
        CLBlockOn = (CLLamp) new CLLamp(true).setBlockName(BlockInfo.CLLamp + "On");
        CLStone = (CLStone) new CLStone().setBlockName(BlockInfo.CLStone);
        CLDust = (CLDust) new CLDust().setUnlocalizedName(BlockInfo.CLDust);
        
        CLBlockIdle.setSwitchBlock(CLBlockOn);
        CLBlockOn.setSwitchBlock(CLBlockIdle);
    }

    public static void registerBlocks() {
        GameRegistry.registerBlock(CLBlockIdle, ItemCLBlock.class, ModInfo.ID + BlockInfo.CLLamp);
        GameRegistry.registerBlock(CLBlockOn,   ItemCLBlock.class, ModInfo.ID + BlockInfo.CLLamp + "On");
        GameRegistry.registerBlock(CLStone,     ItemCLBlock.class, ModInfo.ID + BlockInfo.CLStone);
        GameRegistry.registerItem(CLDust, ModInfo.ID + BlockInfo.CLDust);
    }

    public static void addBlockRecipes() {
        for (int i = 0; i < 16; i++) {
            GameRegistry.addRecipe(new ItemStack(CLDust, 4, i), " s ", "sds", " s ",
                    's', Items.glowstone_dust,
                    'd', new ItemStack(Items.dye, 1, i)
            );
            
            GameRegistry.addShapelessRecipe(new ItemStack(CLStone, 1, i),
                    new ItemStack(CLDust, 1, i),
                    new ItemStack(CLDust, 1, i),
                    new ItemStack(CLDust, 1, i),
                    new ItemStack(CLDust, 1, i)
            );
            
            GameRegistry.addRecipe(new ItemStack(CLBlockIdle, 8, i), " r ", "rsr", " r ",
                    'r', Items.redstone,
                    's', new ItemStack(CLStone, 8, i)
            );
        }
    }
}
