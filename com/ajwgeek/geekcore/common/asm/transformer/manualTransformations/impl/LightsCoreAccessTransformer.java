package com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.launchwrapper.IClassTransformer;

public class LightsCoreAccessTransformer extends AccessTransformer
{	
	public LightsCoreAccessTransformer()  throws IOException
	{	
		super("kovukore/asm/config/lights_at.cfg");
	}
}