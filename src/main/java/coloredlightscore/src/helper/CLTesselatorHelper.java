package kovukore.coloredlights.src.helper;

public class CLTesselatorHelper {

	// This class pretty much does nothing
	// It was used to find the ASM instructions for the replacement setBrightness on the Tessellator object
	// Origional approach was to use reflection, which was abandonded in the name of performance
		
	public CLTesselatorHelper() {
		
	}
	
	// Mock-up to get ASM for replacement method
    public void setBrightness(int par1)
    {
        
        /* Resultant ASM:
         * 
         * 0  aload_0 [this]
         * 1  iconst_1
         * 2  putfield kovukore.coloredlights.src.helper.CLTesselatorHelper.hasBrightness : boolean [20]
         * 5  aload_0 [this]
         * 6  iload_1 [par1]
         * 7  ldc <Integer 15728880> [22]
         * 9  iand
         * 10  putfield kovukore.coloredlights.src.helper.CLTesselatorHelper.brightness : int [23]
         * 13  return
         * 
         */
    }	
	/*
	 * 
	 * REFLECTION APPROACH BELOW

	static Field hasBrightness;
	static Field brightness;
	static boolean initialized = false;
	
	public static void initialize(Tessellator instance) throws NoSuchFieldException, SecurityException
	{
		hasBrightness = instance.getClass().getDeclaredField("hasBrightness");
		brightness = instance.getClass().getDeclaredField("brightness");
		
		hasBrightness.setAccessible(true);
		brightness.setAccessible(true);
	}

    public static void setBrightness(Tessellator instance, int par1) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
    {
    	if (!initialized)
    		initialize(instance);
    	
    	// Use reflection to set the field
    	// This is absolutely ugly and slower than a direct assignment
    	// Need to find a way around this!
    	
        hasBrightness.set(instance, true);
    	
        / ** 
         * First: 								0000SSSS0000000000000000LLLL0000
         * Old: 								0000SSSS0000BBBBGGGGRRRRLLLL0000
         * Refactor: 							0000SSSS0BBBB0GGGG0RRRR0LLLL0000
         * 
         * Takes the lightValue in the Form 	000) 0000 SSSS BBBB GGGG RRRR LLLL 0000
         * and formats it to the expected form: 0000 0000 SSSS 0000 0000 0000 LLLL 0000
         * 
         * CptSpaceToaster
         * /
    	//Visibility issues: instance.brightness = par1 & 15728880;
        brightness.set(instance, par1 & 15728880);
    }
   	 * 
   	 */

}
