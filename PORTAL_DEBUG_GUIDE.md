# Portal Not Working - Debug Guide

## Issue
Clicking the mine portal (travelling block) does nothing - no teleportation, no error.

## Possible Causes

### 1. Dimension Not Created
The mine dimension might not be created yet when you click the portal.

**Check**:
- Did you take the Mine Item from the output slot?
- Did the portal spawn above the Mine Crafter?
- Look in the server console for dimension creation messages

### 2. Dimension Key Not Stored
The portal block entity might not have the dimension key stored.

**Check**:
- The dimension key should be set when the portal is created
- Check if `TravellingBlockEntity` has the correct dimension key

### 3. World Not Loaded
The target dimension world might not be loaded on the server.

**Check**:
- Fantasy mod should create the dimension
- Server needs to load the dimension before teleportation

## How to Debug

### Step 1: Check Console Output
I've added debug logging to `MiningPortalBlock.java`. When you click the portal, you should see:

```
[MiningPortal] Player clicked portal at BlockPos{x=..., y=..., z=...}
[MiningPortal] Dimension key: minecraft:...
[MiningPortal] Is mine world: false
[MiningPortal] Target dimension: minecraft:...
[MiningPortal] Target world exists: true/false
[MiningPortal] Teleporting to: Vec3d(...)
```

### Step 2: Rebuild and Test
```bash
./gradlew build
./gradlew runClient
```

### Step 3: Test the Portal
1. Place Mine Crafter
2. Add Mine Ingredients
3. Craft the mine (click output slot)
4. Take the Mine Item
5. Portal should spawn above
6. Right-click the portal
7. Check console for debug messages

## Expected Behavior

### When Crafting Works:
1. You add ingredients to Mine Crafter
2. Mine Item appears in output slot
3. You click to take it
4. Portal spawns above the Mine Crafter
5. Dimension is created by Fantasy mod
6. Dimension key is stored in portal block entity

### When Clicking Portal:
1. Portal checks if you're in a mine world
2. If not, it gets the target dimension key
3. It looks up the dimension world on the server
4. If found, it teleports you there
5. If not found, shows error message

## Common Issues

### Issue: "Target world is null"
**Cause**: Dimension wasn't created or isn't loaded
**Fix**: 
- Make sure you took the Mine Item from output
- Check that Fantasy mod is working
- Try crafting a new mine

### Issue: Nothing happens, no console output
**Cause**: Portal block entity is missing or not set up
**Fix**:
- Break and replace the portal
- Try crafting a new mine
- Check that the block is actually a `mine_travelling_block`

### Issue: "Is mine world: true" when in overworld
**Cause**: Mixin issue with world detection
**Fix**:
- This is a bug in `ServerWorldMixin`
- The overworld shouldn't be detected as a mine world

## Manual Testing Steps

### Test 1: Check Block Entity
```java
// In game, use F3 to check:
// - Block type should be: minecraft:mine_travelling_block
// - Block entity should exist
```

### Test 2: Check Dimension Creation
Look for these in console when crafting:
```
Creating mine dimension: minecraft:mine_...
Fantasy dimension created: ...
```

### Test 3: Check Portal Creation
Look for these when portal spawns:
```
Portal created at: ...
Dimension key set: ...
```

## Workaround

If the portal doesn't work, you can try:

1. **Use /warp command** (if available):
   ```
   /warp minecraft:mine_1
   ```

2. **Craft a new mine**:
   - Sometimes recreating fixes issues
   - Use different ingredients

3. **Check Fantasy mod**:
   - Make sure Fantasy is installed
   - Check Fantasy is creating dimensions

## Code Changes Made

I added debug logging to `MiningPortalBlock.java`:
- Logs when portal is clicked
- Logs dimension key
- Logs if target world exists
- Shows error message if world is null

## Next Steps

1. Rebuild the mod with debug logging
2. Test clicking the portal
3. Check console output
4. Report what you see in the console
5. We can fix based on the debug output

## Files to Check

- `MiningPortalBlock.java` - Portal click handler
- `TravellingBlockEntity.java` - Stores dimension key
- `MineCrafterBlockEntity.java` - Creates portal
- `MineEffectGenerator.java` - Crafts mine and creates dimension
- `ServerWorldMixin.java` - World detection

## Expected Console Output

**Good (Working)**:
```
[MiningPortal] Player clicked portal at BlockPos{x=100, y=64, z=200}
[MiningPortal] Dimension key: minecraft:mine_1
[MiningPortal] Is mine world: false
[MiningPortal] Target dimension: minecraft:mine_1
[MiningPortal] Target world exists: true
[MiningPortal] Teleporting to: Vec3d(0.5, 64.0, 0.5)
```

**Bad (Not Working)**:
```
[MiningPortal] Player clicked portal at BlockPos{x=100, y=64, z=200}
[MiningPortal] Dimension key: minecraft:overworld
[MiningPortal] Is mine world: false
[MiningPortal] Target dimension: minecraft:overworld
[MiningPortal] Target world exists: true
[MiningPortal] Teleporting to: Vec3d(0.5, 64.0, 0.5)
```
(This would teleport you to overworld spawn - wrong dimension key!)

**Bad (Dimension Not Created)**:
```
[MiningPortal] Player clicked portal at BlockPos{x=100, y=64, z=200}
[MiningPortal] Dimension key: minecraft:mine_1
[MiningPortal] Is mine world: false
[MiningPortal] Target dimension: minecraft:mine_1
[MiningPortal] Target world exists: false
ERROR: Target world is null!
```

## Summary

The portal needs:
1. ✅ Block entity with dimension key
2. ✅ Dimension created by Fantasy
3. ✅ Dimension loaded on server
4. ✅ Correct world detection

Check the console output to see which step is failing!
