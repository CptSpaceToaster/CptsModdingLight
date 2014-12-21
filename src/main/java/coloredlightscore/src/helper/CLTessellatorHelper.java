package coloredlightscore.src.helper;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.*;

public class CLTessellatorHelper {

    //private static int nativeBufferSize = 0x200000;
    public static int texCoordParam;
    public static int lightCoordParam;
    public static int clProgram;
    private static boolean programInUse;
    public static int lightCoordUniform;
    private static IntBuffer cachedLightCoord;
    private static int cachedShader;
    private static boolean hasFlaggedOpenglError;
    private static int lastGLErrorCode = GL11.GL_NO_ERROR;

    static {
        cachedLightCoord = ByteBuffer.allocateDirect(16).asIntBuffer();
        cachedShader = 0;
        hasFlaggedOpenglError = false;
    }

    public CLTessellatorHelper() {
    }

    public static void setBrightness(Tessellator instance, int par1) {
        instance.hasBrightness = true;
        instance.brightness = par1;
    }

    public static void setupShaders() {
        int vertShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        int fragShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(vertShader, "#version 120\n" +
                "uniform sampler2D Texture;" +
                "uniform sampler2D LightMap;" +
                "uniform vec4 u_LightCoord;" +
                "attribute vec2 TexCoord;" +
                "varying vec2 p_TexCoord;" +
                "attribute vec4 LightCoord;" +
                "varying vec4 p_LightCoord;" +
                "varying vec4 p_Color;" +
                "void main() {" +
                    "gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;" +
                    "p_TexCoord = TexCoord;" +
                    "p_Color = gl_Color;" +
                    "if (u_LightCoord == vec4(0.0, 0.0, 0.0, 0.0)) {" +
                        "p_LightCoord = LightCoord;" +
                    "} else {" +
                        "p_LightCoord = u_LightCoord;" +
                    "}" +
                "}");
        GL20.glShaderSource(fragShader, "#version 120\n" +
                "uniform sampler2D Texture;" +
                "uniform sampler2D LightMap;" +
                "varying vec2 p_TexCoord;" +
                "varying vec4 p_Color;" +
                "varying vec4 p_LightCoord;" +
                "uniform vec4 u_LightCoord;" +
                "void main() {" +
                    "float scale = 256;" +
                    "float bias = 0.5;" +
                    "vec4 texel0000 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 0)) * 16 + (floor(p_LightCoord.zw + vec2(0, 0)) + bias)) / scale, 0, 1));" +
                    "vec4 texel0001 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 0)) * 16 + (floor(p_LightCoord.zw + vec2(0, 1)) + bias)) / scale, 0, 1));" +
                    "vec4 texel0010 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 0)) * 16 + (floor(p_LightCoord.zw + vec2(1, 0)) + bias)) / scale, 0, 1));" +
                    "vec4 texel0011 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 0)) * 16 + (floor(p_LightCoord.zw + vec2(1, 1)) + bias)) / scale, 0, 1));" +
                    "vec4 texel0100 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 1)) * 16 + (floor(p_LightCoord.zw + vec2(0, 0)) + bias)) / scale, 0, 1));" +
                    "vec4 texel0101 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 1)) * 16 + (floor(p_LightCoord.zw + vec2(0, 1)) + bias)) / scale, 0, 1));" +
                    "vec4 texel0110 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 1)) * 16 + (floor(p_LightCoord.zw + vec2(1, 0)) + bias)) / scale, 0, 1));" +
                    "vec4 texel0111 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(0, 1)) * 16 + (floor(p_LightCoord.zw + vec2(1, 1)) + bias)) / scale, 0, 1));" +
                    "vec4 texel1000 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 0)) * 16 + (floor(p_LightCoord.zw + vec2(0, 0)) + bias)) / scale, 0, 1));" +
                    "vec4 texel1001 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 0)) * 16 + (floor(p_LightCoord.zw + vec2(0, 1)) + bias)) / scale, 0, 1));" +
                    "vec4 texel1010 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 0)) * 16 + (floor(p_LightCoord.zw + vec2(1, 0)) + bias)) / scale, 0, 1));" +
                    "vec4 texel1011 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 0)) * 16 + (floor(p_LightCoord.zw + vec2(1, 1)) + bias)) / scale, 0, 1));" +
                    "vec4 texel1100 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 1)) * 16 + (floor(p_LightCoord.zw + vec2(0, 0)) + bias)) / scale, 0, 1));" +
                    "vec4 texel1101 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 1)) * 16 + (floor(p_LightCoord.zw + vec2(0, 1)) + bias)) / scale, 0, 1));" +
                    "vec4 texel1110 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 1)) * 16 + (floor(p_LightCoord.zw + vec2(1, 0)) + bias)) / scale, 0, 1));" +
                    "vec4 texel1111 = texture2D(LightMap, clamp((floor(p_LightCoord.xy + vec2(1, 1)) * 16 + (floor(p_LightCoord.zw + vec2(1, 1)) + bias)) / scale, 0, 1));" +
                    "vec4 lightColor = texel0000 * (1 - fract(p_LightCoord.x)) * (1 - fract(p_LightCoord.y)) * (1 - fract(p_LightCoord.z)) * (1 - fract(p_LightCoord.w)) +" +
                                      "texel0001 * (1 - fract(p_LightCoord.x)) * (1 - fract(p_LightCoord.y)) * (1 - fract(p_LightCoord.z)) * fract(p_LightCoord.w) +" +
                                      "texel0010 * (1 - fract(p_LightCoord.x)) * (1 - fract(p_LightCoord.y)) * fract(p_LightCoord.z) * (1 - fract(p_LightCoord.w)) +" +
                                      "texel0011 * (1 - fract(p_LightCoord.x)) * (1 - fract(p_LightCoord.y)) * fract(p_LightCoord.z) * fract(p_LightCoord.w) +" +
                                      "texel0100 * (1 - fract(p_LightCoord.x)) * fract(p_LightCoord.y) * (1 - fract(p_LightCoord.z)) * (1 - fract(p_LightCoord.w)) +" +
                                      "texel0101 * (1 - fract(p_LightCoord.x)) * fract(p_LightCoord.y) * (1 - fract(p_LightCoord.z)) * fract(p_LightCoord.w) +" +
                                      "texel0110 * (1 - fract(p_LightCoord.x)) * fract(p_LightCoord.y) * fract(p_LightCoord.z) * (1 - fract(p_LightCoord.w)) +" +
                                      "texel0111 * (1 - fract(p_LightCoord.x)) * fract(p_LightCoord.y) * fract(p_LightCoord.z) * fract(p_LightCoord.w) +" +
                                      "texel1000 * fract(p_LightCoord.x) * (1 - fract(p_LightCoord.y)) * (1 - fract(p_LightCoord.z)) * (1 - fract(p_LightCoord.w)) +" +
                                      "texel1001 * fract(p_LightCoord.x) * (1 - fract(p_LightCoord.y)) * (1 - fract(p_LightCoord.z)) * fract(p_LightCoord.w) +" +
                                      "texel1010 * fract(p_LightCoord.x) * (1 - fract(p_LightCoord.y)) * fract(p_LightCoord.z) * (1 - fract(p_LightCoord.w)) +" +
                                      "texel1011 * fract(p_LightCoord.x) * (1 - fract(p_LightCoord.y)) * fract(p_LightCoord.z) * fract(p_LightCoord.w) +" +
                                      "texel1100 * fract(p_LightCoord.x) * fract(p_LightCoord.y) * (1 - fract(p_LightCoord.z)) * (1 - fract(p_LightCoord.w)) +" +
                                      "texel1101 * fract(p_LightCoord.x) * fract(p_LightCoord.y) * (1 - fract(p_LightCoord.z)) * fract(p_LightCoord.w) +" +
                                      "texel1110 * fract(p_LightCoord.x) * fract(p_LightCoord.y) * fract(p_LightCoord.z) * (1 - fract(p_LightCoord.w)) +" +
                                      "texel1111 * fract(p_LightCoord.x) * fract(p_LightCoord.y) * fract(p_LightCoord.z) * fract(p_LightCoord.w);" +
                    "gl_FragColor = texture2D(Texture, p_TexCoord) * p_Color * lightColor;" +
                "}");
        GL20.glCompileShader(vertShader);
        GL20.glCompileShader(fragShader);
        if (GL11.glGetError() != GL11.GL_NO_ERROR) {
            CLLog.error("Error compiling shaders");
        }

        clProgram = GL20.glCreateProgram();
        GL20.glAttachShader(clProgram, vertShader);
        GL20.glAttachShader(clProgram, fragShader);
        if (GL11.glGetError() != GL11.GL_NO_ERROR) {
            CLLog.error("Error attaching shaders");
        }

        GL20.glLinkProgram(clProgram);
        if (GL11.glGetError() != GL11.GL_NO_ERROR) {
            CLLog.error("Error linking program");
        }

        GL20.glDetachShader(clProgram, vertShader);
        GL20.glDetachShader(clProgram, fragShader);
        if (GL11.glGetError() != GL11.GL_NO_ERROR) {
            CLLog.error("Error detaching shaders");
        }

        GL20.glValidateProgram(clProgram);
        if (GL11.glGetError() != GL11.GL_NO_ERROR) {
            CLLog.error("Error validating program");
        }
        texCoordParam = GL20.glGetAttribLocation(clProgram, "TexCoord");
        lightCoordParam = GL20.glGetAttribLocation(clProgram, "LightCoord");
        lightCoordUniform = GL20.glGetUniformLocation(clProgram, "u_LightCoord");

        if (texCoordParam <= 0) {
            CLLog.error("texCoordParam attribute location returned: " + texCoordParam);
        }
        if (lightCoordParam <= 0) {
            CLLog.error("lightCoordParam attribute location returned: " + lightCoordParam);
        }
        if (lightCoordUniform <= 0) {
            CLLog.error("lightCoordUniform attribute location returned: " + lightCoordUniform);
        }
    }

