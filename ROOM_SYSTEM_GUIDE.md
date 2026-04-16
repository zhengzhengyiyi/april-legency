# Room System Guide

## Overview

The room system allows you to create custom hub rooms that can be placed using Shimmering Keys. Rooms are saved as **physical NBT structure files** in your world save directory.

## How It Works

### 1. Create a Room Structure

Use the `/room start` command to scaffold a bedrock box with a structure block:

```
/room start <id> <xSize> <zSize> <ySize>
```

**Example:**
```
/room start my_room 10 10 5
```

This will:
- Create a bedrock box 6 blocks in front of you
- Place a structure block below the box (pre-configured)
- The structure block is set to SAVE mode with the correct template name

### 2. Build Your Room

- Build your room inside the bedrock box
- The bedrock walls are just scaffolding - they won't be saved
- Add blocks, decorations, ShimmeringDoors, etc.

### 3. Save the Room

When you're done building, save the structure:

```
/room save <id>
```

**Example:**
```
/room save my_room
```

This will:
- Find the structure block you created with `/room start`
- Capture all blocks from the world into a structure template
- Save the template as an **NBT file** to disk at:
  - `<world_save>/generated/minecraft/structures/hub/room/<id>.nbt`
- Give you a Shimmering Key for the room

### 4. Use the Room

- Right-click a closed ShimmeringDoor with the Shimmering Key
- The room will be placed behind the door
- The door will open automatically

## Technical Details

### File Locations

Rooms are saved as NBT files in your world save directory:
```
<world_save>/generated/minecraft/structures/hub/room/<room_id>.nbt
```

These are **physical structure files** that contain:
- Block data (types, states, properties)
- Block entity data (chests, signs, etc.)
- Entity data (if not ignored)

### Structure Block Configuration

The structure block is automatically configured with:
- **Mode:** SAVE
- **Template Name:** `minecraft:hub/room/<id>`
- **Size:** The dimensions you specified
- **Ignore Entities:** false (entities are saved)

### How Saving Works

1. The `/room save` command searches for the structure block within 50 blocks
2. It calls `structureBlock.saveStructure(true)` which:
   - Creates a `StructureTemplate` object
   - Calls `saveFromWorld()` to capture blocks from the world
   - Calls `saveTemplate()` to write the NBT file to disk
3. The NBT file is a complete snapshot of your room structure

### Room Placement

When you use a Shimmering Key:
1. The `ShimmeringKeyItem` reads the room NBT file
2. It finds valid rotations that align with the door
3. It places the structure using `StructureTemplate.place()`
4. All blocks are placed exactly as they were saved

## Commands Reference

### `/room start <id> <xSize> <zSize> <ySize>`
Creates a bedrock scaffolding box and structure block for building a room.

**Parameters:**
- `id` - Short identifier (e.g., `my_room`, `barrels`, `treasure`)
- `xSize` - Width (X axis)
- `zSize` - Depth (Z axis)  
- `ySize` - Height (Y axis)

### `/room save <id>`
Saves the room structure to an NBT file and gives you a key.

**Parameters:**
- `id` - The same identifier you used in `/room start`

### `/room key <id>`
Gives you a Shimmering Key for an existing room (without saving).

**Parameters:**
- `id` - Room identifier

## Example Workflow

```bash
# 1. Start creating a 10x10x5 room called "treasure"
/room start treasure 10 10 5

# 2. Build your room inside the bedrock box
# (add chests, decorations, doors, etc.)

# 3. Save the room when done
/room save treasure

# 4. You now have a Shimmering Key for "treasure"
# Use it on any closed ShimmeringDoor to place the room!
```

## Notes

- Rooms are saved in the **world save directory**, not in the mod's resources
- Each world has its own set of saved rooms
- The bedrock scaffolding is NOT saved - only the blocks inside
- You can create as many rooms as you want
- Rooms can contain ShimmeringDoors to connect to other rooms
- The structure block must remain in place until you run `/room save`

## Troubleshooting

**"Could not find structure block"**
- Make sure you're within 50 blocks of the structure block
- The structure block must have the correct template name set

**"Failed to save structure from structure block"**
- Check that the structure block is in SAVE mode
- Verify the size is set correctly
- Make sure there are blocks to save

**Room doesn't place correctly**
- Check that there's enough space behind the door
- Try different door orientations
- Make sure the room NBT file was saved successfully
