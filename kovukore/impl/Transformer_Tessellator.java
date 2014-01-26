package kovukore.impl;

import kovukore.asm.transformer.annotation.MethodReplace;

import net.minecraft.client.renderer.Tessellator;

public class Transformer_Tessellator extends Tessellator
{
	private boolean field_78414_p;
	private int field_78401_l;

	@MethodReplace
	public void func_78380_c(int par1)
	{
		this.field_78414_p = true;
		this.field_78401_l = par1 & 15728880;
	}
}