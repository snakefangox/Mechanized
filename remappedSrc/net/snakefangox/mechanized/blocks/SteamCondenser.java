package net.snakefangox.mechanized.blocks;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.entity.SteamCondenserEntity;

public class SteamCondenser extends Block implements BlockEntityProvider, FluidDrainable, AttributeProvider {

	public SteamCondenser(Settings settings) {
		super(settings);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction dir = Direction.fromHorizontal(ctx.getPlayerFacing().getHorizontal());
		return getDefaultState().with(HorizontalFacingBlock.FACING, dir);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(HorizontalFacingBlock.FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return MRegister.STEAM_CONDENSER_ENTITY.instantiate();
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if (world.getBlockEntity(pos) instanceof SteamCondenserEntity) {
			SteamCondenserEntity be = (SteamCondenserEntity) world.getBlockEntity(pos);
			if (be.tank.attemptAnyExtraction(FluidAmount.BUCKET, Simulation.SIMULATE).getAmount_F()
					.isLessThanOrEqual(FluidAmount.BUCKET)) {
				return be.tank.attemptAnyExtraction(FluidAmount.BUCKET, Simulation.ACTION).getRawFluid();
			} else {
				return Fluids.EMPTY;
			}
		}
		return Fluids.EMPTY;
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof SteamCondenserEntity) {
			SteamCondenserEntity tank = (SteamCondenserEntity) be;
			to.offer(tank.tank);
		}
	}

}
