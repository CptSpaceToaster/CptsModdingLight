package kovukore.asm.overriddenclasses;

import kovukore.asm.transformer.ASMReplaceField;
import kovukore.asm.transformer.ASMReplaceMethod;
import net.minecraft.client.renderer.Tessellator;

public class Lights_Tessellator extends Tessellator
{
	private boolean hasBrightness;
	private int brightness;
	
	@ASMReplaceMethod
    public void setBrightness(int par1)
    {
        this.hasBrightness = true;
        
        /** 
         * First: 								0000SSSS0000000000000000LLLL0000
         * Old: 								0000SSSS0000BBBBGGGGRRRRLLLL0000
         * Refactor: 							0000SSSS0BBBB0GGGG0RRRR0LLLL0000
         * 
         * Takes the lightValue in the Form 	000) 0000 SSSS BBBB GGGG RRRR LLLL 0000
         * and formats it to the expected form: 0000 0000 SSSS 0000 0000 0000 LLLL 0000
         * 
         * CptSpaceToaster
         */
        this.brightness = par1 & 15728880;
    }

}
