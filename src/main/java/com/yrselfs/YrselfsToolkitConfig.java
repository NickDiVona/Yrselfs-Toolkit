package com.yrselfs;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("yrselfs")
public interface YrselfsToolkitConfig extends Config {
	@ConfigSection(name = "ToB", description = "Theatre of Blood", position = 0)
	String tobSection = "tob";

	@ConfigSection(name = "ToA", description = "Tombs of Amascut", position = 1)
	String toaSection = "toa";

	@ConfigItem(keyName = "showEverywhere", name = "Show Everywhere", description = "Show the freeze chance infobox regardless of location", section = tobSection)
	default boolean showEverywhere() {
		return false;
	}

	@ConfigItem(
		keyName = "checkThralls",
		name = "Check Thralls",
		description = "Show Book of the Dead and rune checks for Thralls (Fire, Blood, Cosmic/Aether)",
		section = toaSection
	)
	default boolean checkThralls() {
		return true;
	}

	@ConfigItem(
		keyName = "checkDeathCharge",
		name = "Check Death Charge",
		description = "Show rune checks for Death Charge (Death, Blood, Soul/Aether)",
		section = toaSection
	)
	default boolean checkDeathCharge() {
		return true;
	}
}
