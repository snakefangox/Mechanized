package net.snakefangox.mechanized.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.steam.Steam;

public class SteamPipe extends Block implements BlockEntityProvider {

	protected static final BooleanProperty CONNECTED_NORTH = BooleanProperty.of("north");
	protected static final BooleanProperty CONNECTED_SOUTH = BooleanProperty.of("south");
	protected static final BooleanProperty CONNECTED_EAST = BooleanProperty.of("east");
	protected static final BooleanProperty CONNECTED_WEST = BooleanProperty.of("west");
	protected static final BooleanProperty CONNECTED_UP = BooleanProperty.of("up");
	protected static final BooleanProperty CONNECTED_DOWN = BooleanProperty.of("down");

	private static final VoxelShape MAIN_BOX = VoxelShapes.cuboid(0.3, 0.3, 0.3, 0.7, 0.7, 0.7);
	public VoxelShape BOX_N = VoxelShapes.cuboid(0.25, 0.25, 0, 0.75, 0.75, 0.3);
	public VoxelShape BOX_E;
	public VoxelShape BOX_S;
	public VoxelShape BOX_W;
	public VoxelShape BOX_U;
	public VoxelShape BOX_D;

	public SteamPipe(Settings settings) {
		super(settings);
		setDefaultState(
				getDefaultState().with(CONNECTED_NORTH, false).with(CONNECTED_SOUTH, false).with(CONNECTED_EAST, false)
						.with(CONNECTED_WEST, false).with(CONNECTED_UP, false).with(CONNECTED_DOWN, false));
		Box box = BOX_N.getBoundingBox();
		BOX_E = VoxelShapes.cuboid(1F - box.z1, box.y1, box.x1, 1F - box.z2, box.y2, box.x2);
		BOX_S = VoxelShapes.cuboid(box.x1, box.y1, 1F - box.z1, box.x2, box.y2, 1F - box.z2);
		BOX_W = VoxelShapes.cuboid(box.z1, box.y1, box.x1, box.z2, box.y2, box.x2);
		BOX_U = VoxelShapes.cuboid(box.x1, 1F - box.z1, box.y1, box.x2, 1F - box.z2, box.y2);
		BOX_D = VoxelShapes.cuboid(box.x1, box.z1, box.y1, box.x2, box.z2, box.y2);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		VoxelShape shape = MAIN_BOX;
		if (state.get(CONNECTED_NORTH))
			shape = VoxelShapes.union(shape, BOX_N);
		if (state.get(CONNECTED_SOUTH))
			shape = VoxelShapes.union(shape, BOX_S);
		if (state.get(CONNECTED_EAST))
			shape = VoxelShapes.union(shape, BOX_E);
		if (state.get(CONNECTED_WEST))
			shape = VoxelShapes.union(shape, BOX_W);
		if (state.get(CONNECTED_UP))
			shape = VoxelShapes.union(shape, BOX_U);
		if (state.get(CONNECTED_DOWN))
			shape = VoxelShapes.union(shape, BOX_D);
		return shape;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_EAST, CONNECTED_WEST, CONNECTED_UP, CONNECTED_DOWN);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getStateForPipe(ctx.getWorld(), ctx.getBlockPos());
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos,
			boolean moved) {
		world.setBlockState(pos, getStateForPipe(world, pos));
		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
	}

	private BlockState getStateForPipe(World world, BlockPos pos) {
		BlockEntity north = world.getBlockEntity(pos.offset(Direction.NORTH));
		BlockEntity south = world.getBlockEntity(pos.offset(Direction.SOUTH));
		BlockEntity east = world.getBlockEntity(pos.offset(Direction.EAST));
		BlockEntity west = world.getBlockEntity(pos.offset(Direction.WEST));
		BlockEntity up = world.getBlockEntity(pos.offset(Direction.UP));
		BlockEntity down = world.getBlockEntity(pos.offset(Direction.DOWN));
		BlockEntity[] dirEntities = new BlockEntity[] { north, south, east, west, up, down };

		// for(BlockEntity be: dirEntities) {
		// if(be instanceof SteamPipeEntity)
		// ;
		// }

		return getDefaultState().with(CONNECTED_NORTH, north instanceof Steam)
				.with(CONNECTED_SOUTH, south instanceof Steam).with(CONNECTED_EAST, east instanceof Steam)
				.with(CONNECTED_WEST, west instanceof Steam).with(CONNECTED_UP, up instanceof Steam)
				.with(CONNECTED_DOWN, down instanceof Steam);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return MRegister.STEAM_PIPE_ENTITY.instantiate();
	}

}
