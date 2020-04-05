package net.snakefangox.mechanized.blocks;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.entity.PlacerEntity;

public class Placer extends Block implements BlockEntityProvider {

	public static final BooleanProperty EXTENDED = BooleanProperty.of("extended");

	private VoxelShape BOX_N = VoxelShapes.cuboid(0, 0, 0.3, 1, 1, 1);
	private VoxelShape BOX_E;
	private VoxelShape BOX_S;
	private VoxelShape BOX_W;
	private VoxelShape BOX_U;
	private VoxelShape BOX_D;

	public Placer(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(EXTENDED, false));
		Box box = BOX_N.getBoundingBox();
		BOX_E = VoxelShapes.cuboid(1F - box.z1, box.y1, box.x1, 1F - box.z2, box.y2, box.x2);
		BOX_S = VoxelShapes.cuboid(box.x1, box.y1, 1F - box.z1, box.x2, box.y2, 1F - box.z2);
		BOX_W = VoxelShapes.cuboid(box.z1, box.y1, box.x1, box.z2, box.y2, box.x2);
		BOX_U = VoxelShapes.cuboid(box.x1, 1F - box.z1, box.y1, box.x2, 1F - box.z2, box.y2);
		BOX_D = VoxelShapes.cuboid(box.x1, box.z1, box.y1, box.x2, box.z2, box.y2);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		if (world.isClient)
			return ActionResult.SUCCESS;

		BlockEntity be = world.getBlockEntity(pos);
		if (be != null && be instanceof PlacerEntity) {
			ContainerProviderRegistry.INSTANCE.openContainer(MRegister.PLACER_CONTAINER, player,
					(packetByteBuf -> packetByteBuf.writeBlockPos(pos)));
		}

		return ActionResult.SUCCESS;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		switch (state.get(Properties.FACING)) {
		case EAST:
			return BOX_E;

		case NORTH:
			return BOX_N;

		case SOUTH:
			return BOX_S;

		case WEST:
			return BOX_W;
			
		case UP:
			return BOX_U;
			
		case DOWN:
			return BOX_D;

		default:
			return BOX_N;

		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(Properties.FACING, ctx.getSide().getOpposite());
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(Properties.FACING, EXTENDED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return MRegister.PLACER_ENTITY.instantiate();
	}

}
