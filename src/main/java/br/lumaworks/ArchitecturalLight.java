package br.lumaworks;

import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import org.jspecify.annotations.Nullable;

/** Static block states only: no block entities, custom packets or ticking lamps. */
public final class ArchitecturalLight extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATER = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final EnumProperty<Direction> WALL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty EDGES = IntegerProperty.create("edges", 0, 15);
    public static final BooleanProperty FRAMED = BooleanProperty.create("framed");
    private final LightForm form;
    private final int redstoneMode;
    private final boolean wallStrip;
    private final VoxelShape[][] wallShapes;
    private final VoxelShape[] shapes = new VoxelShape[6];
    private final VoxelShape[] strips = new VoxelShape[16];

    public ArchitecturalLight(Properties properties, LightForm form) {
        this(properties, form, true);
    }

    public ArchitecturalLight(Properties properties, LightForm form, boolean framed) {
        this(properties, form, framed, 0);
    }

    public ArchitecturalLight(Properties properties, LightForm form, boolean framed, int redstoneMode) {
        this(properties, form, framed, redstoneMode, false);
    }

    public ArchitecturalLight(Properties properties, LightForm form, boolean framed, int redstoneMode, boolean wallStrip) {
        this.form = form;
        this.redstoneMode = redstoneMode;
        this.wallStrip = wallStrip;
        super(properties);
        wallShapes = wallStrip ? new VoxelShape[4][16] : null;
        BlockState initial = stateDefinition.any().setValue(WATER, false);
        if (controlled()) initial = initial.setValue(LIT, inverted());
        if (strip()) initial = initial.setValue(EDGES, 15);
        if (wallStrip) initial = initial.setValue(WALL_FACING, Direction.NORTH);
        if (!signal() && !strip()) initial = initial.setValue(FACING, Direction.UP);
        if (supportsFrame()) initial = initial.setValue(FRAMED, framed);
        registerDefaultState(initial);
        if (!signal() && !strip()) {
            double width = switch (form) { case BULB -> 5; case FIXTURE -> 8; case TUBE -> 3; default -> 16; };
            double depth = switch (form) { case SLAB -> 8; case PANEL -> 4; case BULB -> 7; case FIXTURE -> 2; default -> 16; };
            double lo = (16 - width) / 2, hi = 16 - lo;
            shapes[Direction.UP.ordinal()] = box(lo, 0, lo, hi, depth, hi);
            shapes[Direction.DOWN.ordinal()] = box(lo, 16-depth, lo, hi, 16, hi);
            shapes[Direction.NORTH.ordinal()] = box(lo, lo, 16-depth, hi, hi, 16);
            shapes[Direction.SOUTH.ordinal()] = box(lo, lo, 0, hi, hi, depth);
            shapes[Direction.WEST.ordinal()] = box(16-depth, lo, lo, 16, hi, hi);
            shapes[Direction.EAST.ordinal()] = box(0, lo, lo, depth, hi, hi);
        }
        if (wallStrip) {
            // Local order seen from the front: top, right, bottom, left.
            double[][] boxes = {{1.4,14.6,14.4375,14.6,16,16}, {0,0,14.4375,1.4,16,16},
                {1.4,0,14.4375,14.6,1.4,16}, {14.6,0,14.4375,16,16,16}};
            for (int rotation = 0; rotation < 4; rotation++) for (int mask = 0; mask < 16; mask++) {
                VoxelShape shape = Shapes.empty();
                for (int bit = 0; bit < 4; bit++) if (((mask == 0 ? 15 : mask) & (1 << bit)) != 0) {
                    double[] b = boxes[bit].clone();
                    for (int turn = 0; turn < rotation; turn++) b = new double[]{16-b[5],b[1],b[0],16-b[2],b[4],b[3]};
                    shape = Shapes.or(shape,box(b[0],b[1],b[2],b[3],b[4],b[5]));
                }
                wallShapes[rotation][mask] = shape.optimize();
            }
        } else if (strip()) {
            double y = form == LightForm.STRIP_HIGH ? 15 : 0;
            double height = form == LightForm.STRIP_HIGH ? 1 : 1.05;
            double rail = 1.4;
            VoxelShape[] sides = {box(0,y,0,16,y+height,rail), box(16-rail,y,0,16,y+height,16),
                box(0,y,16-rail,16,y+height,16), box(0,y,0,rail,y+height,16)};
            for (int mask = 0; mask < 16; mask++) {
                VoxelShape shape = Shapes.empty();
                for (int bit = 0; bit < 4; bit++) if (((mask == 0 ? 15 : mask) & (1 << bit)) != 0) shape = Shapes.or(shape, sides[bit]);
                strips[mask] = shape.optimize();
            }
        }
    }

    private boolean strip() { return form == LightForm.STRIP_LOW || form == LightForm.STRIP_HIGH; }
    private boolean signal() { return form == LightForm.SIGNAL || form == LightForm.INVERSE; }
    private boolean controlled() { return signal() || redstoneMode != 0; }
    private boolean inverted() { return form == LightForm.INVERSE || redstoneMode == 2; }
    public int redstoneMode() { return redstoneMode; }
    public boolean isWallStrip() { return wallStrip; }
    public static int wallRotation(Direction facing) {
        return switch (facing) { case NORTH -> 0; case EAST -> 1; case SOUTH -> 2; case WEST -> 3; default -> throw new IllegalArgumentException("Horizontal face required"); };
    }
    private boolean supportsFrame() { return signal() || form == LightForm.SLAB || form == LightForm.PANEL; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATER);
        if (controlled()) builder.add(LIT);
        if (strip()) builder.add(EDGES);
        else if (!signal()) builder.add(FACING);
        if (wallStrip) builder.add(WALL_FACING);
        if (supportsFrame()) builder.add(FRAMED);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        int mask = 0;
        Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (int i = 0; strip() && i < directions.length; i++) {
            Direction d = directions[i];
            if (Block.canSupportCenter(ctx.getLevel(), ctx.getClickedPos().relative(d), d.getOpposite())) mask |= 1 << i;
        }
        Direction facing = ctx.getClickedFace();
        if ((form == LightForm.SLAB || form == LightForm.PANEL) && !ctx.isSecondaryUseActive()) {
            BlockState previous = ctx.getLevel().getBlockState(ctx.getClickedPos().relative(facing.getOpposite()));
            if (previous.getBlock() instanceof ArchitecturalLight light && (light.form == LightForm.SLAB || light.form == LightForm.PANEL)) facing = previous.getValue(FACING);
        }
        BlockState placed = defaultBlockState().setValue(WATER, ctx.getLevel().getFluidState(ctx.getClickedPos()).is(Fluids.WATER));
        if (controlled()) placed = placed.setValue(LIT, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()) ^ inverted());
        if (wallStrip) placed = placed.setValue(EDGES, 15).setValue(WALL_FACING,
            facing.getAxis().isHorizontal() ? facing : ctx.getHorizontalDirection().getOpposite());
        else if (strip()) placed = placed.setValue(EDGES, mask);
        else if (!signal()) placed = placed.setValue(FACING, facing);
        return placed;
    }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return signal() ? Shapes.block() : wallStrip ? wallShapes[wallRotation(state.getValue(WALL_FACING))][state.getValue(EDGES)]
            : strip() ? strips[state.getValue(EDGES)] : shapes[state.getValue(FACING).ordinal()];
    }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return strip() || form == LightForm.BULB || form == LightForm.FIXTURE ? Shapes.empty() : getShape(state, level, pos, ctx);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block source, @Nullable Orientation orientation, boolean moved) {
        if (!level.isClientSide() && controlled()) {
            boolean lit = level.hasNeighborSignal(pos) ^ inverted();
            if (lit != state.getValue(LIT)) level.setBlock(pos, state.setValue(LIT, lit), UPDATE_CLIENTS);
        }
    }
    // The standard block interaction goes through NeoForge's right-click events (including claim protection).
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!strip() || !player.isShiftKeyDown()) return InteractionResult.PASS;
        if (!player.mayBuild() || !level.mayInteract(player, pos)) return InteractionResult.FAIL;
        if (!level.isClientSide()) {
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)
                serverPlayer.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (id, inventory, owner) -> new EdgeEditorMenu(id, inventory, pos),
                    net.minecraft.network.chat.Component.translatable("gui.lumaworks.edge_editor")), data -> data.writeBlockPos(pos));
        }
        return InteractionResult.SUCCESS;
    }
    @Override protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) {
        return form != LightForm.TUBE && super.isPathfindable(state, type);
    }
    @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATER) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    @Override protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction side, BlockPos other, BlockState neighbor, RandomSource random) {
        if (state.getValue(WATER)) ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return state;
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) {
        if (signal()) return state;
        if (wallStrip) return state.setValue(WALL_FACING, rotation.rotate(state.getValue(WALL_FACING)));
        if (!strip()) return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
        int rotated = 0, mask = state.getValue(EDGES);
        Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (int i = 0; i < 4; i++) if ((mask & 1 << i) != 0) for (int j = 0; j < 4; j++) if (rotation.rotate(dirs[i]) == dirs[j]) rotated |= 1 << j;
        return state.setValue(EDGES, rotated);
    }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) {
        if (signal()) return state;
        if (wallStrip) {
            if (mirror == Mirror.NONE) return state;
            int mask = state.getValue(EDGES);
            return state.setValue(WALL_FACING, mirror.mirror(state.getValue(WALL_FACING)))
                .setValue(EDGES, (mask & 5) | ((mask & 2) << 2) | ((mask & 8) >> 2));
        }
        if (!strip()) return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
        int result = 0, mask = state.getValue(EDGES);
        Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (int i = 0; i < 4; i++) if ((mask & 1 << i) != 0) for (int j = 0; j < 4; j++) if (mirror.mirror(dirs[i]) == dirs[j]) result |= 1 << j;
        return state.setValue(EDGES, result);
    }
}
