# Yrself's Toolkit

Raid utilities for Theatre of Blood and Tombs of Amascut.

## Features

### Theatre of Blood — Maiden Freeze Chance
Displays an infobox while you're in Maiden (or the Ver Sinhaza bank) showing the probability your current magic level and gear can freeze a Nylo. Accounts for magic prayer and the Mystic Vigour scroll effect.

### Tombs of Amascut — Lobby Checks
Shows an overlay on the ToA entrance wall while you're in the lobby. Each check can be toggled independently in the plugin config under the **ToA** section.

**Check Thralls** — verifies you have everything needed to summon thralls:
- Spellbook is Arceuus
- Book of the Dead is carried (inventory or equipped)
- Fire, Blood, and Cosmic runes present (Aether counts as Cosmic)

**Check Death Charge** — verifies you have the runes to cast Death Charge:
- Death, Blood, and Soul runes present (Aether counts as Soul)

Runes are detected loose in your inventory or stored inside a regular or divine rune pouch.

## Development

Build and launch a local RuneLite client with the plugin loaded:

```bash
./gradlew shadowJar
java -jar build/libs/yrselfs-toolkit-1.0-SNAPSHOT-all.jar
```

Or use the **Yrself's Toolkit** launch configuration in VS Code.

> **Note:** The divine rune pouch varbits and Aether rune IDs are marked with `TODO: verify in-game` comments in `ToaThrallChecker.java` — confirm them with RuneLite's varbit/item inspector before shipping.

## Thanks

Coley, Bombology, and based batt for help testing and providing IDs.