    public static void enableShader() {
        cachedShader = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        GL20.glUseProgram(clProgram);
        programInUse = true;
        int textureUniform = GL20.glGetUniformLocation(clProgram, "Texture");
        GL20.glUniform1i(textureUniform, OpenGlHelper.defaultTexUnit - GL13.GL_TEXTURE0);
        int lightmapUniform = GL20.glGetUniformLocation(clProgram, "LightMap");
        GL20.glUniform1i(lightmapUniform, OpenGlHelper.lightmapTexUnit - GL13.GL_TEXTURE0);
    }

    public static void disableShader() {
        programInUse = false;
        GL20.glUseProgram(cachedShader);
    }

    public static void setTextureCoord(FloatBuffer buffer) {
        lastGLErrorCode = GL11.glGetError();
        if (lastGLErrorCode != GL11.GL_NO_ERROR) {
            if (!hasFlaggedOpenglError) {
                CLLog.warn("Render error entering CLTessellatorHelper.setTextureCoord()! Error Code: " + lastGLErrorCode + ". Trying to proceed anyway...");
                hasFlaggedOpenglError = true;
            }
        }
        GL20.glVertexAttribPointer(texCoordParam, 2, false, 32, buffer);
        GL20.glEnableVertexAttribArray(texCoordParam);
    }

