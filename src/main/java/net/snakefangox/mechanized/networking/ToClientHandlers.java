package net.snakefangox.mechanized.networking;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Random;

public class ToClientHandlers {

	private static final Random particle_rand = new Random();

	public static void initPacketHandlers() {

		MinecraftClient client = MinecraftClient.getInstance();
		
		ClientSidePacketRegistry.INSTANCE.register(PacketIdentifiers.FAN_PARTICLES, (packetContext, attachedData) -> {
			BlockPos pos = attachedData.readBlockPos();
			int facing = attachedData.readByte();
			int distence = attachedData.readByte();
			Direction dir = Direction.values()[facing];
			packetContext.getTaskQueue().execute(() -> {
				client.player.clientWorld.addParticle(ParticleTypes.SMOKE,
						pos.getX() + particle_rand.nextFloat(), pos.getY() + particle_rand.nextFloat(),
						pos.getZ() + particle_rand.nextFloat(), dir.getOffsetX() * 0.5, dir.getOffsetY() * 0.5,
						dir.getOffsetZ() * 0.5);
				client.player.clientWorld.addParticle(ParticleTypes.SMOKE,
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
				client.player.clientWorld.addParticle(ParticleTypes.SMOKE,
						pos.getX() + (0.5 + ((0.5 - particle_rand.nextFloat()) * 0.5)),
						pos.getY() + (0.5 + ((0.5 - particle_rand.nextFloat()) * 0.5)),
						pos.getZ() + (0.5 + ((0.5 - particle_rand.nextFloat()) * 0.5)), dir.getOffsetX() * 0.5,
						dir.getOffsetY() * 0.5, dir.getOffsetZ() * 0.5);
			});
		});
		
		ClientSidePacketRegistry.INSTANCE.register(PacketIdentifiers.SYNC_CURSER_STACK, (packetContext, attachedData) -> {
			ItemStack item = attachedData.readItemStack();
			packetContext.getTaskQueue().execute(() -> {
				client.player.inventory.setCursorStack(item);
			});
		});

	}

}
