package net.snakefangox.mechanized.modcompat;

import autoswitch.api.AutoSwitchApi;
import autoswitch.api.AutoSwitchMap;
import autoswitch.api.DurabilityGetter;
import net.snakefangox.mechanized.MRegister;
import net.snakefangox.mechanized.items.SteamDrill;
import net.snakefangox.mechanized.items.SteamSaw;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class AutoSwitch implements AutoSwitchApi {
	@Override
	public void moddedTargets(AutoSwitchMap<String, Object> autoSwitchMap, AutoSwitchMap<String, String> autoSwitchMap1, AutoSwitchMap<String, String> autoSwitchMap2) {

	}

	@Override
	public void moddedToolGroups(AutoSwitchMap<String, Pair<Tag<Item>, Class<?>>> autoSwitchMap) {

	}

	@Override
	public void customDamageSystems(AutoSwitchMap<Class<?>, DurabilityGetter> autoSwitchMap) {
		autoSwitchMap.put(SteamDrill.class, MRegister.STEAM_DRILL::getSteamAmount);
		autoSwitchMap.put(SteamSaw.class, MRegister.STEAM_SAW::getSteamAmount);
	}
}
