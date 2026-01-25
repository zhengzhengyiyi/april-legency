package net.zhengzhengyiyi.block;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BookBox extends HorizontalFacingBlock {
   private static final char[] ALPHABET = new char[]{
      ' ', ',', '.', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
   };

   public BookBox(AbstractBlock.Settings settings) {
      super(settings);
   }
   
   @Override
   public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
       Direction direction = state.get(FACING);
       int y = pos.getY();
       int sideOffset;
       int quadrant;

       switch (direction) {
           case NORTH -> {
               sideOffset = 15 - pos.getX() & 15;
               quadrant = 0;
           }
           case SOUTH -> {
               sideOffset = pos.getX() & 15;
               quadrant = 2;
           }
           case EAST -> {
               sideOffset = 15 - pos.getZ() & 15;
               quadrant = 1;
           }
           default -> {
               sideOffset = pos.getZ() & 15;
               quadrant = 3;
           }
       }

       if (sideOffset > 0 && sideOffset < 15) {
           ChunkPos chunkPos = new ChunkPos(pos);
           String title = chunkPos.x + "/" + chunkPos.z + "/" + quadrant + "/" + sideOffset + "/" + y;

           java.util.Random randX = new java.util.Random(chunkPos.x);
           java.util.Random randZ = new java.util.Random(chunkPos.z);
           java.util.Random randCoord = new java.util.Random((sideOffset << 8) + (y << 4) + quadrant);

           List<RawFilteredPair<Text>> pages = new ArrayList<>();

           for (int p = 0; p < 16; p++) {
               StringBuilder content = new StringBuilder();
               for (int c = 0; c < 128; c++) {
                   int n = randX.nextInt() + randZ.nextInt() - randCoord.nextInt();
                   content.append(ALPHABET[Math.floorMod(n, ALPHABET.length)]);
               }
               pages.add(RawFilteredPair.of(Text.literal(content.toString())));
           }

           WrittenBookContentComponent contentComponent = new WrittenBookContentComponent(
               RawFilteredPair.of(title),
               Formatting.OBFUSCATED + "Universe itself",
               0,
               pages,
               true
           );

           ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
           book.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, contentComponent);

           dropStack(world, pos.offset(hit.getSide()), book);
           return ActionResult.SUCCESS;
       } else {
           return ActionResult.FAIL;
       }
   }

//   @Override
//   public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
//      Direction direction = state.get(FACING);
//      int y = pos.getY();
//      int sideOffset;
//      int quadrant;
//
//      switch (direction) {
//         case NORTH -> {
//            sideOffset = 15 - pos.getX() & 15;
//            quadrant = 0;
//         }
//         case SOUTH -> {
//            sideOffset = pos.getX() & 15;
//            quadrant = 2;
//         }
//         case EAST -> {
//            sideOffset = 15 - pos.getZ() & 15;
//            quadrant = 1;
//         }
//         case WEST -> {
//            sideOffset = pos.getZ() & 15;
//            quadrant = 3;
//         }
//         default -> {
//            sideOffset = pos.getZ() & 15;
//            quadrant = 3;
//         }
//      }
//
//      if (sideOffset > 0 && sideOffset < 15) {
//         ChunkPos chunkPos = new ChunkPos(pos);
//         String title = chunkPos.x + "/" + chunkPos.z + "/" + quadrant + "/" + sideOffset + "/" + y;
//         
//         Random randX = new Random(chunkPos.x);
//         Random randZ = new Random(chunkPos.z);
//         Random randCoord = new Random((sideOffset << 8) + (y << 4) + quadrant);
//         
//         ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
//         NbtCompound tag = book.getOrCreateNbt();
//         NbtList pages = new NbtList();
//
//         for (int p = 0; p < 16; p++) {
//            StringBuilder content = new StringBuilder();
//            for (int c = 0; c < 128; c++) {
//               int n = randX.nextInt() + randZ.nextInt() + -randCoord.nextInt();
//               content.append(ALPHABET[Math.floorMod(n, ALPHABET.length)]);
//            }
//            pages.add(NbtString.of(Text.Serializer.toJson(Text.literal(content.toString()), world.getRegistryManager())));
//         }
//
//         tag.put("pages", pages);
//         tag.putString("author", Formatting.OBFUSCATED + "Universe itself");
//         tag.putString("title", title);
//         
//         dropStack(world, pos.offset(hit.getSide()), book);
//         return ActionResult.SUCCESS;
//      } else {
//         return ActionResult.FAIL;
//      }
//   }

   @Override
   protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   @Override
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
   }

   @SuppressWarnings("unchecked")
   @Override
   protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
	return (MapCodec<? extends HorizontalFacingBlock>)(Object)Block.CODEC;
   }
}
