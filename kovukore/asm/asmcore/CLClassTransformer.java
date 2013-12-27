package kovukore.asm.asmcore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kovukore.asm.overriddenclasses.Lights_Block;
import kovukore.asm.overriddenclasses.Lights_ChunkCache;
import kovukore.asm.overriddenclasses.Lights_ExtendedBlockStorage;
import kovukore.asm.overriddenclasses.Lights_RenderBlocks;
import kovukore.asm.overriddenclasses.Lights_Tessellator;
import kovukore.asm.overriddenclasses.Lights_World;
import kovukore.asm.transformer.ASMClassTransformer;
import net.minecraft.launchwrapper.IClassTransformer;

public class CLClassTransformer extends AccessTransformer implements IClassTransformer
{	
	protected ASMClassTransformer act = null;
	protected HashMap<String, String> classes = null;
	
	public CLClassTransformer()  throws IOException
	{	
		//Do access transforming first
		super("kovukore/asm/config/lights_at.cfg");
		classes = createClassesToTransform();
		//Transform classes
		act = new ASMClassTransformer(classes, new HashMap<String, String>(), new HashMap<String, String>());
	}
	
	protected HashMap<String, String> createClassesToTransform()
	{
		HashMap<String, String> classes = new HashMap<String, String>();
		this.addClasses(classes);
		return classes;
	}
	
	public void addClasses(HashMap classes)
	{
		//Apparently getSimpleName() does return the right class
		addClassNameAndAlias(classes, "net.minecraft.block.Block", net.minecraft.block.Block.class.getSimpleName(), Lights_Block.class);
		addClassNameAndAlias(classes, "net.minecraft.world.ChunkCache", net.minecraft.world.ChunkCache.class.getSimpleName(), Lights_ChunkCache.class);
		addClassNameAndAlias(classes, "net.minecraft.world.chunk.storage.ExtendedBlockStorage", net.minecraft.world.chunk.storage.ExtendedBlockStorage.class.getSimpleName(), Lights_ExtendedBlockStorage.class);
		addClassNameAndAlias(classes, "net.minecraft.world.World", net.minecraft.world.World.class.getSimpleName(), Lights_World.class);
		addClassNameAndAlias(classes, "net.minecraft.client.renderer.RenderBlocks", net.minecraft.client.renderer.RenderBlocks.class.getSimpleName(), Lights_RenderBlocks.class);
		addClassNameAndAlias(classes, "net.minecraft.client.renderer.Tessellator", net.minecraft.client.renderer.Tessellator.class.getSimpleName(), Lights_Tessellator.class);
	}
	
	protected void addClassNameAndAlias(HashMap<String, String> map, String className, String obfName, Class clss)
	{
		if(obfName != null)
		{
			map.put(obfName, clss.getName());
		}
		else
		{
			map.put(className, clss.getName());
		}
	}
	
	@Override
	public byte[] transform(String s, String arg1, byte[] bytes)
	{
		//Exclude any of our own code. We CANNOT transform our transformer. Also exclude any class we don't want to transform
		if(s.startsWith("ASM") || s.startsWith("kovukore") || s.startsWith("yamhaven") || !classes.containsKey(s))
		{
			return bytes;
		}
		else
		{
			return act.transform(s, bytes);
		}
	}
}