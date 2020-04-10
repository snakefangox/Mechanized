package net.snakefangox.mechanized.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FlyingBlockEntity extends FallingBlockEntity {

	public FlyingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, World world) {
		super(entityType, world);
	}

	public FlyingBlockEntity(World world, double d, double e, double f, BlockState state) {
		super(world, d, e, f, state);
	}
	
	@Override
	public void tick() {
		super.tick();
		if(timeFalling > 10)
			timeFalling = 10;
		if(world instanceof ServerWorld && isSpawnChunk(world, getBlockPos(), getServer())) {
			remove();
		}
	}
	
	public static boolean isSpawnChunk(World world, BlockPos pos, MinecraftServer server) {
		BlockPos blockPos2 = world.getSpawnPos();
        int i = MathHelper.abs(pos.getX() - blockPos2.getX());
        int j = MathHelper.abs(pos.getZ() - blockPos2.getZ());
        int k = Math.max(i, j);
        return k <= server.getSpawnProtectionRadius();
	}

}
