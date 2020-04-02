package net.snakefangox.mechanized.networking;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.mechanized.blocks.entity.PressureValveEntity;

public class ToServerHandlers {

	public static void initPacketHandlers() {
		ServerSidePacketRegistry.INSTANCE.register(PacketIdentifiers.SYNC_VALVE_PRESSURE, (packetContext, attachedData) -> {
			BlockPos pos = attachedData.readBlockPos();
			int psb = attachedData.readByte();
			packetContext.getTaskQueue().execute(() -> {
				BlockEntity be = packetContext.getPlayer().world.getBlockEntity(pos);
				if(be instanceof PressureValveEntity)
					((PressureValveEntity) be).setPressure(psb);
			});
		});
	}
}