    public static void unsetTextureCoord() {
        GL20.glDisableVertexAttribArray(texCoordParam);
    }

    public static void setLightCoord(ByteBuffer buffer) {
        GL20.glGetUniform(clProgram, lightCoordUniform, cachedLightCoord);
        GL20.glUniform4i(lightCoordUniform, 0, 0, 0, 0);
        GL20.glVertexAttribPointer(lightCoordParam, 4, true, false, 32, buffer);
        GL20.glEnableVertexAttribArray(lightCoordParam);
    }

    public static void unsetLightCoord() {
        GL20.glDisableVertexAttribArray(lightCoordParam);
        GL20.glUniform4(lightCoordUniform, cachedLightCoord);
    }

    public static void addVertex(Tessellator instance, double par1, double par3, double par5) {
        int cl_rawBufferSize = instance.getRawBufferSize();

        if (instance.rawBufferIndex >= cl_rawBufferSize - 32) {
            if (cl_rawBufferSize == 0) {
                cl_rawBufferSize = 0x10000; //65536
                instance.setRawBufferSize(cl_rawBufferSize);
                instance.rawBuffer = new int[cl_rawBufferSize];
            } else {
                cl_rawBufferSize *= 2;
                instance.setRawBufferSize(cl_rawBufferSize);
                instance.rawBuffer = Arrays.copyOf(instance.rawBuffer, cl_rawBufferSize);
            }
        }

        ++instance.addedVertices;

        if (instance.hasTexture) {
            instance.rawBuffer[instance.rawBufferIndex + 3] = Float.floatToRawIntBits((float) instance.textureU);
            instance.rawBuffer[instance.rawBufferIndex + 4] = Float.floatToRawIntBits((float) instance.textureV);
        }

        if (instance.hasBrightness) {
            /* << and >> take precedence over &
             * Incoming:
             * 0000 0000 SSSS BBBB GGGG RRRR LLLL 0000 */
            
            /* 0000 SSSS 0000 BBBB 0000 GGGG 0000 RRRR */
            instance.rawBuffer[instance.rawBufferIndex + 7] = (instance.brightness << 4 & 0x0F000000)
                                                            | (instance.brightness << 0 & 0x000F0000)
                                                            | (instance.brightness >> 4 & 0x00000F00)
                                                            | (instance.brightness >> 8 & 0x0000000F);
        }

        if (instance.hasColor) {
            instance.rawBuffer[instance.rawBufferIndex + 5] = instance.color;
        }

        if (instance.hasNormals) {
            instance.rawBuffer[instance.rawBufferIndex + 6] = instance.normal;
        }

        instance.rawBuffer[instance.rawBufferIndex + 0] = Float.floatToRawIntBits((float) (par1 + instance.xOffset));
        instance.rawBuffer[instance.rawBufferIndex + 1] = Float.floatToRawIntBits((float) (par3 + instance.yOffset));
        instance.rawBuffer[instance.rawBufferIndex + 2] = Float.floatToRawIntBits((float) (par5 + instance.zOffset));
        instance.rawBufferIndex += 8;
        ++instance.vertexCount;

        return;

    }

    public static boolean isProgramInUse() {
        return programInUse;
    }
}
