package coloredlightscore.src.types;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class CLDynamicTexture2D extends DynamicTexture {
    public CLDynamicTexture2D(int par1, int par2) {
        super(par1, par2);
        
        int[] dataPointer = this.getTextureData();
        for (int i=0; i < par1; i++) {
            for (int j=0; i < par1; i++) {
                dataPointer[i<<4 | j] = 0xFFFFFFFF;
            }
        }
        
        FMLLog.info("Generated a new TextureID: " + this.getGlTextureId());
        this.updateDynamicTexture();
    }
}
