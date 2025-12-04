package com.nylofreeze;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("nylofreeze")
public interface NyloFreezeConfig extends Config {
	@ConfigItem(keyName = "showPercentage", name = "Show Percentage", description = "Display the freeze chance percentage in an infobox")
	default boolean showPercentage() {
		return true;
	}
}