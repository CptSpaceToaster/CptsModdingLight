package coloredlightscore.src.types;

/*
 * Need access to rawBufferSize after Forge adds it?
 * 
 * No problem!  This interface gets applied to Tessellator, and we'll ASM the fields in 
 */

public interface CLTessellatorInterface {
	public static final String appliedInterface = "coloredlightscore/src/types/CLTessellatorInterface";
	public static final String getterName = "getRawBufferSize";
	public static final String setterName = "setRawBufferSize";
	public static final String fieldName = "rawBufferSize";
	public static final String fieldDescriptor = "I";
	
	
	
	public int getRawBufferSize();
	public void setRawBufferSize(int in);
}
