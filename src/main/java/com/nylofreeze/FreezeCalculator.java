package com.nylofreeze;

import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.game.ItemManager;
import net.runelite.http.api.item.ItemStats;
import net.runelite.http.api.item.ItemEquipmentStats;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FreezeCalculator {
	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	/**
	 * Calculate the freeze chance percentage against Nylocas Matomenos
	 */
	public int calculateFreezeChance() {
		// Use BOOSTED magic level (includes potions, brews, etc.)
		int baseMagicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
		int equipmentBonus = getEquipmentMagicBonus();

		// Check all magic prayers, not just Augury
		double prayerMultiplier = getMagicPrayerMultiplier();
		boolean hasEliteVoid = hasEliteVoidSet();

		// Calculate effective magic level
		// Step 1: Apply prayer bonus to base level FIRST
		double effectiveLevel = Math.floor(baseMagicLevel * prayerMultiplier);

		// Step 2: Add invisible +9 boost
		effectiveLevel += 9;

		// Calculate accuracy roll
		double accuracyRoll;

		if (hasEliteVoid) {
			// Void: 1.45 * (equipment + 64) gives effective bonus, then multiply by
			// effective level
			accuracyRoll = effectiveLevel * (1.45 * equipmentBonus + 64);
		} else {
			// Normal: effective level * (equipment + 64)
			accuracyRoll = effectiveLevel * (equipmentBonus + 64);
		}

		// Calculate required roll for 100% freeze
		// Formula from wiki: (Base Magic Level + 9) * 204
		// Note: Use the CURRENT boosted level, not base 99
		int requiredRoll = (baseMagicLevel + 9) * 204;

		// Calculate freeze chance
		if (accuracyRoll >= requiredRoll) {
			return 100;
		}

		// Below threshold, calculate percentage
		return (int) Math.min(100, (accuracyRoll / requiredRoll) * 100);
	}

	/**
	 * Get the magic prayer multiplier based on active prayers
	 */
	private double getMagicPrayerMultiplier() {
		// Check prayers in order of strength (highest first)
		if (client.isPrayerActive(Prayer.AUGURY)) {
			return 1.25; // +25%
		} else if (client.isPrayerActive(Prayer.MYSTIC_MIGHT)) {
			return 1.15; // +15%
		} else if (client.isPrayerActive(Prayer.MYSTIC_LORE)) {
			return 1.10; // +10%
		} else if (client.isPrayerActive(Prayer.MYSTIC_WILL)) {
			return 1.05; // +5%
		}

		return 1.0; // No prayer active
	}

	/**
	 * Get total magic attack bonus from equipped items
	 */
	private int getEquipmentMagicBonus() {
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null) {
			return 0;
		}

		int totalBonus = 0;
		for (Item item : equipment.getItems()) {
			if (item.getId() == -1) {
				continue;
			}

			totalBonus += getMagicBonusForItem(item.getId());
		}

		return totalBonus;
	}

	/**
	 * Get magic attack bonus for a specific item
	 */
	private int getMagicBonusForItem(int itemId) {
		ItemStats itemStats = itemManager.getItemStats(itemId, false);
		if (itemStats == null) {
			return 0;
		}

		ItemEquipmentStats equipment = itemStats.getEquipment();
		if (equipment == null) {
			return 0;
		}

		return equipment.getAmagic();
	}

	/**
	 * Check if player is wearing Elite Void mage set
	 */
	private boolean hasEliteVoidSet() {
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null) {
			return false;
		}

		Item[] items = equipment.getItems();

		// Check for void mage helm (11663)
		boolean hasHelm = hasItemInSlot(items, EquipmentInventorySlot.HEAD, 11663);

		// Check for elite void top (13072) or regular void top (8839)
		boolean hasTop = hasItemInSlot(items, EquipmentInventorySlot.BODY, 13072) ||
				hasItemInSlot(items, EquipmentInventorySlot.BODY, 8839);

		// Check for elite void bottom (13073) or regular void bottom (8840)
		boolean hasBottom = hasItemInSlot(items, EquipmentInventorySlot.LEGS, 13073) ||
				hasItemInSlot(items, EquipmentInventorySlot.LEGS, 8840);

		// Check for void gloves (8842)
		boolean hasGloves = hasItemInSlot(items, EquipmentInventorySlot.GLOVES, 8842);

		return hasHelm && hasTop && hasBottom && hasGloves;
	}

	/**
	 * Helper to check if a specific item is in an equipment slot
	 */
	private boolean hasItemInSlot(Item[] items, EquipmentInventorySlot slot, int itemId) {
		if (slot.getSlotIdx() >= items.length) {
			return false;
		}
		return items[slot.getSlotIdx()].getId() == itemId;
	}
}