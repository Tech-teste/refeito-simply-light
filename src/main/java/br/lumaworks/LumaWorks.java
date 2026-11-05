package br.lumaworks;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.*;

@Mod(LumaWorks.ID)
public final class LumaWorks {
    public static final String ID = "lumaworks";
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ID);
    public static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ID);
    public static final java.util.function.Supplier<net.minecraft.world.inventory.MenuType<EdgeEditorMenu>> EDGE_EDITOR = MENUS.register("edge_editor", () ->
        net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((id, inventory, data) -> new EdgeEditorMenu(id, inventory, data.readBlockPos())));
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);
    public static final Map<String, DeferredBlock<? extends Block>> LIGHTS = new LinkedHashMap<>();

    public static final DeferredItem<FinishingKey> FINISHING_KEY = ITEMS.registerItem("finishing_key", FinishingKey::new);

    static {
        for (DyeColor color : DyeColor.values()) {
            for (LightForm form : LightForm.values()) {
                String name = color.getName() + "_" + form.name().toLowerCase(java.util.Locale.ROOT);
                DeferredBlock<? extends Block> block = form == LightForm.COLUMN
                    ? BLOCKS.registerBlock(name, p -> new ColumnLight(p.strength(1.2f).noOcclusion()
                        .lightLevel(s -> s.getValue(ColumnLight.PART) == 2 ? 15 : 0)))
                    : BLOCKS.registerBlock(name, p -> new ArchitecturalLight((form == LightForm.SIGNAL || form == LightForm.INVERSE ? p : p.noOcclusion()).strength(1.2f)
                        .lightLevel(s -> form == LightForm.SIGNAL || form == LightForm.INVERSE
                            ? (s.getValue(ArchitecturalLight.LIT) ? 15 : 0)
                            : (form == LightForm.BULB || form == LightForm.STRIP_LOW || form == LightForm.STRIP_HIGH ? 14 : 15)), form));
                LIGHTS.put(name, block);
                ITEMS.registerItem(name, p -> new LightingItem(block.get(), p, form));
                if (form == LightForm.SIGNAL || form == LightForm.INVERSE || form == LightForm.SLAB || form == LightForm.PANEL) {
                    String seamless = name + "_seamless";
                    var smoothBlock = BLOCKS.registerBlock(seamless, p -> new ArchitecturalLight((form == LightForm.SIGNAL || form == LightForm.INVERSE ? p : p.noOcclusion()).strength(1.2f)
                        .lightLevel(state -> form == LightForm.SIGNAL || form == LightForm.INVERSE
                            ? (state.getValue(ArchitecturalLight.LIT) ? 15 : 0) : 15), form, false));
                    LIGHTS.put(seamless, smoothBlock);
                    ITEMS.registerItem(seamless, p -> new LightingItem(smoothBlock.get(), p, form));
                }
            }
        }
        TABS.register("lighting", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lumaworks"))
            .icon(() -> LIGHTS.get("white_panel").get().asItem().getDefaultInstance())
            .displayItems((parameters, output) -> { LIGHTS.values().forEach(b -> output.accept(b.get())); output.accept(FINISHING_KEY.get()); })
            .build());
    }

    public LumaWorks(IEventBus bus, net.neoforged.fml.ModContainer container) {
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, ClientLightingConfig.SPEC);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        MENUS.register(bus);
        TABS.register(bus);
    }
}
