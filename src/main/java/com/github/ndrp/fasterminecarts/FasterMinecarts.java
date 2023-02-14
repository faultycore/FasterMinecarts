package com.github.ndrp.fasterminecarts;

import com.github.ndrp.fasterminecarts.config.FasterMinecartsConfig;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class FasterMinecarts implements ModInitializer {
	@Override
	public void onInitialize() {
		AutoConfig.register(FasterMinecartsConfig.class, GsonConfigSerializer::new);
	}
}
