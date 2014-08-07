package coloredlightscore.src.types;

/* This interface is added to Tessellator and gives rudimentary access to the fields we made public in the AT  
 * Cast Tessellator to a (CLTessellatorInterface) and then you can use set/getRawBufferSize */
public interface CLTessellatorInterface {
    public static final String appliedInterface = "coloredlightscore/src/types/CLTessellatorInterface";
    public static final String getterName = "getRawBufferSize";
    public static final String setterName = "setRawBufferSize";
    public static final String fieldName = "rawBufferSize"; //TODO: trianglecube36: WARNING! this is not in srg... if get/setRawBufferSize is ever called it will crash!!!
    public static final String fieldDescriptor = "I";

    public int getRawBufferSize();
    public void setRawBufferSize(int in);
}
