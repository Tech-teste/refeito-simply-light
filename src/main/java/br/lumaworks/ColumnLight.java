package br.lumaworks;

import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;
import org.jspecify.annotations.Nullable;

public final class ColumnLight extends Block implements SimpleWaterloggedBlock {
    private boolean dismantling;
    public static final IntegerProperty PART = IntegerProperty.create("part", 0, 2);
    public static final BooleanProperty WATER = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape[] SHAPES = {
        Shapes.or(box(3,0,3,13,3,13), box(6,3,6,10,16,10)),
        box(6,0,6,10,16,10),
        Shapes.or(box(6,0,6,10,5,10), box(4,5,4,12,15,12))};

    public ColumnLight(Properties properties) {
        super(properties.pushReaction(PushReaction.DESTROY));
        registerDefaultState(stateDefinition.any().setValue(PART, 0).setValue(WATER, false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(PART, WATER); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPES[state.getValue(PART)]; }
    @Override public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos base = ctx.getClickedPos();
        if (base.getY() + 2 > level.getMaxY()) return null;
        for (int i = 0; i < 3; i++) {
            BlockPos at = base.above(i);
            if (!level.getBlockState(at).canBeReplaced(ctx) || (ctx.getPlayer() != null && !level.mayInteract(ctx.getPlayer(), at))) return null;
        }
        return defaultBlockState().setValue(WATER, level.getFluidState(base).is(Fluids.WATER));
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        // Insert both parts without neighbor callbacks, then notify after the structure is complete.
        for (int part = 1; part < 3; part++) {
            BlockPos at = pos.above(part);
            level.setBlock(at, defaultBlockState().setValue(PART, part).setValue(WATER, level.getFluidState(at).is(Fluids.WATER)), UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE);
        }
        for (int part = 0; part < 3; part++) level.updateNeighborsAt(pos.above(part), this);
    }
    @Override protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) { return false; }
    @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATER) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }
    @Override protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction side, BlockPos other, BlockState neighbor, RandomSource random) {
        if (state.getValue(WATER)) ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return state;
    }
    @Override protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean moved) {
        if (dismantling || level.getBlockState(pos).is(this)) return;
        dismantling = true;
        try {
            BlockPos base = pos.below(state.getValue(PART));
            for (int i = 0; i < 3; i++) {
                BlockPos at = base.above(i);
                BlockState segment = level.getBlockState(at);
                if (!at.equals(pos) && segment.is(this) && segment.getValue(PART) == i)
                    level.setBlock(at, segment.getFluidState().createLegacyBlock(), UPDATE_ALL | UPDATE_SUPPRESS_DROPS);
            }
        } finally { dismantling = false; }
    }
}
