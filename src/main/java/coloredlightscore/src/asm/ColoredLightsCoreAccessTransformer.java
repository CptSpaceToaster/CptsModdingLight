package coloredlightscore.src.asm;

import java.io.IOException;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

public class ColoredLightsCoreAccessTransformer extends AccessTransformer {

    public ColoredLightsCoreAccessTransformer() throws IOException {
        super("ColoredLightCore_at.cfg");
    }

}
