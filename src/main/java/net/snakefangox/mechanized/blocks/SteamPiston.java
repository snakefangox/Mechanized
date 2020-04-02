package net.snakefangox.mechanized.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.entity.SteamPistonEntity;

public class SteamPiston extends Block implements BlockEntityProvider {

	private VoxelShape BOX = VoxelShapes.cuboid(0, 0.001, 0, 1, 1, 1);
	public static final BooleanProperty EXTENDED = BooleanProperty.of("extended");

	public SteamPiston(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(EXTENDED, false));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos,
			boolean moved) {
		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof SteamPistonEntity) {
			((SteamPistonEntity) be).updateSignal(world.getReceivedStrongRedstonePower(pos));
		}
	}

	//Ugly hack, I know
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return BOX;
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(Properties.FACING, ctx.getSide());
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(Properties.FACING, EXTENDED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return MRegister.STEAM_PISTON_ENTITY.instantiate();
	}
}
