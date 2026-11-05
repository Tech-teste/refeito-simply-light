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

/** Small cardinal-direction switch editor; all visuals are drawn independently. */
public final class EdgeEditorScreen extends Screen implements MenuAccess<EdgeEditorMenu> {
    private final EdgeEditorMenu menu;
    private final int imageWidth = 128;
    private final int imageHeight = 70;
    private int leftPos;
    private int topPos;
    private final Button[] directions = new Button[4];
    private final int[] bits = new int[4];
    private static final String[] SIDES = {"north", "east", "south", "west"};
    private Direction forward = Direction.NORTH;

    public EdgeEditorScreen(EdgeEditorMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
    }
    @Override protected void init() {
        super.init();
        leftPos = Math.max(4, width / 2 - 192);
        topPos = Math.max(4, (height - imageHeight) / 2);
        if (minecraft != null && minecraft.player != null) forward = minecraft.player.getDirection();
        Direction[] sides = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (int i = 0; i < 4; i++) {
            bits[i] = switch (sides[i]) { case NORTH -> 0; case EAST -> 1; case SOUTH -> 2; default -> 3; };
            final int side = i;
            directions[i] = addRenderableWidget(Button.builder(Component.empty(), b -> send(side))
                .bounds(leftPos + 5, topPos + 4 + i * 16, 32, 14).build());
            // Keep native input, focus, tooltips and narration; draw our own compact switch below.
            directions[i].setAlpha(0.0f);
        }
        addRenderableWidget(Button.builder(Component.translatableWithFallback("gui.lumaworks.exit", "Sair"), b -> onClose())
            .bounds(leftPos + 92, topPos + 50, 32, 16).build());
        updateButtons();
    }
    private void send(int button) {
        if (minecraft != null && minecraft.gameMode != null)
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button);
    }
    private void updateButtons() {
        for (int i = 0; i < 4; i++) if (directions[i] != null) {
            boolean selected = (menu.mask() & (1 << i)) != 0;
            directions[i].setMessage(Component.translatable("gui.lumaworks.side_state",
                Component.translatable("gui.lumaworks." + SIDES[i]),
                Component.translatable("gui.lumaworks." + (selected ? "on" : "off"))));
            directions[i].active = !selected || Integer.bitCount(menu.mask()) > 1;
            directions[i].setTooltip(Tooltip.create(Component.translatable(
                !directions[i].active ? "gui.lumaworks.keep_one" : selected ? "gui.lumaworks.remove_side" : "gui.lumaworks.add_side")));
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
        for (int i = 0; i < 4; i++)
            g.text(font, Component.translatable("gui.lumaworks." + SIDES[i]), 40, 7 + i * 16, 0xff404040, false);
        Component facing = Component.translatableWithFallback("gui.lumaworks.looking", "Olhando:");
        Component direction = Component.translatable("gui.lumaworks." + forward.getName());
        g.text(font, facing, 124 - font.width(facing), 8, 0xff404040, false);
        g.text(font, direction, 124 - font.width(direction), 19, 0xff404040, false);
    }
}
