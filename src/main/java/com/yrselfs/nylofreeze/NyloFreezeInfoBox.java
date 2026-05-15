package com.yrselfs.nylofreeze;

import com.yrselfs.YrselfsToolkitPlugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class NyloFreezeInfoBox extends InfoBox {
	private final FreezeCalculator freezeCalculator;
	private int freezeChance;

	public NyloFreezeInfoBox(BufferedImage image, FreezeCalculator freezeCalculator,
			@Nonnull YrselfsToolkitPlugin plugin) {
		super(image, plugin);
		this.freezeCalculator = freezeCalculator;
		this.freezeChance = 0;
	}

	public void update() {
		freezeChance = freezeCalculator.calculateFreezeChance();
	}

	@Override
	public String getText() {
		return freezeChance + "%";
	}

	@Override
	public Color getTextColor() {
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
