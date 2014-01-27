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

public class Scanner implements IFMLCallHook
{
	public File coremodLocation;

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
				if (entry.getName().equals("modData.jar"))
				{
					OutputStream os = new FileOutputStream(location.getParentFile() + "/modData.jar");
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
			e.printStackTrace();
		}
	}

	@Override
	public Void call() throws Exception
	{
		if (coremodLocation != null)
		{
			log("ColoredLightsCore is loading the default patchset");
			String sep = System.getProperty("file.separator");
			extract(coremodLocation);
			File jar = new File(coremodLocation.getParentFile().getAbsolutePath() + "/modData.jar");
			loadJarmod(jar);
			log("We have preloaded " + Transformer.size() + " patches");
			jar.delete();
		}
		return null;
	}

	private void log(String msg)
	{
		FMLLog.log(Level.INFO, "[ColoredLightsCore] " + msg);
	}

	private void loadJarmod(File file) throws ZipException, IOException
	{
		ZipFile zipFile = new ZipFile(file);
		for (Enumeration<? extends ZipEntry> entr = zipFile.entries(); entr.hasMoreElements();)
		{
			ZipEntry entry = entr.nextElement();
			String className = entry.getName().replace(".patch", "").replace('/', '.');
			byte[] bytes = new byte[(int) entry.getSize()];
			DataInputStream dataInputStream = new DataInputStream(zipFile.getInputStream(entry));
			dataInputStream.readFully(bytes);
			Transformer.put(className, bytes);
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