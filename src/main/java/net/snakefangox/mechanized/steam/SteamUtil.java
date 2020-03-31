package net.snakefangox.mechanized.steam;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SteamUtil {

	public static void equalizeSteam(World world, Steam source, BlockPos pos, Direction side) {
		Steam[] out = new Steam[6];
		for (int i = 0; i < Direction.values().length; i++) {
			BlockEntity be = world.getBlockEntity(pos.offset(Direction.values()[i]));
			if (be instanceof Steam) {
				out[i] = (Steam) be;
			}
		}

		for (int i = 0; i < out.length; i++) {
			if (out[i] == null)
				continue;
			float outFraction = out[i].getSteamAmount(Direction.values()[i].getOpposite())
					/ (float) out[i].getMaxSteamAmount(Direction.values()[i].getOpposite());
			float sourceFraction = source.getSteamAmount(side) / (float) source.getMaxSteamAmount(side);
			float totalFraction = (sourceFraction - outFraction);
			if (totalFraction > 0) {
				out[i].addSteam(Direction.values()[i].getOpposite(),
						source.removeSteam(side, (int) (source.getSteamAmount(side) * totalFraction)));
			} else {
				source.addSteam(side, out[i].removeSteam(Direction.values()[i].getOpposite(),
						(int) (out[i].getSteamAmount(side) * -totalFraction)));
			}
		}
	}
}
