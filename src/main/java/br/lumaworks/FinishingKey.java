package br.lumaworks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

/** Reusable crafting tool and in-world finish conversion. */
public final class FinishingKey extends Item {
    public FinishingKey(Properties properties) { super(properties.stacksTo(1)); }
    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context,
            net.minecraft.world.item.component.TooltipDisplay display,
            java.util.function.Consumer<net.minecraft.network.chat.Component> tooltip, TooltipFlag flags) {
        tooltip.accept(net.minecraft.network.chat.Component.translatable("tooltip.lumaworks.finishing_key.world").withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltip.accept(net.minecraft.network.chat.Component.translatable("tooltip.lumaworks.finishing_key.craft").withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltip.accept(net.minecraft.network.chat.Component.translatable("tooltip.lumaworks.finishing_key.reuse").withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    @Override public ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
        if (instance instanceof ItemStack stack && !stack.isEmpty())
            return ItemStackTemplate.fromNonEmptyStack(stack.copyWithCount(1));
        if (instance instanceof ItemStackTemplate template) return template.withCount(1);
        return new ItemStackTemplate(this);
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var player = context.getPlayer();
        var state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ArchitecturalLight) || !state.hasProperty(ArchitecturalLight.FRAMED)) return InteractionResult.PASS;
        if (player == null || !player.mayBuild() || !level.mayInteract(player, pos)) return InteractionResult.FAIL;
        String name = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath().replace("_seamless", "");
        boolean framed = !state.getValue(ArchitecturalLight.FRAMED);
        var entry = LumaWorks.LIGHTS.get(name + (framed ? "" : "_seamless"));
        if (entry == null) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            var next = entry.get().withPropertiesOf(state).setValue(ArchitecturalLight.FRAMED, framed);
            level.setBlock(pos, next, Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }
}
