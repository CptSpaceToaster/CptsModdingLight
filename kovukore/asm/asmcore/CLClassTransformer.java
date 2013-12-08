package kovukore.asm.asmcore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import kovukore.asm.overriddenclasses.Lights_Block;
import kovukore.asm.overriddenclasses.Lights_RenderBlocks;
import kovukore.asm.overriddenclasses.Lights_Tessellator;
import kovukore.asm.transformer.ASMClassTransformer;
import net.minecraft.launchwrapper.IClassTransformer;

public class CLClassTransformer implements IClassTransformer
{	
	protected ASMClassTransformer act = null;
	protected HashMap<String, String> classes = null;
	
	public CLClassTransformer()
	{		
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
		addClassNameAndAlias(classes, "net.minecraft.block.Block", "aqz", Lights_Block.class);
		addClassNameAndAlias(classes, "net.minecraft.client.renderer.RenderBlocks", "bfr", Lights_RenderBlocks.class);
		addClassNameAndAlias(classes, "net.minecraft.client.renderer.Tessellator", "bfq", Lights_Tessellator.class);
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
