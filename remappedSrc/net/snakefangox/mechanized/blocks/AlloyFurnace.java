package net.snakefangox.mechanized.blocks;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.entity.AlloyFurnaceEntity;

public class AlloyFurnace extends Block implements BlockEntityProvider {

	public static final BooleanProperty LIT = BooleanProperty.of("lit");

	public AlloyFurnace(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(LIT, false));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		if (world.isClient)
			return ActionResult.SUCCESS;

		BlockEntity be = world.getBlockEntity(pos);
		if (be != null && be instanceof AlloyFurnaceEntity) {
			ContainerProviderRegistry.INSTANCE.openContainer(MRegister.ALLOY_FURNACE_CONTAINER, player,
					(packetByteBuf -> packetByteBuf.writeBlockPos(pos)));
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof AlloyFurnaceEntity) {
			((AlloyFurnaceEntity) be).dropEverything(world, pos);
		}
	}

	@Override
	public int getLuminance(BlockState state) {
		return state.get(LIT) ? 10 : 0;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction dir = Direction.fromHorizontal(ctx.getPlayerFacing().getOpposite().getHorizontal());
		return getDefaultState().with(HorizontalFacingBlock.FACING, dir);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(HorizontalFacingBlock.FACING, LIT);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return MRegister.ALLOY_FURNACE_ENTITY.instantiate();
	}

}
