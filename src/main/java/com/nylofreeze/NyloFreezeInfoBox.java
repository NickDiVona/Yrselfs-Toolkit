package com.nylofreeze;

import net.runelite.client.ui.overlay.infobox.InfoBox;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class NyloFreezeInfoBox extends InfoBox {
	private final NyloFreezeConfig config;
	private final FreezeCalculator freezeCalculator;
	private int freezeChance;

	public NyloFreezeInfoBox(BufferedImage image, NyloFreezeConfig config, FreezeCalculator freezeCalculator,
			NyloFreezePlugin plugin) {
		super(image, plugin);
		this.config = config;
		this.freezeCalculator = freezeCalculator;
		this.freezeChance = 0;
	}

	@Override
	public String getText() {
		// Update freeze chance
		freezeChance = freezeCalculator.calculateFreezeChance();

		// Show percentage if config enabled
		if (config.showPercentage()) {
			return freezeChance + "%";
		}
		return "";
	}

	@Override
	public Color getTextColor() {
		// Determine color based on freeze chance
		if (freezeChance >= 100) {
			return Color.GREEN;
		} else if (freezeChance >= 90) {
			return Color.YELLOW;
		} else if (freezeChance >= 80) {
			return Color.ORANGE;
		} else {
			return Color.RED;
		}
	}

	@Override
	public String getTooltip() {
		return "Nylo Freeze Chance: " + freezeChance + "%";
	}
}