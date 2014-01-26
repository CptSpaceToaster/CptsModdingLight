package kovukore.impl;

import kovukore.asm.transformer.annotation.MethodCreate;
import kovukore.asm.transformer.annotation.MethodReplace;

import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class Transformer_ExtendedBlockStorage extends ExtendedBlockStorage
{
	/* DO NOT use this method */
	public Transformer_ExtendedBlockStorage(int par1, boolean par2)
	{
		super(par1, par2);
	}

	@MethodReplace
	public void func_76677_d(int x, int y, int z, int lightValue)
	{
		this.blocklightArray.set(x, y, z, lightValue);
		this.rColorArray.set(x, y, z, (lightValue >> 5) & 15);
		this.gColorArray.set(x, y, z, (lightValue >> 10) & 15);
		this.bColorArray.set(x, y, z, (lightValue >> 15) & 15);
	}

	@MethodReplace
	public int func_76674_d(int par1, int par2, int par3)
	{
		return (this.blocklightArray.get(par1, par2, par3)) | (this.rColorArray.get(par1, par2, par3) << 5) | (this.gColorArray.get(par1, par2, par3) << 10) | (this.bColorArray.get(par1, par2, par3) << 15);
	}
}