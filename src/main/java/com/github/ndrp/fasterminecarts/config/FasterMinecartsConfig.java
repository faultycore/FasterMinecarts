package com.github.ndrp.fasterminecarts.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
@Config(name = "faster-minecarts")
public class FasterMinecartsConfig implements ConfigData {
	
	@ConfigEntry.Gui.Excluded
	private double slowSpeed = 0.3;

	@ConfigEntry.Gui.Tooltip(count = 2)
	@ConfigEntry.Category("default")
    private boolean automaticMinecartSlowDown = true;
	
	@ConfigEntry.Gui.Tooltip()
	@ConfigEntry.BoundedDiscrete(max = 12)
	@ConfigEntry.Category("default")
    private int maxSpeed = 8;
	
	public boolean getAutomaticMinecartSlowDown() {  return automaticMinecartSlowDown; }
	public double getSlowSpeed() { return slowSpeed; }
	public double getMaxSpeed() { return 0.4 + maxSpeed / 10D; }
}
