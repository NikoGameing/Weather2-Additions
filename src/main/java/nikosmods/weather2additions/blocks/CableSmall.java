package nikosmods.weather2additions.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import nikosmods.weather2additions.blocks.blockfunction.CableGenericEntity;
import nikosmods.weather2additions.blocks.blockfunction.CableSmallEntity;
import nikosmods.weather2additions.items.itemreg.Items;
import org.jetbrains.annotations.NotNull;

public class CableSmall extends Block implements EntityBlock {

    public static EnumProperty<Connection> UP = EnumProperty.create("up", Connection.class);
    public static EnumProperty<Connection> DOWN = EnumProperty.create("down", Connection.class);
    public static EnumProperty<Connection> NORTH = EnumProperty.create("north", Connection.class);
    public static EnumProperty<Connection> EAST = EnumProperty.create("east", Connection.class);
    public static EnumProperty<Connection> SOUTH = EnumProperty.create("south", Connection.class);
    public static EnumProperty<Connection> WEST = EnumProperty.create("west", Connection.class);


    public CableSmall(Properties p_55160_) {
        super(p_55160_);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(UP, Connection.NONE)
                        .setValue(DOWN, Connection.NONE)
                        .setValue(NORTH, Connection.NONE)
                        .setValue(EAST, Connection.NONE)
                        .setValue(SOUTH, Connection.NONE)
                        .setValue(WEST, Connection.NONE)
        );
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState state1, boolean p_60519_) {
        if (state.getBlock() != state1.getBlock()) {
            CableGenericEntity cableEntity = (CableGenericEntity) level.getBlockEntity(blockPos);
            assert cableEntity != null;
            cableEntity.splitNetwork();
            super.onRemove(state, level, blockPos, state1, p_60519_);
        }
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new CableSmallEntity(blockPos, blockState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> entityType) {
        return CableSmallEntity::tick;
    }

    @Override
    public VoxelShape getShape(BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        VoxelShape defaultShape = Shapes.box(6.0 / 16.0, 6.0 / 16.0, 6.0 / 16.0, 10.0 / 16.0, 10.0 / 16.0, 10.0 / 16.0);
        switch (state.getValue(UP)) {
            case CABLE, CONNECTED -> {
                defaultShape = Shapes.join(defaultShape, Shapes.box(6.0 / 16.0, 10.0 / 16.0, 6.0 / 16.0, 10.0 / 16.0, 16.0 / 16.0, 10.0 / 16.0), BooleanOp.OR);
            }
        }
        switch (state.getValue(DOWN)) {
            case CABLE, CONNECTED -> {
                defaultShape = Shapes.join(defaultShape, Shapes.box(6.0/16.0, 0.0/16.0, 6.0/16.0, 10.0/16.0, 6.0/16.0, 10.0/16.0), BooleanOp.OR);
            }
        }
        switch (state.getValue(NORTH)) {
            case CABLE, CONNECTED -> {
                defaultShape = Shapes.join(defaultShape, Shapes.box(6.0/16.0, 6.0/16.0, 0.0/16.0, 10.0/16.0, 10.0/16.0, 6.0/16.0), BooleanOp.OR);
            }
        }
        switch (state.getValue(EAST)) {
            case CABLE, CONNECTED -> {
                defaultShape = Shapes.join(defaultShape, Shapes.box(10.0/16.0, 6.0/16.0, 6.0/16.0, 16.0/16.0, 10.0/16.0, 10.0/16.0), BooleanOp.OR);
            }
        }
        switch (state.getValue(SOUTH)) {
            case CABLE, CONNECTED -> {
                defaultShape = Shapes.join(defaultShape, Shapes.box(6.0/16.0, 6.0/16.0, 10.0/16.0, 10.0/16.0, 10.0/16.0, 16.0/16.0), BooleanOp.OR);
            }
        }
        switch (state.getValue(WEST)) {
            case CABLE, CONNECTED -> {
                defaultShape = Shapes.join(defaultShape, Shapes.box(0.0/16.0, 6.0/16.0, 6.0/16.0, 6.0/16.0, 10.0/16.0, 10.0/16.0), BooleanOp.OR);
            }
        }
        return defaultShape;
    }

    public enum Connection implements StringRepresentable {
        NONE("none"),
        CABLE("cable"),
        CONNECTED("connected");

        private final String serializedName;

        Connection(String serializedName) {
            this.serializedName = serializedName;
        }

        public @NotNull String getSerializedName() {
            return serializedName;
        }
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide && player.getMainHandItem().getItem() == Items.ANALYSER.get() && level.getBlockEntity(blockPos) instanceof CableGenericEntity) {
            NetworkHooks.openScreen((ServerPlayer) player, (CableGenericEntity) level.getBlockEntity(blockPos), blockPos);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}
