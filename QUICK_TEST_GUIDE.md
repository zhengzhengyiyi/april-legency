# Quick Test Guide - Portal Debug

## Build & Run
```bash
./gradlew build
./gradlew runClient
```

## Test Steps
1. Place Mine Crafter
2. Add Mine Ingredients
3. Take Mine Item from output ← **This creates the dimension**
4. Click the portal above Mine Crafter

## What to Check in Console

### ✅ SUCCESS - Look for:
```
[class_10967] Fantasy world created successfully
[MineEffectGenerator] Verifying world exists: true
[MiningPortal] Target world exists: true
[MiningPortal] Teleporting to: Vec3d(...)
```
**Result**: You should be teleported!

### ❌ FAILURE - Look for:
```
[MineEffectGenerator] Verifying world exists: false
```
**Problem**: Fantasy dimension not created

```
[MiningPortal] Target world exists: false
```
**Problem**: Dimension not accessible when clicking

```
[MiningPortal] Dimension key: minecraft:overworld
```
**Problem**: Wrong dimension key (should be zhengzhengyiyi:level0)

## Copy Console Output

When testing, copy the console output and share it. Look for lines starting with:
- `[class_10967]`
- `[MineEffectGenerator]`
- `[MineCrafterBlockEntity]`
- `[MiningPortal]`
- `[TravellingBlockEntity]`

## Common Issues

| Console Output | Problem | Solution |
|----------------|---------|----------|
| No logs at all | Portal not created | Check block above Mine Crafter is empty |
| "world exists: false" | Fantasy failed | Check Fantasy mod is installed |
| "dimension key: overworld" | Wrong key stored | NBT save/load bug |
| No "Teleporting to:" | Code not reached | Check earlier errors |

## Full Documentation

- `PORTAL_FIX.md` - Complete debug guide
- `CHANGES_SUMMARY.md` - What was changed
- `PORTAL_DEBUG_GUIDE.md` - Original troubleshooting

## The Key Question

**Does the console show "Verifying world exists: true"?**
- YES → Dimension created, problem is elsewhere
- NO → Fantasy dimension creation failed
