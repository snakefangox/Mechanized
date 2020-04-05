package net.snakefangox.mechanized.items;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.snakefangox.mechanized.steam.Steam;

public class PressureGauge extends Item {

	private static final String PRESSURE_KEY = "mechanized.pressure_gauge";

	public PressureGauge(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getWorld().isClient)
			return ActionResult.SUCCESS;
		BlockEntity be = context.getWorld().getBlockEntity(context.getBlockPos());
		if (be instanceof Steam) {
			context.getPlayer().addChatMessage(
					new TranslatableText(PRESSURE_KEY, ((Steam) be).getPressurePSBForReadout(context.getSide())), true);
			return ActionResult.SUCCESS;
		} else {
			return super.useOnBlock(context);
		}
	}

}
