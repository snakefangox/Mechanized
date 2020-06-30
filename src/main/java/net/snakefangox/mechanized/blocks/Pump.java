package net.snakefangox.mechanized.blocks;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.entity.PumpEntity;

public class Pump extends Block implements BlockEntityProvider, AttributeProvider {

	private static final VoxelShape MAIN_BOX = VoxelShapes.cuboid(0.2, 0, 0.2, 0.8, 1, 0.8);
	private VoxelShape BOX_N = VoxelShapes.cuboid(0.35, 0.6, 0, 0.65, 0.9, 0.5);
	private VoxelShape BOX_E;
	private VoxelShape BOX_S;
	private VoxelShape BOX_W;

	public Pump(Settings settings) {
		super(settings);
		Box box = BOX_N.getBoundingBox();
		BOX_E = VoxelShapes.cuboid(1F - box.minZ, box.minY, box.minX, 1F - box.maxZ, box.maxY, box.maxX);
		BOX_S = VoxelShapes.cuboid(box.minX, box.minY, 1F - box.minZ, box.maxX, box.maxY, 1F - box.maxZ);
		BOX_W = VoxelShapes.cuboid(box.minZ, box.minY, box.minX, box.maxZ, box.maxY, box.maxX);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		VoxelShape shape;
		switch (state.get(HorizontalFacingBlock.FACING)) {
		case EAST:
			shape = BOX_E;
			break;
		case NORTH:
			shape = BOX_N;
			break;
		case SOUTH:
			shape = BOX_S;
			break;
		case WEST:
			shape = BOX_W;
			break;
		default:
			shape = BOX_N;
			break;
		}
		return VoxelShapes.union(MAIN_BOX, shape);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction dir = Direction.fromHorizontal(ctx.getSide().getOpposite().getHorizontal());
		return getDefaultState().with(HorizontalFacingBlock.FACING, dir);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(HorizontalFacingBlock.FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return MRegister.PUMP_ENTITY.instantiate();
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof PumpEntity) {
			PumpEntity tank = (PumpEntity) be;
			to.offer(tank.tank);
		}
	}

}
