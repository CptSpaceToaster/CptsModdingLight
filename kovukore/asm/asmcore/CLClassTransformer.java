package kovukore.asm.asmcore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;
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
		// Do Access Transforming First
		super("kovukore/asm/config/lights_at.cfg");
		
		// Do Class Replacement second
		classes = createClassesToTransform();
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
		System.out.println("Patching all classes");
		addClassNameAndAlias(classes, "net.minecraft.block.Block", "aqz", Lights_Block.class);
		addClassNameAndAlias(classes, "net.minecraft.world.chunk.ChunkCache", "acl", Lights_ChunkCache.class);
		addClassNameAndAlias(classes, "net.minecraft.world.chunk.storage.ExtendedBlockStorage", "ads", Lights_ExtendedBlockStorage.class);
		addClassNameAndAlias(classes, "net.minecraft.client.renderer.RenderBlocks", "bfr", Lights_RenderBlocks.class);
		addClassNameAndAlias(classes, "net.minecraft.client.renderer.Tessellator", "bfq", Lights_Tessellator.class);
		addClassNameAndAlias(classes, "net.minecraft.world.World", "abw", Lights_World.class);
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
		if(s.startsWith("ASM") || !classes.containsKey(s))
		{
			return bytes;
		}
		else
		{
			System.out.println("Patching: " + s + ", " + arg1);
			return act.transform(s, bytes);
		}
	}
}
