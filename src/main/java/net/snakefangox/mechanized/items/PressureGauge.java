package net.snakefangox.mechanized.items;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.snakefangox.mechanized.steam.Steam;

public class PressureGauge extends Item {

	private static final String PRESSURE_KEY = "mechanized.pressure_gauge";

	public PressureGauge(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (world.isClient)
			return ActionResult.SUCCESS;
		BlockEntity be = world.getBlockEntity(context.getBlockPos());
		if (be instanceof Steam && context.getPlayer() != null) {
			context.getPlayer().sendMessage(
					new TranslatableText(PRESSURE_KEY, ((Steam) be).getPressurePSBForReadout(context.getSide())), true);
			return ActionResult.SUCCESS;
		} else {
			return super.useOnBlock(context);
		}
	}

}
