package net.snakefangox.mechanized.blocks;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
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
		BOX_E = VoxelShapes.cuboid(1F - box.z1, box.y1, box.x1, 1F - box.z2, box.y2, box.x2);
		BOX_S = VoxelShapes.cuboid(box.x1, box.y1, 1F - box.z1, box.x2, box.y2, 1F - box.z2);
		BOX_W = VoxelShapes.cuboid(box.z1, box.y1, box.x1, box.z2, box.y2, box.x2);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
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
