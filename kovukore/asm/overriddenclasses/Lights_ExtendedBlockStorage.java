package kovukore.asm.overriddenclasses;

import kovukore.asm.transformer.ASMAddField;
import kovukore.asm.transformer.ASMReplaceMethod;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class Lights_ExtendedBlockStorage extends ExtendedBlockStorage
{
	@ASMAddField
	private NibbleArray rColorArray = new NibbleArray(4096, 4);
	@ASMAddField
	private NibbleArray gColorArray = new NibbleArray(4096, 4);
	@ASMAddField
	private NibbleArray bColorArray = new NibbleArray(4096, 4);

	public Lights_ExtendedBlockStorage(int par1, boolean par2)
    {
		super(par1, par2);
    }
}