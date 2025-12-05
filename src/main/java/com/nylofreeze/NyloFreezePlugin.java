package com.nylofreeze;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(name = "Nylo Freeze Indicator", description = "Shows freeze chance for Nylocas Matomenos in ToB", tags = {
        "tob", "theatre", "maiden", "freeze", "nylo" })
public class NyloFreezePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private NyloFreezeConfig config;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private FreezeCalculator freezeCalculator;

    private NyloFreezeInfoBox infoBox;

    @Override
    protected void startUp() throws Exception {
        log.info("Nylo Freeze Indicator started!");

        // Create a simple red circle as the icon (placeholder for nylo)
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setColor(new Color(200, 50, 50)); // Dark red
        g.fillOval(4, 4, 24, 24); // Circle
        g.setColor(Color.BLACK);
        g.drawOval(4, 4, 24, 24); // Black outline
        g.dispose();

        // Create and add the infobox
        infoBox = new NyloFreezeInfoBox(icon, config, freezeCalculator, this);
        infoBoxManager.addInfoBox(infoBox);

        log.info("InfoBox added!");
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Nylo Freeze Indicator stopped!");

        // Remove the infobox
        if (infoBox != null) {
            infoBoxManager.removeInfoBox(infoBox);
            infoBox = null;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        // Force the InfoBox to update every game tick
        // This makes it recalculate when gear/prayers/potions change
        if (infoBox != null) {
            infoBox.update();
        }
    }

    @Provides
    NyloFreezeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(NyloFreezeConfig.class);
    }
}