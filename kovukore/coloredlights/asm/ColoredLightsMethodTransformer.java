package kovukore.coloredlights.asm;

import java.util.HashMap;

import com.ajwgeek.geekcore.common.asm.transformer.annotation.MethodTransformer;

import net.minecraft.launchwrapper.IClassTransformer;

public class ColoredLightsMethodTransformer implements IClassTransformer
{
	protected MethodTransformer act = null;
	protected HashMap<String, String> classes = new HashMap<String, String>();

	public ColoredLightsMethodTransformer()
	{
		addClassNameAndAlias(classes, "net.minecraft.block.Block", "aqz", "com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_Block");
		addClassNameAndAlias(classes, "net.minecraft.world.ChunkCache", "acl", "com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_ChunkCache");
		addClassNameAndAlias(classes, "net.minecraft.world.chunk.storage.ExtendedBlockStorage", "ads", "com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_ExtendedBlockStorage");
		addClassNameAndAlias(classes, "net.minecraft.world.World", "abw", "com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_World");
		addClassNameAndAlias(classes, "net.minecraft.client.renderer.RenderBlocks", "bfr", "com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_RenderBlocks");
		addClassNameAndAlias(classes, "net.minecraft.client.renderer.Tessellator", "bfq", "com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_Tessellator");
		act = new MethodTransformer(classes, new HashMap<String, String>());
	}

	protected void addClassNameAndAlias(HashMap<String, String> transformerMap, String className, String obfuscatedName, String tfClazzName)
	{
		if (obfuscatedName != null)
		{
			System.out.println("Marked " + obfuscatedName + " For transformation against " + tfClazzName);
			transformerMap.put(obfuscatedName, tfClazzName);	
		}
		else
		{
			System.out.println("Marked " + className + " For transformation against " + tfClazzName);
			transformerMap.put(className, tfClazzName);
		}
	}

	@Override
	public byte[] transform(String s, String arg1, byte[] bytes)
	{
		arg1 = arg1.replace('/', '.');
		if (classes.containsKey(s)) {
			System.out.println("IT'S A DEBUG: " + s + " - " + arg1);
			return act.transform(s, bytes);
		} else { 
			return bytes;
		}
	}
}