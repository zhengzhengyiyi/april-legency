# Mine Crafter GUI Guide

## ✅ Fixed Issue
The player inventory was missing from the Mine Crafter GUI. This has been fixed!

---

## GUI Layout

```
┌─────────────────────────────────────────────────────────────┐
│                    MINE CRAFTER GUI                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  [Ingredient Slots in Circle Pattern]                      │
│                                                             │
│              ○  ○  ○                                        │
│           ○           ○                                     │
│        ○                 ○                                  │
│           ○           ○                                     │
│              ○  ○  ○                                        │
│                                                             │
│         [Output Slot] ← Crafted Mine appears here          │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│  DISCOVERED EFFECTS (Left Panel)                           │
│  ┌──────────────────────────────────┐                      │
│  │ [Effect] [Effect] [Effect] ...   │                      │
│  │ [Effect] [Effect] [Effect] ...   │                      │
│  │ [Effect] [Effect] [Effect] ...   │                      │
│  └──────────────────────────────────┘                      │
├─────────────────────────────────────────────────────────────┤
│  YOUR INVENTORY (Now Visible!)                             │
│  ┌──────────────────────────────────┐                      │
│  │ [Item] [Item] [Item] [Item] ...  │  ← Main Inventory    │
│  │ [Item] [Item] [Item] [Item] ...  │                      │
│  │ [Item] [Item] [Item] [Item] ...  │                      │
│  └──────────────────────────────────┘                      │
│  ┌──────────────────────────────────┐                      │
│  │ [1] [2] [3] [4] [5] [6] [7] [8] [9] │ ← Hotbar          │
│  └──────────────────────────────────┘                      │
└─────────────────────────────────────────────────────────────┘
```

---

## How to Use

### 1. Opening the GUI
- Right-click the Mine Crafter block
- Your inventory should now be visible at the bottom

### 2. Adding Ingredients
**Method 1: Click and Place**
1. Click a Mine Ingredient in your inventory
2. Click an empty ingredient slot (the circles)
3. The ingredient is placed

**Method 2: Shift-Click**
1. Shift-click a Mine Ingredient in your inventory
2. It automatically goes to an available ingredient slot

### 3. Viewing Effects
- Ingredients you add show their effects
- The left panel shows all discovered effects
- Hover over effects to see descriptions

### 4. Crafting the Mine
1. Add 1-50 ingredients (depending on upgrade level)
2. The output slot shows the Mine Item
3. Click the Mine Item to take it
4. Portal spawns above the Mine Crafter

### 5. Removing Ingredients
- Click an ingredient slot to remove it
- It goes back to your inventory
- The output updates automatically

---

## Slot Types

### Ingredient Slots (Circle Pattern)
- **Unlocked** (bright): Can place ingredients
- **Locked** (dim): Need to upgrade Mine Crafter
- **Filled** (has item): Contains an ingredient
- **Empty** (no item): Ready for an ingredient

### Output Slot (Center)
- Shows the crafted Mine Item
- Click to take it
- Cannot place items here

### Discovered Effects (Left Panel)
- Shows all effects you've unlocked
- Click to add to ingredient slots
- Grayed out = not yet discovered

### Your Inventory (Bottom)
- **3 rows**: Main inventory (27 slots)
- **1 row**: Hotbar (9 slots)
- All your items are visible here

---

## Tips

### Quick Ingredient Management
- **Shift-click** ingredients to quickly add them
- **Click** ingredient slots to remove them
- **Drag** to move items around

### Upgrading for More Slots
1. Click the "DONATE" button (if visible)
2. Spend 20 experience levels
3. More ingredient slots unlock
4. Can add more effects to your mine

### Viewing Effects
- Hover over ingredients to see what they do
- Check the left panel for all discovered effects
- Experiment with combinations!

---

## Troubleshooting

**Still can't see inventory?**
- Make sure you're using the latest build
- Try closing and reopening the GUI
- Restart the game if needed

**Can't add ingredients?**
- Check if slots are locked (need upgrade)
- Make sure you're using Mine Ingredients
- Try shift-clicking instead

**Output slot is empty?**
- Add at least one ingredient
- Make sure ingredients are valid
- Check that you have space above the Mine Crafter

---

## What Changed

### Before (Broken)
```
- No player inventory visible
- Only one empty slot shown
- Couldn't access your items
- Very confusing!
```

### After (Fixed)
```
✅ Full player inventory visible (36 slots)
✅ Can see all your items
✅ Can shift-click ingredients
✅ Proper GUI layout
✅ Works like other container GUIs
```

---

## Technical Details

The fix added:
- 27 main inventory slots (3 rows × 9 columns)
- 9 hotbar slots (1 row × 9 columns)
- Proper slot positioning
- Shift-click support for Mine Ingredients
- Standard container behavior

---

## Enjoy Crafting Mines!

Your inventory is now fully functional in the Mine Crafter GUI. You can:
- See all your items
- Easily add/remove ingredients
- Shift-click for quick management
- Craft mines efficiently

Happy mining! ⛏️
