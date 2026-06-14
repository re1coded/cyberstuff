package ru.re1coded.cyberstuff.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ReplicatorBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

//    public VoxelShape makeShape(){
//        VoxelShape shape = Shapes.empty();
//        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.0625, 0.875), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.0625, 0.0625, 0.0625, 0.9375, 0.1875, 0.3125), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.0625, 0.1875, 0.0625, 0.9375, 0.2625, 0.23828125), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.0625, 0.0625, 0.3125, 0.9375, 1, 0.9375), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.3125, 0.6875, 0.1875, 0.34375, 0.8125, 0.3125), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.3125, 0.8125, 0.1875, 0.34375, 0.856694375, 0.3125), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.178346875, 0.678346875, 0.1875, 0.209596875, 0.72254125, 0.3125), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.290403125, 0.678346875, 0.1875, 0.321653125, 0.72254125, 0.3125), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.15625, 0.8125, 0.1875, 0.1875, 0.856694375, 0.3125), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.15625, 0.6875, 0.1875, 0.1875, 0.8125, 0.3125), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.1875, 0.8125, 0.1875, 0.3125, 0.84375, 0.3125), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.1875, 0.65625, 0.1875, 0.3125, 0.6875, 0.3125), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.1875, 0.6875, 0.1875, 0.3125, 0.75, 0.203125), BooleanOp.OR);
//        shape = Shapes.join(shape, Shapes.box(0.1875, 0.75, 0.1875, 0.3125, 0.8125, 0.203125), BooleanOp.OR);
//
//        return shape;
//    }


    public ReplicatorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level,
                                               BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ReplicatorBlockEntity replicator) {
            player.openMenu(replicator, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(ReplicatorBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ReplicatorBlockEntity(blockPos, blockState);
    }
}
