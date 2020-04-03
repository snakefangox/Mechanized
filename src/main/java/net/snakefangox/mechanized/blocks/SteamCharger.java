package net.snakefangox.mechanized.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.blocks.entity.SteamChargerEntity;
import net.snakefangox.mechanized.steam.SteamItem;

public class SteamCharger extends Block implements BlockEntityProvider {

	private VoxelShape BOX = VoxelShapes.cuboid(0, 0, 0, 1, 0.75, 1);

	public SteamCharger(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		if (world.isClient)
			return ActionResult.SUCCESS;
		ItemStack held = player.getStackInHand(hand);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof SteamChargerEntity) {
			if (held.getItem() instanceof SteamItem && !held.isEmpty() && ((SteamChargerEntity) be).getItems().get(0).isEmpty()) {
				ItemStack inStack = held.copy();
				inStack.setCount(1);
				((SteamChargerEntity) be).getItems().set(0, inStack);
				held.decrement(1);
				((SteamChargerEntity) be).sync();
			}else if(held.isEmpty() && !((SteamChargerEntity) be).getItems().get(0).isEmpty()) {
				player.setStackInHand(hand, ((SteamChargerEntity) be).getItems().get(0));
				((SteamChargerEntity) be).setInvStack(0, ItemStack.EMPTY);
				((SteamChargerEntity) be).sync();
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return BOX;
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
		return MRegister.STEAM_CHARGER_ENTITY.instantiate();
	}

}
