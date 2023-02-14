package com.github.ndrp.fasterminecarts.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "faster-minecarts")
public class FasterMinecartsConfig implements ConfigData {

	
	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.Category("default")
    public boolean automaticMinecartSlowDown = true;

	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.Category("default")
	public boolean soulSandSlowDown = true;
	
	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.BoundedDiscrete(min=8, max = 32)
	@ConfigEntry.Category("default")
    public int maxSpeed = 30;

	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.BoundedDiscrete(min = 1, max = 8)
	@ConfigEntry.Category("default")
	public int slowSpeed = 6;
}
