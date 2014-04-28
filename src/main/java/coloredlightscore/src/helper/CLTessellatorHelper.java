package coloredlightscore.src.helper;

import java.util.Arrays;

import net.minecraft.client.renderer.Tessellator;

public class CLTessellatorHelper {

	//private static int nativeBufferSize = 0x200000;
	public static float sunlightBrightness = 1.0f;
	
	public CLTessellatorHelper() {
		// TODO Auto-generated constructor stub
	}
    
	 public void setBrightness(Tessellator instance, int par1)
    {
	 	instance.hasBrightness = true;
        instance.brightness = par1;
    }

	
    public static void addVertex(Tessellator junkthis, double par1, double par3, double par5)
    {
    	//Crutch
    	int cl_rawBufferSize = 0;
    	
    	
    	
        if (Tessellator.instance.rawBufferIndex >= cl_rawBufferSize - 32) 
        {
            if (cl_rawBufferSize == 0)
            {
            	cl_rawBufferSize = 0x10000; 		//65536
                Tessellator.instance.rawBuffer = new int[cl_rawBufferSize];
            }
            else
            {
            	cl_rawBufferSize *= 2;
                Tessellator.instance.rawBuffer = Arrays.copyOf(Tessellator.instance.rawBuffer, cl_rawBufferSize);
            }
        }
        
        ++Tessellator.instance.addedVertices;

        if (Tessellator.instance.hasTexture)
        {
        	Tessellator.instance.rawBuffer[Tessellator.instance.rawBufferIndex + 3] = Float.floatToRawIntBits((float)Tessellator.instance.textureU);
        	Tessellator.instance.rawBuffer[Tessellator.instance.rawBufferIndex + 4] = Float.floatToRawIntBits((float)Tessellator.instance.textureV);
        }

        if (Tessellator.instance.hasBrightness)
        {
        	
        	/*
        	 Two lightmaps:
        	  RSL
        	  GSB
        	 Three sets of two texture values:
        	  SL
        	  BR
        	  GS
        	
        	 << and >> take precedence over &
        	  http://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html
        	*/
        	
        	/* 0000 0000 SSSS 0000 0000 0000 LLLL 0000 */
        	Tessellator.instance.rawBuffer[Tessellator.instance.rawBufferIndex + 7] = Tessellator.instance.brightness & 15728880;
        	/* 0000 0000 BBBB 0000 0000 0000 RRRR 0000 */
        	Tessellator.instance.rawBuffer[Tessellator.instance.rawBufferIndex + 8] = (Tessellator.instance.brightness<<4 & 15728640) | (Tessellator.instance.brightness>>8 & 240);
        	/* 0000 0000 GGGG 0000 0000 0000 SSSS 0000 */
        	Tessellator.instance.rawBuffer[Tessellator.instance.rawBufferIndex + 9] = (Tessellator.instance.brightness<<8 & 15728640) | (Tessellator.instance.brightness>>16 & 240);
        	
        }

        if (Tessellator.instance.hasColor)
        {
            Tessellator.instance.rawBuffer[Tessellator.instance.rawBufferIndex + 5] = Tessellator.instance.color;
        }

        if (Tessellator.instance.hasNormals)
        {
            Tessellator.instance.rawBuffer[Tessellator.instance.rawBufferIndex + 6] = Tessellator.instance.normal;
        }

        Tessellator.instance.rawBuffer[Tessellator.instance.rawBufferIndex + 0] = Float.floatToRawIntBits((float)(par1 + Tessellator.instance.xOffset));
        Tessellator.instance.rawBuffer[Tessellator.instance.rawBufferIndex + 1] = Float.floatToRawIntBits((float)(par3 + Tessellator.instance.yOffset));
        Tessellator.instance.rawBuffer[Tessellator.instance.rawBufferIndex + 2] = Float.floatToRawIntBits((float)(par5 + Tessellator.instance.zOffset));
        Tessellator.instance.rawBufferIndex += 10;
        ++Tessellator.instance.vertexCount;
        
        
        return;
        
    }
}
