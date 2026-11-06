package br.lumaworks.client;

import br.lumaworks.EdgeEditorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.FluidTags;

/** Small cardinal-direction switch editor; all visuals are drawn independently. */
public final class EdgeEditorScreen extends Screen implements MenuAccess<EdgeEditorMenu> {
    private final EdgeEditorMenu menu;
    private final int imageWidth = 148;
    private final int imageHeight = 70;
    private int leftPos;
    private int topPos;
    private final Button[] directions = new Button[4];
    private static final String[] SIDES = {"north", "east", "south", "west"};
    private static final Direction[] CARDINALS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private final BlockState[] neighbors = new BlockState[4];
    private final ItemStack[] previews = new ItemStack[4];
    private final Component[] neighborNames = new Component[4];
    private Direction forward = Direction.NORTH;

    public EdgeEditorScreen(EdgeEditorMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
        for (int i = 0; i < 4; i++) {
            previews[i] = ItemStack.EMPTY;
            neighborNames[i] = Component.translatable("gui.lumaworks.neighbor_unavailable");
        }
    }
    @Override protected void init() {
        super.init();
        leftPos = Math.max(4, width / 2 - 192);
        topPos = Math.max(4, (height - imageHeight) / 2);
        if (minecraft != null && minecraft.player != null) forward = minecraft.player.getDirection();
        updateNeighbors();
        for (int i = 0; i < 4; i++) {
            final int side = i;
            directions[i] = addRenderableWidget(Button.builder(Component.empty(), b -> send(side))
                .bounds(leftPos + 5, topPos + 3 + i * 16, 90, 16).build());
            // Keep native input, focus, tooltips and narration; draw our own compact switch below.
            directions[i].setAlpha(0.0f);
        }
        addRenderableWidget(Button.builder(Component.translatableWithFallback("gui.lumaworks.exit", "Sair"), b -> onClose())
            .bounds(leftPos + imageWidth - 36, topPos + 50, 32, 16).build());
        updateButtons();
    }
    private void send(int button) {
        if (minecraft != null && minecraft.gameMode != null)
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button);
    }
    private void updateNeighbors() {
        if (minecraft == null || minecraft.level == null) return;
        for (int i = 0; i < CARDINALS.length; i++) {
            var pos = menu.position().relative(menu.sideDirection(i));
            BlockState state = minecraft.level.hasChunkAt(pos) ? minecraft.level.getBlockState(pos) : null;
            if (state == neighbors[i]) continue;
            neighbors[i] = state;
            previews[i] = state == null ? ItemStack.EMPTY : state.getBlock().asItem().getDefaultInstance();
            if (state != null && previews[i].isEmpty() && !state.isAir()) {
                if (state.getFluidState().is(FluidTags.WATER)) previews[i] = Items.WATER_BUCKET.getDefaultInstance();
                else if (state.getFluidState().is(FluidTags.LAVA)) previews[i] = Items.LAVA_BUCKET.getDefaultInstance();
            }
            neighborNames[i] = state == null ? Component.translatable("gui.lumaworks.neighbor_unavailable") : state.getBlock().getName();
        }
    }
    private void updateButtons() {
        for (int i = 0; i < 4; i++) if (directions[i] != null) {
            boolean selected = (menu.mask() & (1 << i)) != 0;
            directions[i].setMessage(Component.translatable("gui.lumaworks.side_state",
                Component.translatable("gui.lumaworks." + menu.sideLabel(i)),
                Component.translatable("gui.lumaworks." + (selected ? "on" : "off")))
                .append(" — ").append(Component.translatable("gui.lumaworks.neighbor", neighborNames[i])));
            directions[i].active = !selected || Integer.bitCount(menu.mask()) > 1;
            directions[i].setTooltip(Tooltip.create(Component.translatable("gui.lumaworks.neighbor", neighborNames[i])
                .append("\n").append(Component.translatable(
                !directions[i].active ? "gui.lumaworks.keep_one" : selected ? "gui.lumaworks.remove_side" : "gui.lumaworks.add_side"))));
        }
    }
    @Override public EdgeEditorMenu getMenu() { return menu; }
    @Override public boolean isPauseScreen() { return false; }
    @Override public boolean isInGameUi() { return true; }
    @Override public void tick() {
        if (minecraft == null || minecraft.player == null || minecraft.player.containerMenu != menu) {
            if (minecraft != null) minecraft.setScreen(null);
            return;
        }
        updateNeighbors();
        updateButtons();
    }
    @Override public void onClose() {
        if (minecraft != null && minecraft.player != null) minecraft.player.closeContainer();
        super.onClose();
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g, int x, int y, float delta) {
        super.extractRenderState(g, x, y, delta);
        g.pose().pushMatrix();
        g.pose().translate(leftPos, topPos);
        extractLabels(g, x, y);
        g.pose().popMatrix();
    }
    @Override public void extractBackground(GuiGraphicsExtractor g, int x, int y, float delta) {
        // No fullscreen dimming or blur: the edited block stays visible in the world.
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xff373737);
        g.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xfff0f0f0);
        g.fill(leftPos + 3, topPos + 3, leftPos + imageWidth - 3, topPos + imageHeight - 3, 0xffc6c6c6);
        for (int i = 0; i < 4; i++) {
            int sx = leftPos + 5, sy = topPos + 4 + i * 16;
            boolean on = (menu.mask() & (1 << i)) != 0;
            boolean focus = directions[i] != null && directions[i].isHoveredOrFocused();
            g.fill(sx, sy, sx + 32, sy + 14, focus ? 0xffffffff : 0xff555555);
            g.fill(sx + 1, sy + 1, sx + 31, sy + 13, on ? 0xff61965c : 0xff777777);
            int knob = sx + (on ? 19 : 2);
            g.fill(knob, sy + 2, knob + 11, sy + 12, 0xff666666);
            g.fill(knob, sy + 2, knob + 10, sy + 11, 0xffeeeeee);
            g.fill(knob + 1, sy + 3, knob + 9, sy + 4, 0xffffffff);
        }
    }
    private void extractLabels(GuiGraphicsExtractor g, int x, int y) {
        for (int i = 0; i < 4; i++) {
            int sy = 3 + i * 16;
            g.fill(40, sy, 57, sy + 16, 0xffa0a0a0);
            g.fill(41, sy + 1, 56, sy + 15, 0xffb6b6b6);
            if (!previews[i].isEmpty()) g.item(previews[i], 41, sy);
            else if (neighbors[i] != null && neighbors[i].isAir()) {
                Component air = neighbors[i].getBlock().getName();
                g.text(font, air, 49 - font.width(air) / 2, sy + 4, 0xff606060, false);
            } else g.text(font, "?", 46, sy + 4, 0xff606060, false);
            g.text(font, Component.translatable("gui.lumaworks." + menu.sideLabel(i)), 62, 7 + i * 16, 0xff404040, false);
        }
        Component facing = Component.translatableWithFallback("gui.lumaworks.looking", "Olhando:");
        Component direction = Component.translatable("gui.lumaworks." + forward.getName());
        g.text(font, facing, imageWidth - 4 - font.width(facing), 8, 0xff404040, false);
        g.text(font, direction, imageWidth - 4 - font.width(direction), 19, 0xff404040, false);
    }
}
