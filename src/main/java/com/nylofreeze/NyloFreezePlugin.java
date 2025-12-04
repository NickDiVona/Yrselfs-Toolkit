package com.nylofreeze;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.api.ItemID;

import java.awt.image.BufferedImage;

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
    private InfoBoxManager infoBoxManager;

    @Inject
    private FreezeCalculator freezeCalculator;

    @Inject
    private SpriteManager spriteManager;

    private NyloFreezeInfoBox infoBox;

    @Override
    protected void startUp() throws Exception
    {
        log.info("Nylo Freeze Indicator started!");
    }

    @Override
    protected void shutDown() throws Exception
    {
        log.info("Nylo Freeze Indicator stopped!");
        removeInfoBox();
    }

    @Provides
    NyloFreezeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(NyloFreezeConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {

				// TODO: remove aftertesting
				if (client.getLocalPlayer() != null)
    		{
        int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        log.info("Current region ID: {}", regionId);
    		}


        if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
        {
            removeInfoBox();
            return;
        }

        int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();

        if (inVerSinhazaOrToB(regionId))
        {
            if (infoBox == null)
            {
                spriteManager.getSpriteAsync(ItemID.ICE_ANCIENT_SCEPTRE, 0, icon -> {
                    if (icon == null)
                    {
                        icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                    }
                    infoBox = new NyloFreezeInfoBox(icon, config, freezeCalculator, this);
                    infoBoxManager.addInfoBox(infoBox);
                });
            }
        }
        else
        {
            removeInfoBox();
        }
    }

    private void removeInfoBox()
    {
        if (infoBox != null)
        {
            infoBoxManager.removeInfoBox(infoBox);
            infoBox = null;
        }
    }

    private boolean inVerSinhazaOrToB(int regionId)
    {
        // Ver Sinhaza bank + ToB regions (example IDs, adjust if needed)
        return regionId == 14642 || regionId == 14643 || regionId == 14898
            || (regionId >= 14642 && regionId <= 14646)
            || (regionId >= 14898 && regionId <= 14902);
    }
}
