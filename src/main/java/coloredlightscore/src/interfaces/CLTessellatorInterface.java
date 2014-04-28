package coloredlightscore.src.interfaces;

/*
 * Need access to rawBufferSize after Forge adds it?
 * 
 * No problem!  This interface gets applied to Tessellator, and we'll ASM the fields in 
 */

public interface CLTessellatorInterface {
	public int getRawBufferSize();
	public int setRawBufferSize(int in);
}
