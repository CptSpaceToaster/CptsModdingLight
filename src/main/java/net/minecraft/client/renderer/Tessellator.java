package net.minecraft.client.renderer;

/**
 * Dummy class to enable access to runtime-added methods
 */
public class Tessellator {
    public static Tessellator instance;
    public boolean hasBrightness;
    public int brightness;
    public int rawBufferIndex;
    public int[] rawBuffer;
    public int addedVertices;
    public boolean hasTexture;
    public double textureU;
    public double textureV;
    public boolean hasColor;
    public int color;
    public boolean hasNormals;
    public int normal;
    public double xOffset;
    public double yOffset;
    public double zOffset;
    public int vertexCount;
    // Added by TransformTessellator
    public static int clProgram;
    public static int clTexCoordAttribute;

    // Added by TransformTessellator
    public int getRawBufferSize() { return 0; }

    // Added by TransformTessellator
    public void setRawBufferSize(int in) {}

    public void setBrightness(int brightness) {}

    public void setColorOpaque_F(float f10, float f13, float f16) {}

    public void addVertexWithUV(double x, double y, double z, double u, double v) {}

    public void startDrawing(int drawMode) {}

    public int draw() {
        return 0;
    }
}
