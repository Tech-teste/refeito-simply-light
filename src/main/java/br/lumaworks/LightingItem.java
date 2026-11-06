package br.lumaworks;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/** Gameplay instructions visible both in inventory and recipe viewers. */
public final class LightingItem extends BlockItem {
    private final LightForm form;
    public LightingItem(Block block, Properties properties, LightForm form) {
        super(block, propertiesFor(properties, form));
        this.form = form;
    }
    private static Properties propertiesFor(Properties properties, LightForm form) {
        properties.equippable(net.minecraft.world.entity.EquipmentSlot.HEAD);
        if (form == LightForm.COLUMN) properties.attributes(
            net.minecraft.world.item.component.ItemAttributeModifiers.builder()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                    new net.minecraft.world.entity.ai.attributes.AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 4.0,
                        net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE),
                    net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED,
                    new net.minecraft.world.entity.ai.attributes.AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.0,
                        net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE),
                    net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND).build());
        return properties;
    }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
            Consumer<Component> tooltip, TooltipFlag flags) {
        super.appendHoverText(stack, context, display, tooltip, flags);
        boolean wall = getBlock() instanceof ArchitecturalLight light && light.isWallStrip();
        tooltip.accept(Component.translatable("tooltip.lumaworks." + (wall ? "strip_wall" : form.name().toLowerCase(java.util.Locale.ROOT))).withStyle(ChatFormatting.GRAY));
        if (getBlock() instanceof br.lumaworks.ArchitecturalLight light && light.redstoneMode() != 0)
            tooltip.accept(Component.translatable("tooltip.lumaworks.redstone." + (light.redstoneMode() == 2 ? "inverted" : "normal")).withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.lumaworks.wear").withStyle(ChatFormatting.GRAY));
        if (form == LightForm.SIGNAL || form == LightForm.INVERSE || form == LightForm.SLAB || form == LightForm.PANEL)
            tooltip.accept(Component.translatable("tooltip.lumaworks.finish").withStyle(ChatFormatting.GRAY));
    }
}
