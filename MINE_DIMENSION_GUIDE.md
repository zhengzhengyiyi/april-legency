# Complete Guide to Mine Dimensions in April's Legacy

## Overview
The Mine system is a roguelike dimension-based gameplay mechanic from Minecraft's 25w14craftmine April Fools snapshot, now fully integrated into April's Legacy mod. Players craft custom mine dimensions, complete objectives, and earn rewards.

---

## Getting Started

### Required Items
1. **Mine Crafter** - The main crafting block (crafted or found)
2. **Mine Ingredients** - Special items that modify mine properties
3. **Mine Item** - The generated mine token

### Basic Workflow
```
Craft Mine → Enter Mine → Complete Objectives → Exit → Collect Rewards
```

---

## Step-by-Step Guide

### 1. Obtaining a Mine Crafter

The Mine Crafter is the core block for creating mines. You can:
- **Craft it** (if recipe is enabled)
- **Find it** in generated structures
- **Get it from creative inventory** (Functional tab)

**Block ID**: `minecraft:mine_crafter`

### 2. Collecting Mine Ingredients

Mine Ingredients are special items that determine what your mine will contain. They can be found by:

- **Killing specific mobs** (drops ingredients related to that mob)
- **Mining specific blocks** (drops biome/block-related ingredients)
- **Completing achievements** (unlocks new ingredients)
- **Exploring biomes** (biome-specific ingredients)
- **Winning previous mines** (unlocks advanced ingredients)

**Common Starting Ingredients**:
- Plains Biomes (common)
- Forest Biomes (break leaves)
- Zombies (kill zombies)
- Skeletons (kill skeletons)
- Surface Exits (default)

**Item ID**: `minecraft:mine_ingredient`

### 3. Crafting a Mine

1. **Place the Mine Crafter** block in the world
2. **Right-click** to open the Mine Crafter GUI
3. **Add Mine Ingredients** to the slots (1-50 slots depending on level)
4. **Review the effects** shown in the interface
5. **Click "Craft"** to generate the mine
6. A **Mine Portal** will spawn above the Mine Crafter
7. You'll receive a **Mine Item** in your inventory

**GUI Features**:
- **Ingredient Slots**: Add up to 50 ingredients (upgradeable)
- **Effect Preview**: Shows what the mine will contain
- **Experience Cost**: Higher-level mines cost more XP
- **Hints**: Shows undiscovered ingredients
- **Donate Button**: Spend 20 XP to upgrade the Mine Crafter

### 4. Entering the Mine

**Method 1: Through the Portal**
1. A glowing portal beam spawns above the Mine Crafter
2. Walk into the beam
3. You'll see "Entering Mine in: X seconds"
4. Stand still to teleport

**Method 2: Using the Mine Item**
1. Hold the Mine Item
2. Right-click on a Mine Crafter
3. Portal spawns automatically

**Block ID**: `minecraft:mine_travelling_block` (portal)

### 5. Inside the Mine

**Objectives**:
Mines have different objectives based on ingredients:
- **Standard Mines**: Find and reach the exit portal
- **Boss Fight Mines**: Defeat the boss to spawn exit
- **Event Mines**: Complete the event (raid, waves, etc.)
- **Cave Mines**: Exit spawns in caves
- **Surface Mines**: Exit spawns on surface

**Finding the Exit**:
- Use **Eye of Exit** (`exit_eye`) - Throw it like an Eye of Ender
- Look for glowing portal beams
- Follow compass (if enabled)
- Explore caves (for cave exit mines)

**Tips**:
- Bring food, weapons, and tools
- Some mines have special rules (One HP, Soul Link, etc.)
- Mobs may be more dangerous
- Terrain can be unusual (floating islands, void world, etc.)

### 6. Completing the Mine

**Success**:
1. Reach the exit portal
2. Walk into it
3. Message: "MINE COMPLETED SUCCESSFULLY"
4. Teleported back to overworld
5. Rewards drop from Mine Crafter

**Failure**:
1. Die in the mine
2. Message: "MINE FAILED"
3. Teleported back
4. No rewards

**Rewards**:
- **Experience Points** (varies by difficulty)
- **Shimmering Keys** (for secret areas)
- **New Mine Ingredients** (unlocked effects)
- **Trophies** (for special achievements)

### 7. Revisiting Completed Mines

You can revisit any completed mine using the **Mine Revisitor** block.

**Steps**:
1. Place a **Mine Revisitor** block (`minecraft:mine_revisit`)
2. Hold your completed **Mine Item**
3. Right-click the Mine Revisitor
4. Portal spawns to that exact mine dimension
5. You can explore but won't get rewards again

**Block ID**: `minecraft:mine_revisit`
**Item ID**: `minecraft:mine_revisit`

---

## Advanced Features

### Upgrading the Mine Crafter

The Mine Crafter can be upgraded to accept more ingredients:

1. Open the Mine Crafter GUI
2. Click the "DONATE" button
3. Spend 20 experience levels
4. Increases max ingredient slots
5. Unlocks harder mines

**Upgrade Levels**:
- Level 0-4: 3 slots, costs 100-300 XP
- Level 5-9: 4 slots, costs 400-800 XP
- Level 10-14: 5 slots, costs 1000-1600 XP
- Level 15+: 6+ slots, costs 2000+ XP

### Using the Eye of Exit

The **Eye of Exit** helps you locate mine exits:

1. Craft or find an Eye of Exit
2. Inside a mine, right-click to throw it
3. It flies toward the nearest exit structure
4. Follow its direction
5. Has a chance to break (like Eye of Ender)

