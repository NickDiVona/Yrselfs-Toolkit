package com.yrselfs.nylofreeze;

import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.client.game.ItemManager;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FreezeCalculator {
	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	public int calculateFreezeChance() {
		int boostedMagicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
		int realMagicLevel = client.getRealSkillLevel(Skill.MAGIC);
		int equipmentBonus = getEquipmentMagicBonus();

		double prayerMultiplier = getMagicPrayerMultiplier();
		boolean hasVoidMage = hasVoidMageSet();

		// Apply prayer bonus to boosted level, then add invisible +9
		double effectiveLevel = Math.floor(boostedMagicLevel * prayerMultiplier) + 9;

		double accuracyRoll;
		if (hasVoidMage) {
			accuracyRoll = effectiveLevel * (1.45 * equipmentBonus + 64);
		} else {
			accuracyRoll = effectiveLevel * (equipmentBonus + 64);
		}

		// Required roll for 100% freeze: (Base Magic Level + 9) * 204
		int requiredRoll = (realMagicLevel + 9) * 204;

		if (accuracyRoll >= requiredRoll) {
			return 100;
		}

		return (int) Math.min(100, (accuracyRoll / requiredRoll) * 100);
	}

	private double getMagicPrayerMultiplier() {
		if (isPrayerActive(Prayer.AUGURY)) {
			return 1.25;
		} else if (isPrayerActive(Prayer.MYSTIC_VIGOUR)) {
			return 1.18;
		} else if (isPrayerActive(Prayer.MYSTIC_MIGHT)) {
			return 1.15;
		} else if (isPrayerActive(Prayer.MYSTIC_LORE)) {
			return 1.10;
		} else if (isPrayerActive(Prayer.MYSTIC_WILL)) {
			return 1.05;
		}
		return 1.0;
	}

	private boolean isPrayerActive(Prayer prayer) {
		return client.getVarbitValue(prayer.getVarbit()) == 1;
	}

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

	// Checks for void mage helm + void top/bottom (elite or regular) + void gloves.
	// Both elite and regular void grant the same magic accuracy multiplier (1.45x).
	private boolean hasVoidMageSet() {
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null) {
			return false;
		}

		Item[] items = equipment.getItems();
		boolean hasHelm = hasItemInSlot(items, EquipmentInventorySlot.HEAD, 11663);
		boolean hasTop = hasItemInSlot(items, EquipmentInventorySlot.BODY, 13072)
				|| hasItemInSlot(items, EquipmentInventorySlot.BODY, 8839);
		boolean hasBottom = hasItemInSlot(items, EquipmentInventorySlot.LEGS, 13073)
				|| hasItemInSlot(items, EquipmentInventorySlot.LEGS, 8840);
		boolean hasGloves = hasItemInSlot(items, EquipmentInventorySlot.GLOVES, 8842);

		return hasHelm && hasTop && hasBottom && hasGloves;
	}

	private boolean hasItemInSlot(Item[] items, EquipmentInventorySlot slot, int itemId) {
		if (slot.getSlotIdx() >= items.length) {
			return false;
		}
		return items[slot.getSlotIdx()].getId() == itemId;
	}
}
