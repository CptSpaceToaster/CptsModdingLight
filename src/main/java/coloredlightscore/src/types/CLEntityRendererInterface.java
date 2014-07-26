package coloredlightscore.src.types;

/* This interface is added to EntityRenderer and gives rudimentary access to the fields we ASM into it.  
 * Cast Entity Renderer to a (CLEntityRendererInterface) and then you can use set/getLightMapTexture2 */
public interface CLEntityRendererInterface {
    public static final String appliedInterface = "coloredlightscore/src/types/CLEntityRendererInterface";
    public static final String getterName = "getLightmapTexture2";
    public static final String setterName = "setLightmapTextureData2";
    public static final String fieldName = "lightmapTexture2";
    public static final String fieldDescriptor = "Lnet/minecraft/client/renderer/texture/DynamicTexture;";

    public CLDynamicTexture3D getLightmapTexture2();
    public void setLightmapTextureData2(int[] in);
}
