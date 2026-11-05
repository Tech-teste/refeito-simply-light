package br.lumaworks.compat;

import br.lumaworks.LightForm;
import br.lumaworks.LumaWorks;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;

/** Optional information pages; the mod still runs when JEI is absent. */
@JeiPlugin
public final class LumaJeiPlugin implements IModPlugin {
    @Override public Identifier getPluginUid() { return Identifier.fromNamespaceAndPath(LumaWorks.ID, "information"); }
    @Override public void registerRecipes(IRecipeRegistration recipes) {
        for (LightForm form : LightForm.values()) {
            String name = form.name().toLowerCase(Locale.ROOT);
            var stacks = LumaWorks.LIGHTS.entrySet().stream()
                .filter(e -> e.getKey().endsWith("_" + name) || e.getKey().endsWith("_" + name + "_seamless"))
                .map(e -> e.getValue().get().asItem().getDefaultInstance()).toList();
            if (form == LightForm.SIGNAL || form == LightForm.INVERSE || form == LightForm.SLAB || form == LightForm.PANEL)
                recipes.addItemStackInfo(stacks, Component.translatable("tooltip.lumaworks." + name), Component.translatable("tooltip.lumaworks.finish"), Component.translatable("tooltip.lumaworks.wear"));
            else recipes.addItemStackInfo(stacks, Component.translatable("tooltip.lumaworks." + name), Component.translatable("tooltip.lumaworks.wear"));
        }
        recipes.addItemStackInfo(LumaWorks.FINISHING_KEY.get().getDefaultInstance(),
            Component.translatable("tooltip.lumaworks.finishing_key.world"),
            Component.translatable("tooltip.lumaworks.finishing_key.craft"),
            Component.translatable("tooltip.lumaworks.finishing_key.reuse"));
    }
}
