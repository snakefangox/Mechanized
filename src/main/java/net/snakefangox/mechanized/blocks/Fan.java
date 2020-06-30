package net.snakefangox.mechanized.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.snakefangox.mechanized.MRegister;

public class Fan extends Block implements BlockEntityProvider {

	private VoxelShape BOX_N = VoxelShapes.cuboid(0, 0, 0.5, 1, 1, 0.999);
	private VoxelShape BOX_E;
	private VoxelShape BOX_S;
	private VoxelShape BOX_W;
	private VoxelShape BOX_U;
	private VoxelShape BOX_D;

	public Fan(Settings settings) {
		super(settings);
		Box box = BOX_N.getBoundingBox();
		BOX_E = VoxelShapes.cuboid(1F - box.minZ, box.minY, box.minX, 1F - box.maxZ, box.maxY, box.maxX);
		BOX_S = VoxelShapes.cuboid(box.minX, box.minY, 1F - box.minZ, box.maxX, box.maxY, 1F - box.maxZ);
		BOX_W = VoxelShapes.cuboid(box.minZ, box.minY, box.minX, box.maxZ, box.maxY, box.maxX);
		BOX_U = VoxelShapes.cuboid(box.minX, 1F - box.minZ, box.minY, box.maxX, 1F - box.maxZ, box.maxY);
		BOX_D = VoxelShapes.cuboid(box.minX, box.minZ, box.minY, box.maxX, box.maxZ, box.maxY);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
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
		return getDefaultState().with(Properties.FACING, ctx.getSide());
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(Properties.FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return MRegister.FAN_ENTITY.instantiate();
	}
}
