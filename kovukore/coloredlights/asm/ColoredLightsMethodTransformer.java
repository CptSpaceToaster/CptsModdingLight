package kovukore.coloredlights.asm;

import java.util.HashMap;

import com.ajwgeek.geekcore.common.asm.transformer.annotation.MethodTransformer;
import com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_Block;
import com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_ChunkCache;
import com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_ExtendedBlockStorage;
import com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_RenderBlocks;
import com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_Tessellator;
import com.ajwgeek.geekcore.common.asm.transformer.annotation.impl.Transformer_World;

import net.minecraft.launchwrapper.IClassTransformer;

public class ColoredLightsMethodTransformer implements IClassTransformer
{
	protected MethodTransformer act = null;
	protected HashMap<String, String> classes = null;

	public ColoredLightsMethodTransformer()
	{
		addClassNameAndAlias(classes, "net.minecraft.block.Block", net.minecraft.block.Block.class.getSimpleName(), Transformer_Block.class);
		addClassNameAndAlias(classes, "net.minecraft.world.ChunkCache", net.minecraft.world.ChunkCache.class.getSimpleName(), Transformer_ChunkCache.class);
		addClassNameAndAlias(classes, "net.minecraft.world.chunk.storage.ExtendedBlockStorage", net.minecraft.world.chunk.storage.ExtendedBlockStorage.class.getSimpleName(), Transformer_ExtendedBlockStorage.class);
		addClassNameAndAlias(classes, "net.minecraft.world.World", net.minecraft.world.World.class.getSimpleName(), Transformer_World.class);
		addClassNameAndAlias(classes, "net.minecraft.client.renderer.RenderBlocks", net.minecraft.client.renderer.RenderBlocks.class.getSimpleName(), Transformer_RenderBlocks.class);
		addClassNameAndAlias(classes, "net.minecraft.client.renderer.Tessellator", net.minecraft.client.renderer.Tessellator.class.getSimpleName(), Transformer_Tessellator.class);
		act = new MethodTransformer(classes, new HashMap<String, String>());
	}

	protected void addClassNameAndAlias(HashMap<String, String> transformerMap, String className, String obfuscatedName, Class clazz)
	{
		if (obfuscatedName != null)
		{
			transformerMap.put(obfuscatedName, clazz.getName());
		}
		else
		{
			transformerMap.put(className, clazz.getName());
		}
	}

	@Override
	public byte[] transform(String s, String arg1, byte[] bytes)
	{
		return act.transform(s, bytes);
	}
}