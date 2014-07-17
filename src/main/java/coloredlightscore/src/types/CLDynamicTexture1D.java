package coloredlightscore.src.types;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CLDynamicTexture1D extends DynamicTexture {

    public int[] dynamicTextureData; 

    /* width of this icon in pixels */
    public final int width;
    /* TextureID */
    public int myID = -1;

    /*
     * I sure hope no one wants this... public CLDynamicTexture3D(BufferedImage
     * par1BufferedImage) { this(par1BufferedImage.getWidth(),
     * par1BufferedImage.getHeight()); par1BufferedImage.getRGB(0, 0,
     * par1BufferedImage.getWidth(), par1BufferedImage.getHeight(),
     * this.dynamicTextureData, 0, par1BufferedImage.getWidth());
     * this.updateDynamicTexture(); }
     */

    public CLDynamicTexture1D(int par1) {
        super(par1, 1); // Hopefully we can fix what this does after it's all messed up! ._.
        this.width = par1;
        this.dynamicTextureData = new int[par1]; // This was duplicated :< ooops
        allocateTextureImpl(this.getGlTextureId(), 0, par1, 1.0F);
    }
    
    @Override
    public int getGlTextureId() {
        if (this.myID == -1) {
            this.myID = GL11.glGenTextures();
            System.out.println("Generated a new TextureID: " + myID);
        }

        return this.myID;
    }

    public static void allocateTextureImpl(int textureID, int mipmapLevel, int pWidth, float anUnusedFloatFromDynamicTexture) {
        //GL11.glDeleteTextures(textureID);
        GL11.glBindTexture(GL11.GL_TEXTURE_1D, textureID);

        /*
         * Looks to be some sort of wizardry for anisotropic filtering on all 2D
         * textures... Not sure if I need this... if
         * (OpenGlHelper.anisotropicFilteringSupported) {
         * GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 34046, p_147946_4_); }
         */

        if (mipmapLevel > 0) {
            GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_MAX_LEVEL, mipmapLevel);
            GL11.glTexParameterf(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_MIN_LOD, 0.0F);
            GL11.glTexParameterf(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_MAX_LOD, (float) mipmapLevel);
            GL11.glTexParameterf(GL12.GL_TEXTURE_3D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);
        }

        GL11.glTexImage1D(GL11.GL_TEXTURE_1D, mipmapLevel, GL11.GL_RGBA/* 8bit per chan */, pWidth, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
                TextureUtil.dataBuffer);

    }

    /* Dynamic Texture had this... I don't even... */
    public void loadTexture(IResourceManager par1ResourceManager) {
    }

    public void updateDynamicTexture() {
        GL11.glBindTexture(GL11.GL_TEXTURE_1D, this.getGlTextureId());

        GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        // Texture wrapping I shouldn't need these, but we'll leave it in for
        // kicks...
        GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL12.GL_TEXTURE_WRAP_R, GL11.GL_REPEAT);

        /*
         * This probably won't support 3D texture anyway int[] aint1 =
         * par0ArrayOfInteger;
         * 
         * if (Minecraft.getMinecraft().gameSettings.anaglyph) { aint1 =
         * TextureUtil.updateAnaglyph(par0ArrayOfInteger); }
         */

        // I don't think I need to slice this up, because I can just generate
        // the texture in the right order back in EntityRenderer
        int offset = 16*16*16;
        int textureLength = this.width;
        
        /* This is probably overwriting the other texture */
        TextureUtil.dataBuffer.clear();
        TextureUtil.dataBuffer.put(this.dynamicTextureData, 0, textureLength + offset);
        TextureUtil.dataBuffer.position(0).limit(textureLength);

        int mipmapLevel = 0;
        GL11.glTexImage1D(GL11.GL_TEXTURE_1D, mipmapLevel, GL11.GL_RGBA/* 8bit per chan */, width, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
                TextureUtil.dataBuffer);
    }
}