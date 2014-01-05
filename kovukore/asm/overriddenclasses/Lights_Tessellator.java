package kovukore.asm.overriddenclasses;

import kovukore.asm.transformer.ASMReplaceField;
import kovukore.asm.transformer.ASMReplaceMethod;
import net.minecraft.client.renderer.Tessellator;

/*
 * XXX UPDATE THESE FIELD NAMES AND METHOD NAMES/SIGS ON EACH NEW VERSION OF MINECRAFT
 */
public class Lights_Tessellator extends Tessellator
{
	private boolean p;
	private int l;
	
	@ASMReplaceMethod
    public void c(int par1)
    {
        this.p = true;
        this.l = par1 & 15728880;
    }
}