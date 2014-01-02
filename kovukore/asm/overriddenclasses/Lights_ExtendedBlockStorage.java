package kovukore.asm.overriddenclasses;

import kovukore.asm.transformer.ASMAddField;
import kovukore.asm.transformer.ASMAddMethod;
import kovukore.asm.transformer.ASMReplaceMethod;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

/*
 * XXX UPDATE THESE FIELD NAMES AND METHOD NAMES/SIGS ON EACH NEW VERSION OF MINECRAFT
 */
public class Lights_ExtendedBlockStorage extends ExtendedBlockStorage
{
	@ASMAddField
	private NibbleArray rColorArray;
	@ASMAddField
	private NibbleArray gColorArray;
	@ASMAddField
	private NibbleArray bColorArray;

	@ASMAddMethod
	public Lights_ExtendedBlockStorage(int par1, boolean par2)
    {
		super(par1, par2);
		this.rColorArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.gColorArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.bColorArray = new NibbleArray(this.blockLSBArray.length, 4);
    }
	
	@ASMReplaceMethod
	public void setExtBlocklightValue(int x, int y, int z, int lightValue)
    {
        this.blocklightArray.set(x, y, z, lightValue);
        this.rColorArray.set(x, y, z, (lightValue>>5) &15);
        this.gColorArray.set(x, y, z, (lightValue>>10)&15);
        this.bColorArray.set(x, y, z, (lightValue>>15)&15);
    }
	
	@ASMReplaceMethod
	public int d(int par1, int par2, int par3)
    {
    	return 	(this.blocklightArray.get(par1, par2, par3)) | 
        		(this.rColorArray.get(par1, par2, par3)<<5)  |
        		(this.gColorArray.get(par1, par2, par3)<<10) |
        		(this.bColorArray.get(par1, par2, par3)<<15) ;
    }
}