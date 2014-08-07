package coloredlightscore.src.types;

/* This interface is added to EntityRenderer and gives rudimentary access to the fields we ASM into it.  
 * Cast Entity Renderer to a (CLEntityRendererInterface) and then you can use set/getLightMapTexture2 */
public interface CLEntityRendererInterface {
    public static final String appliedInterface = "coloredlightscore/src/types/CLEntityRendererInterface";
    
    public static final String getterName2 = "getLightmapTexture2";
    public static final String fieldName2 = "lightmapTexture2";
    public static final String getterName3 = "getLightmapTexture3";
    public static final String fieldName3 = "lightmapTexture3";
    
    public static final String fieldName = "lightmapTexture";
    public static final String obfFieldName = "field_78513_d"; //was T
    public static final String setterName = "setLightmapTexture";
    
    public CLDynamicTexture3D getLightmapTexture2();
    public CLDynamicTexture2D getLightmapTexture3();
    
    public void setLightmapTexture(int[] map);
}
