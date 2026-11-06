package br.lumaworks;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Standard container buttons: the client never sends a target position or arbitrary block state. */
public final class EdgeEditorMenu extends AbstractContainerMenu {
    private final Level level;
    private final BlockPos position;
    private final Block expectedBlock;
    private final UUID owner;
    private final DataSlot edges = DataSlot.standalone();

    public EdgeEditorMenu(int id, Inventory inventory, BlockPos position) {
        super(LumaWorks.EDGE_EDITOR.get(), id);
        this.level = inventory.player.level();
        this.position = position.immutable();
        this.expectedBlock = level.getBlockState(position).getBlock();
        this.owner = inventory.player.getUUID();
        edges.set(readMask());
        addDataSlot(edges);
    }
    private int readMask() {
        BlockState state = level.getBlockState(position);
        if (!state.hasProperty(ArchitecturalLight.EDGES)) return 15;
        int mask = state.getValue(ArchitecturalLight.EDGES);
        return mask == 0 ? 15 : mask; // Preserve the existing full-outline representation.
    }
    public int mask() { return edges.get(); }
    public BlockPos position() { return position; }
    public boolean isWallStrip() { return expectedBlock instanceof ArchitecturalLight light && light.isWallStrip(); }
    public Direction sideDirection(int index) {
        if (!isWallStrip()) return new Direction[]{Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST}[index];
        var state = level.getBlockState(position);
        Direction face = state.hasProperty(ArchitecturalLight.WALL_FACING) ? state.getValue(ArchitecturalLight.WALL_FACING) : Direction.NORTH;
        return switch (index) { case 0 -> Direction.UP; case 1 -> face.getCounterClockWise(); case 2 -> Direction.DOWN; case 3 -> face.getClockWise(); default -> throw new IllegalArgumentException(); };
    }
    public String sideLabel(int index) { return isWallStrip() ? new String[]{"top","right","bottom","left"}[index] : sideDirection(index).getName(); }
    @Override public boolean stillValid(Player player) {
        return owner.equals(player.getUUID()) && player.level() == level && player.mayBuild() && !player.isSpectator()
            && player.distanceToSqr(Vec3.atCenterOf(position)) <= 64
            && level.hasChunkAt(position) && level.mayInteract(player, position)
            && level.getBlockState(position).is(expectedBlock)
            && level.getBlockState(position).hasProperty(ArchitecturalLight.EDGES);
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (level.isClientSide() || button < 0 || button > 4 || !stillValid(player)) return false;
        var permission = new PlayerInteractEvent.RightClickBlock(player, InteractionHand.MAIN_HAND, position,
            new BlockHitResult(Vec3.atCenterOf(position), isWallStrip() ? level.getBlockState(position).getValue(ArchitecturalLight.WALL_FACING) : Direction.UP, position, false));
        NeoForge.EVENT_BUS.post(permission);
        if (permission.isCanceled() || permission.getUseBlock() == TriState.FALSE || !stillValid(player)) return false;
        int mask = button == 4 ? 15 : readMask() ^ (1 << button);
        if (mask == 0) return false; // A selectable strip must retain at least one visible side.
        BlockState current = level.getBlockState(position);
        level.setBlock(position, current.setValue(ArchitecturalLight.EDGES, mask), Block.UPDATE_ALL);
        edges.set(mask);
        broadcastChanges();
        return true;
    }
    @Override public void broadcastChanges() {
        if (!level.isClientSide()) edges.set(readMask());
        super.broadcastChanges();
    }
    @Override public ItemStack quickMoveStack(Player player, int slot) { return ItemStack.EMPTY; }
}
