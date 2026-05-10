package com.yrselfs.toathralls;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.yrselfs.toathralls.ToaThrallChecker.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ToaThrallCheckerTest {
	@Mock private Client client;
	@InjectMocks private ToaThrallChecker checker;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(client.getVarbitValue(anyInt())).thenReturn(0);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(null);
		when(client.getItemContainer(InventoryID.EQUIPMENT)).thenReturn(null);
	}

	// --- Spellbook ---

	@Test
	public void getSpellbookName_returnsArceuusWhenVarbitIsThree() {
		when(client.getVarbitValue(4070)).thenReturn(3);
		assertEquals("Arceuus", checker.getSpellbookName());
	}

	@Test
	public void getSpellbookName_returnsStandardWhenVarbitIsZero() {
		when(client.getVarbitValue(4070)).thenReturn(0);
		assertEquals("Standard", checker.getSpellbookName());
	}

	@Test
	public void getSpellbookName_returnsAncientWhenVarbitIsOne() {
		when(client.getVarbitValue(4070)).thenReturn(1);
		assertEquals("Ancient", checker.getSpellbookName());
	}

	@Test
	public void getSpellbookName_returnsLunarWhenVarbitIsTwo() {
		when(client.getVarbitValue(4070)).thenReturn(2);
		assertEquals("Lunar", checker.getSpellbookName());
	}

	// --- Book of the Dead ---

	@Test
	public void hasBookOfTheDead_trueWhenInInventory() {
		ItemContainer inventory = mockContainerContaining(25818);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inventory);
		assertTrue(checker.hasBookOfTheDead());
	}

	@Test
	public void hasBookOfTheDead_trueWhenEquipped() {
		ItemContainer equipment = mockContainerContaining(25818);
		when(client.getItemContainer(InventoryID.EQUIPMENT)).thenReturn(equipment);
		assertTrue(checker.hasBookOfTheDead());
	}

	@Test
	public void hasBookOfTheDead_falseWhenAbsent() {
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(mockEmptyContainer());
		when(client.getItemContainer(InventoryID.EQUIPMENT)).thenReturn(mockEmptyContainer());
		assertFalse(checker.hasBookOfTheDead());
	}

	// --- Thrall rune status ---

	@Test
	public void getThrallRuneStatus_missingFireRunes_returnsMessage() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_BLOOD);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(200);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[1])).thenReturn(RUNE_COSMIC);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[1])).thenReturn(100);
		assertEquals("No Fire Runes!", checker.getThrallRuneStatus());
	}

	@Test
	public void getThrallRuneStatus_missingBloodRunes_returnsMessage() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_FIRE);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(500);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[1])).thenReturn(RUNE_COSMIC);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[1])).thenReturn(100);
		assertEquals("No Blood Runes!", checker.getThrallRuneStatus());
	}

	@Test
	public void getThrallRuneStatus_missingCosmicRunes_returnsMessage() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_FIRE);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(500);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[1])).thenReturn(RUNE_BLOOD);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[1])).thenReturn(200);
		assertEquals("No Cosmic Runes!", checker.getThrallRuneStatus());
	}

	@Test
	public void getThrallRuneStatus_allRunesInPouch_returnsNull() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_FIRE);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(500);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[1])).thenReturn(RUNE_BLOOD);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[1])).thenReturn(200);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[2])).thenReturn(RUNE_COSMIC);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[2])).thenReturn(100);
		assertNull(checker.getThrallRuneStatus());
	}

	@Test
	public void getThrallRuneStatus_divinePouch4thSlot_returnsNull() {
		ItemContainer inv = mockPouchContainer(DIVINE_RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_FIRE);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(500);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[1])).thenReturn(RUNE_BLOOD);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[1])).thenReturn(200);
		when(client.getVarbitValue(DIVINE_SLOT4_RUNE_VARBIT)).thenReturn(RUNE_COSMIC);
		when(client.getVarbitValue(DIVINE_SLOT4_AMT_VARBIT)).thenReturn(100);
		assertNull(checker.getThrallRuneStatus());
	}

	@Test
	public void getThrallRuneStatus_aetherSatisfiesCosmic_returnsNull() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_FIRE);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(500);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[1])).thenReturn(RUNE_BLOOD);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[1])).thenReturn(200);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[2])).thenReturn(RUNE_AETHER);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[2])).thenReturn(50);
		assertNull(checker.getThrallRuneStatus());
	}

	@Test
	public void getThrallRuneStatus_looseRunesInInventory_returnsNull() {
		ItemContainer inventory = mock(ItemContainer.class);
		when(inventory.contains(ITEM_FIRE_RUNE)).thenReturn(true);
		when(inventory.contains(ITEM_BLOOD_RUNE)).thenReturn(true);
		when(inventory.contains(ITEM_COSMIC_RUNE)).thenReturn(true);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inventory);
		assertNull(checker.getThrallRuneStatus());
	}

	@Test
	public void getThrallRuneStatus_noPouchNoRunes_returnsMessage() {
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(mockEmptyContainer());
		assertEquals("No Fire Runes!", checker.getThrallRuneStatus());
	}

	// --- Death Charge rune status ---

	@Test
	public void getDeathChargeRuneStatus_missingDeathRunes_returnsMessage() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_BLOOD);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(200);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[1])).thenReturn(RUNE_SOUL);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[1])).thenReturn(100);
		assertEquals("No Death Runes!", checker.getDeathChargeRuneStatus());
	}

	@Test
	public void getDeathChargeRuneStatus_missingBloodRunes_returnsMessage() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_DEATH);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(200);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[1])).thenReturn(RUNE_SOUL);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[1])).thenReturn(100);
		assertEquals("No Blood Runes!", checker.getDeathChargeRuneStatus());
	}

	@Test
	public void getDeathChargeRuneStatus_missingSoulRunes_returnsMessage() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_DEATH);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(200);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[1])).thenReturn(RUNE_BLOOD);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[1])).thenReturn(200);
		assertEquals("No Soul Runes!", checker.getDeathChargeRuneStatus());
	}

	@Test
	public void getDeathChargeRuneStatus_allRunesInPouch_returnsNull() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_DEATH);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(200);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[1])).thenReturn(RUNE_BLOOD);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[1])).thenReturn(200);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[2])).thenReturn(RUNE_SOUL);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[2])).thenReturn(100);
		assertNull(checker.getDeathChargeRuneStatus());
	}

	@Test
	public void getDeathChargeRuneStatus_aetherSatisfiesSoul_returnsNull() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_DEATH);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(200);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[1])).thenReturn(RUNE_BLOOD);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[1])).thenReturn(200);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[2])).thenReturn(RUNE_AETHER);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[2])).thenReturn(50);
		assertNull(checker.getDeathChargeRuneStatus());
	}

	// --- hasRune helper ---

	@Test
	public void hasRune_findsRuneInPouchSlot() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[2])).thenReturn(RUNE_BLOOD);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[2])).thenReturn(50);
		assertTrue(checker.hasRune(RUNE_BLOOD, ITEM_BLOOD_RUNE));
	}

	@Test
	public void hasRune_returnsFalseWhenAmountIsZero() {
		ItemContainer inv = mockPouchContainer(RUNE_POUCH);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inv);
		when(client.getVarbitValue(POUCH_RUNE_VARBITS[0])).thenReturn(RUNE_FIRE);
		when(client.getVarbitValue(POUCH_AMT_VARBITS[0])).thenReturn(0);
		assertFalse(checker.hasRune(RUNE_FIRE, ITEM_FIRE_RUNE));
	}

	@Test
	public void hasRune_returnsFalseWithNoPouchAndNoLooseRune() {
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(mockEmptyContainer());
		assertFalse(checker.hasRune(RUNE_FIRE, ITEM_FIRE_RUNE));
	}

	@Test
	public void hasRune_findsLooseRuneInInventory() {
		ItemContainer inventory = mock(ItemContainer.class);
		when(inventory.contains(ITEM_FIRE_RUNE)).thenReturn(true);
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(inventory);
		assertTrue(checker.hasRune(RUNE_FIRE, ITEM_FIRE_RUNE));
	}

	@Test
	public void hasRune_returnsFalseWhenInventoryNull() {
		when(client.getItemContainer(InventoryID.INVENTORY)).thenReturn(null);
		assertFalse(checker.hasRune(RUNE_FIRE, ITEM_FIRE_RUNE));
	}

	// --- Helpers ---

	private ItemContainer mockPouchContainer(int pouchItemId) {
		ItemContainer container = mock(ItemContainer.class);
		when(container.contains(pouchItemId)).thenReturn(true);
		return container;
	}

	private ItemContainer mockContainerContaining(int itemId) {
		ItemContainer container = mock(ItemContainer.class);
		when(container.contains(itemId)).thenReturn(true);
		return container;
	}

	private ItemContainer mockEmptyContainer() {
		return mock(ItemContainer.class);
	}
}
