package net.snakefangox.mechanized.networking;

import net.minecraft.util.Identifier;
import net.snakefangox.mechanized.Mechanized;

public class PacketIdentifiers {

	public static final Identifier FAN_PARTICLES = new Identifier(Mechanized.MODID, "fanparticle");
	public static final Identifier VENT_PARTICLES = new Identifier(Mechanized.MODID, "ventparticle");
	public static final Identifier SYNC_VALVE_PRESSURE = new Identifier(Mechanized.MODID, "valvepressure");
	public static final Identifier SYNC_CURSER_STACK = new Identifier(Mechanized.MODID, "curserstack");
}
