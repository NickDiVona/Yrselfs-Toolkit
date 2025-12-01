package com.nylofreeze;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Nylo Freeze Indicator",
	description = "Shows freeze chance for Nylocas Matomenos in ToB",
	tags = {"tob", "theatre", "maiden", "freeze", "nylo"}
)
public class NyloFreezePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private NyloFreezeConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NyloFreezeOverlay overlay;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Nylo Freeze Indicator started!");
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Nylo Freeze Indicator stopped!");
		overlayManager.remove(overlay);
	}

	@Provides
	NyloFreezeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NyloFreezeConfig.class);
	}
}