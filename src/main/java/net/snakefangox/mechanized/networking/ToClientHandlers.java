package net.snakefangox.mechanized.networking;

import java.util.Random;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ToClientHandlers {

	private static final Random particle_rand = new Random();

	public static void initPacketHandlers() {

		ClientSidePacketRegistry.INSTANCE.register(PacketIdentifiers.FAN_PARTICLES, (packetContext, attachedData) -> {
			BlockPos pos = attachedData.readBlockPos();
			int facing = attachedData.readByte();
			int distence = attachedData.readByte();
			Direction dir = Direction.values()[facing];
			packetContext.getTaskQueue().execute(() -> {
				MinecraftClient.getInstance().player.clientWorld.addParticle(ParticleTypes.SMOKE,
						pos.getX() + particle_rand.nextFloat(), pos.getY() + particle_rand.nextFloat(),
						pos.getZ() + particle_rand.nextFloat(), dir.getOffsetX() * 0.5, dir.getOffsetY() * 0.5,
						dir.getOffsetZ() * 0.5);
				MinecraftClient.getInstance().player.clientWorld.addParticle(ParticleTypes.SMOKE,
						pos.getX() + particle_rand.nextFloat() + (dir.getOffsetX() * -distence),
						pos.getY() + particle_rand.nextFloat() + (dir.getOffsetY() * -distence),
						pos.getZ() + particle_rand.nextFloat() + (dir.getOffsetZ() * -distence), dir.getOffsetX() * 0.5,
						dir.getOffsetY() * 0.5, dir.getOffsetZ() * 0.5);
			});
		});

		ClientSidePacketRegistry.INSTANCE.register(PacketIdentifiers.VENT_PARTICLES, (packetContext, attachedData) -> {
			BlockPos pos = attachedData.readBlockPos();
			int facing = attachedData.readByte();
			Direction dir = Direction.values()[facing].getOpposite();
			packetContext.getTaskQueue().execute(() -> {
				MinecraftClient.getInstance().player.clientWorld.addParticle(ParticleTypes.SMOKE,
						pos.getX() + (0.5 + ((0.5 - particle_rand.nextFloat()) * 0.5)),
						pos.getY() + (0.5 + ((0.5 - particle_rand.nextFloat()) * 0.5)),
						pos.getZ() + (0.5 + ((0.5 - particle_rand.nextFloat()) * 0.5)), dir.getOffsetX() * 0.5,
						dir.getOffsetY() * 0.5, dir.getOffsetZ() * 0.5);
			});
		});

	}

}
