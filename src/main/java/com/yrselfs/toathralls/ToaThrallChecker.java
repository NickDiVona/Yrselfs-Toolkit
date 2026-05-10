package com.yrselfs.toathralls;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ToaThrallChecker {
	// Spellbook varbit: 0=Standard, 1=Ancient, 2=Lunar, 3=Arceuus
	private static final int SPELLBOOK_VARBIT = 4070;

	private static final int BOOK_OF_THE_DEAD = 25818;

	static final int RUNE_POUCH = 12791;
	static final int DIVINE_RUNE_POUCH = 27281;

	// Rune pouch slots 1-3 — shared by both regular and divine rune pouch
	static final int[] POUCH_RUNE_VARBITS = {29,   1622, 1623};
	static final int[] POUCH_AMT_VARBITS  = {1624, 1625, 1626};

	// Divine rune pouch 4th slot only
	static final int DIVINE_SLOT4_RUNE_VARBIT = 14285;
	static final int DIVINE_SLOT4_AMT_VARBIT  = 14286;

	// Rune type indices stored in pouch varbits (not item IDs)
	static final int RUNE_FIRE   = 4;
	static final int RUNE_DEATH  = 7;
	static final int RUNE_BLOOD  = 8;
	static final int RUNE_COSMIC = 9;
	static final int RUNE_SOUL   = 13;
	// Aether rune counts as both Cosmic and Soul — TODO: verify pouch varbit index in-game
	static final int RUNE_AETHER = 22;

	// Item IDs for loose runes carried in inventory — TODO: verify Aether item ID in-game
	static final int ITEM_FIRE_RUNE   = 554;
	static final int ITEM_BLOOD_RUNE  = 565;
	static final int ITEM_COSMIC_RUNE = 564;
	static final int ITEM_DEATH_RUNE  = 560;
	static final int ITEM_SOUL_RUNE   = 566;
	static final int ITEM_AETHER_RUNE = 30843; // TODO: confirm — item examiner showed this but a compost potion was overlapping

	@Inject
	private Client client;

	public String getSpellbookName() {
		switch (client.getVarbitValue(SPELLBOOK_VARBIT)) {
			case 0: return "Standard";
			case 1: return "Ancient";
			case 2: return "Lunar";
			case 3: return "Arceuus";
			default: return "Unknown";
		}
	}

	public boolean hasBookOfTheDead() {
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		return (inventory != null && inventory.contains(BOOK_OF_THE_DEAD))
				|| (equipment != null && equipment.contains(BOOK_OF_THE_DEAD));
	}

	/** Returns null if all thrall runes (Fire, Blood, Cosmic/Aether) are present. */
	public String getThrallRuneStatus() {
		if (!hasRune(RUNE_FIRE,   ITEM_FIRE_RUNE))   return "No Fire Runes!";
		if (!hasRune(RUNE_BLOOD,  ITEM_BLOOD_RUNE))  return "No Blood Runes!";
		if (!hasRune(RUNE_COSMIC, ITEM_COSMIC_RUNE) && !hasRune(RUNE_AETHER, ITEM_AETHER_RUNE))
			return "No Cosmic Runes!";
		return null;
	}

	/** Returns null if all Death Charge runes (Death, Blood, Soul/Aether) are present. */
	public String getDeathChargeRuneStatus() {
		if (!hasRune(RUNE_DEATH, ITEM_DEATH_RUNE))  return "No Death Runes!";
		if (!hasRune(RUNE_BLOOD, ITEM_BLOOD_RUNE))  return "No Blood Runes!";
		if (!hasRune(RUNE_SOUL,  ITEM_SOUL_RUNE) && !hasRune(RUNE_AETHER, ITEM_AETHER_RUNE))
			return "No Soul Runes!";
		return null;
	}

	/**
	 * Returns true if the given rune is found loose in the inventory OR stored in
	 * any rune pouch or divine rune pouch the player is carrying.
	 */
	boolean hasRune(int pouchType, int itemId) {
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		if (inventory == null) return false;

		if (inventory.contains(itemId)) return true;

		boolean hasRegular = inventory.contains(RUNE_POUCH);
		boolean hasDivine = inventory.contains(DIVINE_RUNE_POUCH);

		if (hasRegular || hasDivine) {
			for (int i = 0; i < POUCH_RUNE_VARBITS.length; i++) {
				if (client.getVarbitValue(POUCH_RUNE_VARBITS[i]) == pouchType
						&& client.getVarbitValue(POUCH_AMT_VARBITS[i]) > 0) {
					return true;
				}
			}
		}

		if (hasDivine) {
			if (client.getVarbitValue(DIVINE_SLOT4_RUNE_VARBIT) == pouchType
					&& client.getVarbitValue(DIVINE_SLOT4_AMT_VARBIT) > 0) {
				return true;
			}
		}

		return false;
	}
}
