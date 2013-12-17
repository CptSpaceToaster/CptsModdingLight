package kovukore.asm.overriddenclasses;

import kovukore.asm.transformer.ASMAddField;
import kovukore.asm.transformer.ASMReplaceMethod;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class Lights_ExtendedBlockStorage extends ExtendedBlockStorage {
	public Lights_ExtendedBlockStorage(int par1, boolean par2) {
		super(par1, par2);
	}
	private int yBase;
	private byte[] blockLSBArray;
	private NibbleArray blockMetadataArray;
	private NibbleArray blocklightArray;
	private NibbleArray skylightArray;
	
	
	
	@ASMAddField
	private NibbleArray rColorArray;
	
	@ASMAddField
    private NibbleArray gColorArray;
	
	@ASMAddField
    private NibbleArray bColorArray;
	
	//TODO: Replace the constructor
	public ExtendedBlockStorage(int par1, boolean par2)
    {
        this.yBase = par1;
        this.blockLSBArray = new byte[4096];
        this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.blocklightArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.rColorArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.gColorArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.bColorArray = new NibbleArray(this.blockLSBArray.length, 4);

        if (par2)
        {
            this.skylightArray = new NibbleArray(this.blockLSBArray.length, 4);
        }
    }
}
