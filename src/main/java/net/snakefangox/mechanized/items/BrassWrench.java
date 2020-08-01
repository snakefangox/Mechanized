package net.snakefangox.mechanized.items;

import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;

public class BrassWrench extends Item {

	public BrassWrench(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		if(ctx.getWorld().isClient()) return super.useOnBlock(ctx);
		BlockState state = ctx.getWorld().getBlockState(ctx.getBlockPos());
		//Get optional
		Optional<Direction> facing = state.method_28500(Properties.FACING);
		if (facing.isPresent()) {
			ctx.getWorld().setBlockState(ctx.getBlockPos(), state.cycle(Properties.FACING));
			return ActionResult.SUCCESS;
		}
		Optional<Direction> hfacing = state.method_28500(HorizontalFacingBlock.FACING);
		if (hfacing.isPresent()) {
			ctx.getWorld().setBlockState(ctx.getBlockPos(), state.cycle(HorizontalFacingBlock.FACING));
			return ActionResult.SUCCESS;
		}
		return super.useOnBlock(ctx);
	}
}
