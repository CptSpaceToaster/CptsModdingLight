package kovukore.asm.transformer.head;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLCallHook;

public class ColoredLightsPatchPreloader implements IFMLCallHook
{
	public File coremodLocation;
	private String modDataJarFile = "modData.jar";
	private String patchFileExtension = ".patch";
	
	public void extract(File location)
	{
		String sourceZipFile = location.getAbsolutePath();
		try
		{
			FileInputStream fin = new FileInputStream(sourceZipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry entry = zin.getNextEntry();
			while ( (entry = zin.getNextEntry()) != null ) 
			{
				if (entry.getName().equals(modDataJarFile))
				{
					OutputStream os = new FileOutputStream(location.getParentFile() + File.separator + modDataJarFile);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = zin.read(buffer)) > 0)
					{
						os.write(buffer, 0, length);
					}
					os.close();
				}
		    }
			zin.close();
		}
		catch (IOException e)
		{
			ColoredLightsLoadingPlugin.log(Level.SEVERE, "A severe problem has occured while reading the patchset:" + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public Void call() throws Exception
	{
		if (coremodLocation != null)
		{
			ColoredLightsLoadingPlugin.log(Level.INFO, "ColoredLightsCore is loading the default patchset");
			extract(coremodLocation);
			File jar = new File(coremodLocation.getParentFile().getAbsolutePath() + File.separator + modDataJarFile);
			loadJarmod(jar);
			ColoredLightsLoadingPlugin.log(Level.INFO, "We have preloaded " + ColoredLightsPatchTransformer.size() + " patches");
			jar.delete();
		}
		return null;
	}
	
	private String makeValidClassName(String toReplace)
	{
		return toReplace.replace(patchFileExtension, new String()).replace('/', '.');
	}

	private void loadJarmod(File file) throws ZipException, IOException
	{
		ZipFile zipFile = new ZipFile(file);
		for (Enumeration<? extends ZipEntry> entr = zipFile.entries(); entr.hasMoreElements();)
		{
			ZipEntry entry = entr.nextElement();
			byte[] bytes = new byte[(int) entry.getSize()];
			DataInputStream dataInputStream = new DataInputStream(zipFile.getInputStream(entry));
			dataInputStream.readFully(bytes);
			ColoredLightsPatchTransformer.put(makeValidClassName(entry.getName()), bytes);
		}
		zipFile.close();
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		if (data.containsKey("coremodLocation"))
		{
			coremodLocation = (File) data.get("coremodLocation");
		}
	}
}