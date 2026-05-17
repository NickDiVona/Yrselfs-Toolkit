package com.yrselfs;

import com.google.inject.Provides;
import com.yrselfs.nylofreeze.FreezeCalculator;
import com.yrselfs.nylofreeze.NyloFreezeInfoBox;
import com.yrselfs.toathralls.ToaLobbyOverlay;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Yrself's Toolkit",
	description = "Raid utilities — ToB freeze indicator, ToA lobby checks, and more",
	tags = {"tob", "toa", "theatre", "maiden", "freeze", "nylo", "tombs", "amascut", "thrall"}
)
public class YrselfsToolkitPlugin extends Plugin {
	private static final int MAIDEN_REGION = 12613;
	private static final int VER_SINHAZA_REGION = 14642;

	private static final int TOA_ENTRANCE_OBJECT_ID = 46089;

	@Inject private Client client;
	@Inject private YrselfsToolkitConfig config;
	@Inject private InfoBoxManager infoBoxManager;
	@Inject private OverlayManager overlayManager;
	@Inject private FreezeCalculator freezeCalculator;
	@Inject private ToaLobbyOverlay toaLobbyOverlay;

	private NyloFreezeInfoBox infoBox;

	@Override
	protected void startUp() {
		overlayManager.add(toaLobbyOverlay);
		log.info("Yrself's Toolkit started!");
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(toaLobbyOverlay);
		toaLobbyOverlay.setEntrance(null);
		removeInfoBox();
		log.info("Yrself's Toolkit stopped!");
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		if (event.getGameObject().getId() == TOA_ENTRANCE_OBJECT_ID) {
			toaLobbyOverlay.setEntrance(event.getGameObject());
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event) {
		if (event.getGameObject().getId() == TOA_ENTRANCE_OBJECT_ID) {
			toaLobbyOverlay.setEntrance(null);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOADING) {
			toaLobbyOverlay.setEntrance(null);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null) {
			removeInfoBox();
			return;
		}

		int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();

		if (isInToBOrBank(regionId) || config.showEverywhere()) {
			if (infoBox == null) {
				BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/nylo_icon.png");
				if (icon != null) {
					icon = ImageUtil.resizeImage(icon, 32, 32);
					infoBox = new NyloFreezeInfoBox(icon, freezeCalculator, this);
					infoBoxManager.addInfoBox(infoBox);
					infoBox.update();
				} else {
					log.warn("Failed to load nylo icon from resources");
				}
			} else {
				infoBox.update();
			}
		} else {
			removeInfoBox();
		}
	}

	private void removeInfoBox() {
		if (infoBox != null) {
			infoBoxManager.removeInfoBox(infoBox);
			infoBox = null;
		}
	}

	private boolean isInToBOrBank(int regionId) {
		return regionId == MAIDEN_REGION || regionId == VER_SINHAZA_REGION;
	}

	@Provides
	YrselfsToolkitConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(YrselfsToolkitConfig.class);
	}
}
