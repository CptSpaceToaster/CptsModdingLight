package kovukore.asm.overriddenclasses;

import kovukore.asm.transformer.ASMAddField;
import kovukore.asm.transformer.ASMReplaceMethod;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class Lights_ExtendedBlockStorage extends ExtendedBlockStorage {
	@ASMAddField
	private NibbleArray rColorArray;
	
	@ASMAddField
    private NibbleArray gColorArray;
	
	@ASMAddField
    private NibbleArray bColorArray;
	
	@ASMReplaceMethod	
	public Lights_ExtendedBlockStorage(int par1, boolean par2)
    {
		super(par1, par2);

		//this.blockLSBArray.length should evaluate to 4096... but we don't have access
        this.rColorArray = new NibbleArray(4096, 4);
        this.gColorArray = new NibbleArray(4096, 4);
        this.bColorArray = new NibbleArray(4096, 4);
    }
	
}
