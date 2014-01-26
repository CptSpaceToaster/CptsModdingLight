package kovukore.impl;

import kovukore.asm.transformer.annotation.MethodCreate;
import kovukore.asm.transformer.annotation.MethodReplace;

import cpw.mods.fml.common.registry.BlockProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class Transformer_Block extends Block
{
	/* DO NOT use this method */
	public Transformer_Block(int par1, Material par2Material)
	{
		super(par1, par2Material);
	}
	
	public Block addColorLightValue(float r, float g, float b)
	{
		lightValue[this.blockID] &= 15;
		lightValue[this.blockID] |= ((((int) (15.0F * b)) << 15) + (((int) (15.0F * g)) << 10) + (((int) (15.0F * r)) << 5));
		return this;
	}
	
	public Block func_71900_a(float par1)
	{
		lightValue[this.blockID] = (int) (15.0F * par1);
		return this.addColorLightValue(par1, par1, par1);
	}
}