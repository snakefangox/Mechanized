package net.snakefangox.mechanized.steam;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SteamUtil {

	public static void equalizeSteam(World world, Steam source, BlockPos pos, Direction side) {
		for (int i = 0; i < Direction.values().length; i++) {
			directionalEqualizeSteam(world, source, pos, side, Direction.values()[i]);
		}
	}
	
	public static void directionalEqualizeSteam(World world, Steam source, BlockPos pos, Direction side, Direction out) {
		BlockEntity be = world.getBlockEntity(pos.offset(out));
		if (be instanceof Steam) {
			float outFraction = ((Steam)be).getPressure(out.getOpposite());
			float sourceFraction = source.getPressure(side);
			float totalFraction = (sourceFraction - outFraction);
			if (totalFraction > 0) {
				((Steam)be).addSteam(out.getOpposite(),
						source.removeSteam(side, (int) (source.getSteamAmount(side) * totalFraction)));
			} else {
				source.addSteam(side, ((Steam)be).removeSteam(out.getOpposite(),
						(int) (((Steam)be).getSteamAmount(side) * -totalFraction)));
			}
		}
	}
	
}
