package com.nylofreeze;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import javax.inject.Inject;
import java.awt.*;

public class NyloFreezeOverlay extends Overlay
{
	private final Client client;
	private final NyloFreezeConfig config;
	private final FreezeCalculator freezeCalculator;
	private final PanelComponent panelComponent = new PanelComponent();

	@Inject
	private NyloFreezeOverlay(Client client, NyloFreezeConfig config, FreezeCalculator freezeCalculator)
	{
		this.client = client;
		this.config = config;
		this.freezeCalculator = freezeCalculator;
		setPosition(OverlayPosition.TOP_RIGHT);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// Calculate freeze chance
		int freezeChance = freezeCalculator.calculateFreezeChance();

		// Determine color based on freeze chance
		Color borderColor;
		if (freezeChance >= 100)
		{
			borderColor = Color.GREEN;
		}
		else if (freezeChance >= 90)
		{
			borderColor = Color.YELLOW;
		}
		else if (freezeChance >= 80)
		{
			borderColor = Color.ORANGE;
		}
		else
		{
			borderColor = Color.RED;
		}

		// Set panel border color
		panelComponent.setBackgroundColor(new Color(0, 0, 0, 150)); // Semi-transparent black
		panelComponent.setBorder(new Rectangle(2, 2, 2, 2));
		panelComponent.setPreferredSize(new Dimension(80, 40));

		// Draw the percentage text if enabled
		if (config.showPercentage())
		{
			graphics.setColor(borderColor);
			graphics.setFont(new Font("Arial", Font.BOLD, 16));
			graphics.drawString(freezeChance + "%", 10, 25);
		}

		return panelComponent.render(graphics);
	}
}