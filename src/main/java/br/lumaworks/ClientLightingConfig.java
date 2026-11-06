package br.lumaworks;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Client-only preference; enabled for new installations. The normal light engine and server gameplay remain independent. */
public final class ClientLightingConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue NATIVE_RGB;

    static {
        var builder = new ModConfigSpec.Builder();
        NATIVE_RGB = builder
            .comment("Experimental colored lighting for nearby block surfaces. On by default for new installations; existing preferences are preserved.",
                "Tested with the standard renderer, Sodium 0.9.2 and Iris 1.11.4 + Complementary r5.9.3.",
                "Does not color entities or fluids. Shader-pack results can vary.")
            .translation("lumaworks.configuration.nativeRgbEnabled")
            .define("nativeRgbEnabled", true);
        SPEC = builder.build();
    }

    private ClientLightingConfig() {}
}
