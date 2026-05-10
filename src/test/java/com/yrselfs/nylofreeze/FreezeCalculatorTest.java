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
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FreezeCalculatorTest {
	@Mock private Client client;
	@Mock private ItemManager itemManager;
	@InjectMocks private FreezeCalculator calculator;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		// defaults: no prayers, no equipment
		when(client.getVarbitValue(anyInt())).thenReturn(0);
		when(client.getItemContainer(InventoryID.EQUIPMENT)).thenReturn(null);
	}

	// --- Base formula ---

	@Test
	public void noGearNoPrayer_returns31Percent() {
		// effectiveLevel = floor(99 * 1.0) + 9 = 108
		// accuracyRoll   = 108 * 64 = 6912
		// requiredRoll   = (99 + 9) * 204 = 22032
		// chance         = 6912/22032 * 100 = 31.37 -> 31
		given99Magic();
		assertEquals(31, calculator.calculateFreezeChance());
	}

	@Test
	public void capAt100_whenAccuracyExceedsRequired() {
		// boosted=200 (super-potioned), real=1 (de-levelled)
		// effectiveLevel = 209, accuracyRoll = 209 * 64 = 13376
		// requiredRoll   = (1 + 9) * 204 = 2040  ->  13376 >= 2040
		when(client.getBoostedSkillLevel(Skill.MAGIC)).thenReturn(200);
		when(client.getRealSkillLevel(Skill.MAGIC)).thenReturn(1);
		assertEquals(100, calculator.calculateFreezeChance());
	}

	// --- Prayer multipliers ---

	@Test
	public void augury_appliesOnePointTwoFiveMultiplier() {
		// effectiveLevel = floor(99 * 1.25) + 9 = floor(123.75) + 9 = 132
		// accuracyRoll   = 132 * 64 = 8448
		// chance         = 8448/22032 * 100 = 38.34 -> 38
		given99Magic();
		when(client.getVarbitValue(Prayer.AUGURY.getVarbit())).thenReturn(1);
		assertEquals(38, calculator.calculateFreezeChance());
	}

	@Test
	public void mysticVigour_appliesOnePointOneEightMultiplier() {
		// effectiveLevel = floor(99 * 1.18) + 9 = floor(116.82) + 9 = 125
		// accuracyRoll   = 125 * 64 = 8000
		// chance         = 8000/22032 * 100 = 36.30 -> 36
		given99Magic();
		when(client.getVarbitValue(Prayer.MYSTIC_VIGOUR.getVarbit())).thenReturn(1);
		assertEquals(36, calculator.calculateFreezeChance());
	}

	@Test
	public void augury_takesPriorityOverMysticVigour() {
		given99Magic();
		when(client.getVarbitValue(Prayer.AUGURY.getVarbit())).thenReturn(1);
		when(client.getVarbitValue(Prayer.MYSTIC_VIGOUR.getVarbit())).thenReturn(1);
		// should behave identically to Augury-only (38%)
		assertEquals(38, calculator.calculateFreezeChance());
	}

	// --- Void mage set ---

	@Test
	public void voidSet_appliesOnePointFourFiveEquipmentMultiplier() {
		// Each of the 4 void pieces contributes +10 magic -> equipmentBonus = 40
		// accuracyRoll (void)    = 108 * (1.45 * 40 + 64) = 108 * 122 = 13176
		// accuracyRoll (no void) = 108 * (40 + 64)        = 108 * 104 = 11232
		// chance (void)          = 13176/22032 * 100 = 59.80 -> 59
		given99Magic();
		ItemContainer voidGear = mockVoidEquipment(10);
		when(client.getItemContainer(InventoryID.EQUIPMENT)).thenReturn(voidGear);
		assertEquals(59, calculator.calculateFreezeChance());
	}

	@Test
	public void noVoidSet_usesStandardEquipmentMultiplier() {
		// Same +40 bonus from non-void items (no void detection -> standard formula)
		// accuracyRoll = 108 * 104 = 11232
		// chance       = 11232/22032 * 100 = 50.98 -> 50
		given99Magic();
		ItemContainer nonVoidGear = mockNonVoidEquipment(40);
		when(client.getItemContainer(InventoryID.EQUIPMENT)).thenReturn(nonVoidGear);
		assertEquals(50, calculator.calculateFreezeChance());
	}

	// --- Helpers ---

	private void given99Magic() {
		when(client.getBoostedSkillLevel(Skill.MAGIC)).thenReturn(99);
		when(client.getRealSkillLevel(Skill.MAGIC)).thenReturn(99);
	}

	/**
	 * Builds a mock equipment container wearing the full void mage set.
	 * Each piece returns the given magic attack bonus from ItemManager.
	 */
	private ItemContainer mockVoidEquipment(int magicBonusPerPiece) {
		Item[] items = emptyItemArray(13);
		when(items[EquipmentInventorySlot.HEAD.getSlotIdx()].getId()).thenReturn(11663);  // void mage helm
		when(items[EquipmentInventorySlot.BODY.getSlotIdx()].getId()).thenReturn(8839);   // void top
		when(items[EquipmentInventorySlot.LEGS.getSlotIdx()].getId()).thenReturn(8840);   // void bottom
		when(items[EquipmentInventorySlot.GLOVES.getSlotIdx()].getId()).thenReturn(8842); // void gloves

		stubMagicBonus(magicBonusPerPiece);

		ItemContainer container = mock(ItemContainer.class);
		when(container.getItems()).thenReturn(items);
		return container;
	}

	/**
	 * Builds a mock equipment container with a single item in the weapon slot
	 * giving the specified total magic bonus. No void pieces — void check fails.
	 */
	private ItemContainer mockNonVoidEquipment(int totalMagicBonus) {
		Item[] items = emptyItemArray(13);
		when(items[EquipmentInventorySlot.WEAPON.getSlotIdx()].getId()).thenReturn(1234);

		stubMagicBonus(totalMagicBonus);

		ItemContainer container = mock(ItemContainer.class);
		when(container.getItems()).thenReturn(items);
		return container;
	}

	private Item[] emptyItemArray(int size) {
		Item[] items = new Item[size];
		for (int i = 0; i < size; i++) {
			items[i] = mock(Item.class);
			when(items[i].getId()).thenReturn(-1);
		}
		return items;
	}

	private void stubMagicBonus(int bonus) {
		ItemStats stats = mock(ItemStats.class);
		ItemEquipmentStats equipStats = mock(ItemEquipmentStats.class);
		when(itemManager.getItemStats(anyInt(), anyBoolean())).thenReturn(stats);
		when(stats.getEquipment()).thenReturn(equipStats);
		when(equipStats.getAmagic()).thenReturn(bonus);
	}
}