**Item ID**: `minecraft:exit_eye`
**Crafting**: (Check recipe book)

### Shimmering Keys and Secret Doors

**Shimmering Keys** unlock secret hub areas:

1. Earn keys by completing mines
2. Find **Shimmering Doors** in the hub world
3. Right-click door with key
4. Key is consumed
5. Door opens to secret room
6. Contains special rewards/challenges

**Key ID**: `minecraft:shimmering_key`
**Door ID**: `minecraft:shimmering_door`

### Special Mine Types

**Boss Fight Mines**:
- Ender Dragon Boss
- Wither Boss
- Warden Boss
- Angry Ghast Boss
- The Enderman Boss
- Small but Deadly Boss

**Event Mines**:
- Raid (defend village)
- Wave Defense
- Timed Challenges

**World Modifiers**:
- Amplified World (extreme terrain)
- Floating Islands
- Cave World
- Void World
- Grid World
- Water World
- Ultrawarm (nether-like)

**Special Rules**:
- One HP (1 heart only)
- Soul Link (shared damage)
- Universal Anger (all mobs hostile)
- Eternal Night/Rain/Lightning
- No Drops
- Explosive Traps

---

## Item and Block Reference

### Blocks

| Block | ID | Purpose |
|-------|----|----|
| Mine Crafter | `minecraft:mine_crafter` | Craft mines |
| Mine Portal | `minecraft:mine_travelling_block` | Enter/exit mines |
| Mine Revisitor | `minecraft:mine_revisit` | Revisit completed mines |
| Shimmering Door | `minecraft:shimmering_door` | Secret area doors |
| Trophy Block | `minecraft:trophy` | Display achievements |
| Mob Trophy | `minecraft:mob_trophy` | Display captured mobs |

### Items

| Item | ID | Purpose |
|------|----|----|
| Mine Item | `minecraft:mine` | Generated mine token |
| Mine Ingredient | `minecraft:mine_ingredient` | Mine modifiers |
| Eye of Exit | `minecraft:exit_eye` | Locate mine exits |
| Shimmering Key | `minecraft:shimmering_key` | Unlock secret doors |
| Ender Pearl Launcher | `minecraft:ender_pearl_launcher` | Launch ender pearls |
| Fireball Wand | `minecraft:fireball_wand` | Shoot fireballs |
| Wind Charge Wand | `minecraft:wind_charge_wand` | Shoot wind charges |
| Wings | `minecraft:wings` | Flight item |

---

## Tips and Strategies

### For Beginners
1. Start with simple ingredients (Plains, Forest, Surface Exits)
2. Bring basic gear (iron armor, food, torches)
3. Use Eye of Exit to find the exit quickly
4. Don't combine too many dangerous ingredients at once

### For Advanced Players
1. Combine boss fights with beneficial biomes
2. Use event mines for maximum XP
3. Upgrade Mine Crafter to level 15+ for best rewards
4. Collect all ingredients for achievements
5. Try challenge combinations (One HP + Boss Fight)

### Ingredient Synergies
- **Easy XP**: Plains + Forest + Surface Exits
- **Boss Rush**: Multiple boss ingredients
- **Exploration**: Floating Islands + Rare Surface Exits
- **Challenge**: One HP + Soul Link + Boss Fight
- **Resource Gathering**: Specific biomes + No Drops (for XP only)

---

## Troubleshooting

**Portal won't spawn**:
- Make sure there's space above the Mine Crafter (3+ blocks)
- Check that you have a valid Mine Item
- Try breaking and replacing the Mine Crafter

**Can't find exit**:
- Craft an Eye of Exit
- Check if it's a cave exit mine (go underground)
- Look for glowing beams in the sky
- Some exits are rare (intentionally hard to find)

**Mine Crafter won't accept ingredients**:
- Check if you've reached the slot limit
- Upgrade the Mine Crafter with XP
- Make sure items are actual Mine Ingredients

**Lost in mine dimension**:
- Use Eye of Exit to navigate
- Build a tall pillar to see farther
- If you die, you'll respawn in overworld
- Use /kill command as last resort

---

## Data Components

Mines use custom data components to track state:

- `MINE_COMPLETED` - Boolean, whether mine was won
- `DIMENSION_ID` - Registry key of the mine dimension
- `WORLD_MODIFIERS` - List of effects in the mine
- `MINE_ACTIVE` - Boolean, whether mine is currently active
- `EXCHANGE_VALUE` - Experience value for conversion

These are automatically managed by the mod.

---

## Creative Mode

In creative mode, you can:
- Get Mine Crafter from Functional tab
- Get all Mine Ingredients from creative menu
- Spawn Mine Portals directly
- Use Mine Revisitor without completed mines
- Get infinite Shimmering Keys

---

## Multiplayer

Mines work in multiplayer:
- Multiple players can enter the same mine
- Shared objectives and rewards
- Soul Link affects all players in mine
- First player to exit triggers completion
- Rewards distributed to all participants

---

## Commands

Useful commands for mine management:

```
/warp <dimension> - Teleport to dimension
/debugdim - Debug dimension information
/transform - Transform world settings
```

(Admin/cheat commands only)

---

## Conclusion

The Mine system adds endless replayability with procedurally generated challenges. Experiment with different ingredient combinations, upgrade your Mine Crafter, and collect all the ingredients to become a master miner!

**Good luck, and happy mining!** ⛏️
