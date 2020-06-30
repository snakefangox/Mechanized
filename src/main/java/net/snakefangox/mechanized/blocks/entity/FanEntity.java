package net.snakefangox.mechanized.blocks.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.networking.PacketIdentifiers;
import net.snakefangox.mechanized.steam.Steam;
import net.snakefangox.mechanized.steam.SteamUtil;

import java.util.List;
import java.util.stream.Stream;

public class FanEntity extends AbstractSteamEntity {

	private static final int COST_PER_TICK = 1;
	private static final double VEL_MODIFIER = 0.1;
	private static final int STEAM_CAPACITY = Steam.UNIT;

	public FanEntity() {
		super(MRegister.FAN_ENTITY);
	}

	@Override
	public void tick() {
		if (world.isClient)
			return;
		if (world.getTime() % 5 == 0) {
			SteamUtil.equalizeSteam(world, this, pos, null);
			if(getSteamAmount(null) > 0 && world.getReceivedRedstonePower(pos) == 0) {
				Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(world,pos);
				PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
				passedData.writeBlockPos(pos);
				passedData.writeByte(getCachedState().get(Properties.FACING).getId());
				passedData.writeByte((int) ((int) Math.max(1, getPressurePSB(null) * 0.3) * 0.5));
				watchingPlayers.forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PacketIdentifiers.FAN_PARTICLES, passedData));
			}
		}
		Direction facing = getCachedState().get(Properties.FACING);
		if (getSteamAmount(null) > 0 && world.getReceivedRedstonePower(pos) == 0) {
			int pushDistance = (int) ((int) Math.max(1, getPressurePSB(null) * 0.3) * 0.5);
			List<Entity> entities = world.getEntities(null, new Box(pos.offset(facing, pushDistance).add(1, 1, 1),
					pos.offset(facing.getOpposite(), pushDistance)));
			entities.forEach(ent -> {
				double velX = facing.getOffsetX() * VEL_MODIFIER * getPressure(null);
				double velY = facing.getOffsetY() * VEL_MODIFIER * getPressure(null);
				double velZ = facing.getOffsetZ() * VEL_MODIFIER * getPressure(null);
				ent.addVelocity(velX, velY, velZ);
				ent.velocityModified = true;
			});
			removeSteam(null, COST_PER_TICK);
		}
	}

	@Override
	public int getMaxSteamAmount(Direction dir) {
		return STEAM_CAPACITY;
	}
}
