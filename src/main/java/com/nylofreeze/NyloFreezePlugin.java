package com.nylofreeze;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.SpriteID;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(name = "Nylo Freeze Indicator", description = "Shows freeze chance for Nylocas Matomenos in ToB", tags = {
        "tob", "theatre", "maiden", "freeze", "nylo" })
public class NyloFreezePlugin extends Plugin {
    // Relevant Theatre of Blood region IDs
    private static final int MAIDEN_REGION = 12613;
    private static final int VER_SINHAZA_REGION = 14642;

    @Inject
    private Client client;

    @Inject
    private NyloFreezeConfig config;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private FreezeCalculator freezeCalculator;

    @Inject
    private SpriteManager spriteManager;

    private NyloFreezeInfoBox infoBox;

    @Override
    protected void startUp() throws Exception {
        log.info("Nylo Freeze Indicator started!");
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Nylo Freeze Indicator stopped!");
        removeInfoBox();
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null) {
            removeInfoBox();
            return;
        }

        int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();

        // Only show in the Maiden's room/hall or Ver Sinhaza
        if (isAtMaidenOrToBBank(regionId)) {
            if (infoBox == null) {
                // Load Ice Barrage spell icon
                spriteManager.getSpriteAsync(SpriteID.SPELL_ICE_BARRAGE, 0, this::createInfoBox);
            } else {
                // Update existing infobox
                infoBox.update();
            }
        } else {
            removeInfoBox();
        }
    }

    private void createInfoBox(BufferedImage icon) {
        if (icon == null) {
            log.warn("Failed to load Ice Barrage icon");
            return;
        }

        infoBox = new NyloFreezeInfoBox(icon, config, freezeCalculator, this);
        infoBoxManager.addInfoBox(infoBox);
        log.info("InfoBox created with Ice Barrage icon");
    }

    private void removeInfoBox() {
        if (infoBox != null) {
            infoBoxManager.removeInfoBox(infoBox);
            infoBox = null;
        }
    }

    private boolean isAtMaidenOrToBBank(int regionId) {
        return regionId == MAIDEN_REGION
                || regionId == VER_SINHAZA_REGION;
    }

    @Provides
    NyloFreezeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(NyloFreezeConfig.class);
    }
}