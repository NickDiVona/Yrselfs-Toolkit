package com.yrselfs.toathralls;

import com.yrselfs.YrselfsToolkitConfig;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

@Singleton
public class ToaLobbyOverlay extends Overlay {
	private final ToaThrallChecker checker;
	private final YrselfsToolkitConfig config;
	private GameObject entrance;

	@Inject
	public ToaLobbyOverlay(ToaThrallChecker checker, YrselfsToolkitConfig config) {
		this.checker = checker;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	public void setEntrance(GameObject entrance) {
		this.entrance = entrance;
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if (entrance == null) {
			return null;
		}

		int line = 0;

		if (!"Arceuus".equals(checker.getSpellbookName())) {
			renderLine(graphics, "Spellbook: " + checker.getSpellbookName(), Color.RED, ++line);
		}

		if (config.checkThralls()) {
			if (!checker.hasBookOfTheDead()) {
				renderLine(graphics, "Book of the Dead: MISSING!", Color.RED, ++line);
			}
			String thrallStatus = checker.getThrallRuneStatus();
			if (thrallStatus != null) {
				renderLine(graphics, "Thrall Runes: " + thrallStatus, Color.RED, ++line);
			}
		}

		if (config.checkDeathCharge()) {
			String dcStatus = checker.getDeathChargeRuneStatus();
			if (dcStatus != null) {
				renderLine(graphics, "Death Charge: " + dcStatus, Color.RED, ++line);
			}
		}

		return null;
	}

	private void renderLine(Graphics2D graphics, String text, Color color, int lineNumber) {
		Point point = entrance.getCanvasTextLocation(graphics, text, lineNumber * 60);
		if (point != null) {
			OverlayUtil.renderTextLocation(graphics, point, text, color);
		}
	}
}
