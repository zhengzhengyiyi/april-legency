package net.zhengzhengyiyi.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.zhengzhengyiyi.component.ModDataComponentTypes;
import net.zhengzhengyiyi.component.MobTrophyComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.zhengzhengyiyi.component.MobTrophyComponent;
import org.jetbrains.annotations.Nullable;

public class MobTrophyBlock extends BlockWithEntity {
   private static final Map<Direction, VoxelShape> SHAPES = VoxelShapes.createHorizontalFacingShapeMap(Block.createColumnShape(12.0, 0.0, 16.0));
   public static final MapCodec<MobTrophyBlock> CODEC = createCodec(MobTrophyBlock::new);
   public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
   public static final EnumProperty<Grade> GRADE = EnumProperty.of("grade", Grade.class);

   @Override
   public MapCodec<MobTrophyBlock> getCodec() {
      return CODEC;
   }

   public MobTrophyBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(GRADE, Grade.GRASS));
   }

   @Override
   protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      super.appendProperties(builder);
      builder.add(FACING, GRADE);
   }

   @Override
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
   }

   @Nullable
   @Override
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new MobTrophyBlockEntity(pos, state);
   }

   @Override
   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      ItemStack itemStack = super.getPickStack(world, pos, state, includeData);
      itemStack.set(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(GRADE, state.get(GRADE)));
      return itemStack;
   }

   @Override
   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES.get(state.get(FACING));
   }

   @Override
   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return state.with(FACING, rotation.rotate(state.get(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.with(FACING, mirror.apply(state.get(FACING)));
   }

   @Override
   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (world instanceof ServerWorld serverWorld) {
         serverWorld.getBlockEntity(pos, ModBlocks.MOB_TROPHY_BLOCK_ENTITY)
            .ifPresent(blockEntity -> {
               MobTrophyComponent trophy = blockEntity.getMobTrophy();
               if (trophy != null) {
                  trophy.type().getKey().ifPresent(registryKey -> {
                     String prefix = "entity." + registryKey.getValue().getPath() + ".";
                     serverWorld.getRegistryManager()
                        .getOrThrow(RegistryKeys.SOUND_EVENT)
                        .streamEntries()
                        .filter(ref -> ref.registryKey().getValue().getPath().startsWith(prefix))
                        .findFirst()
                        .ifPresent(ref -> world.playSound(null, pos, ref.value(), SoundCategory.BLOCKS));
                  });
               }
            });
      }
      return ActionResult.PASS;
   }

   public static ItemStack createTrophyStack(RegistryEntry<EntityType<?>> entityType, Random random) {
      Grade grade = Grade.WEIGHTED_POOL.getOrEmpty(random).orElseThrow();
      boolean shiny = random.nextFloat() < 0.01f;
      ItemStack itemStack = new ItemStack(net.zhengzhengyiyi.item.ModItems.MOB_TROPHY);
      itemStack.set(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(GRADE, grade));
      itemStack.set(ModDataComponentTypes.TYPE_MOB_TROPHY, new MobTrophyComponent(entityType, shiny));
      return itemStack;
   }

   public enum Grade implements StringIdentifiable {
      GRASS("grass", 0x50C040),
      STONE("stone", 0x477452),
      GOLD("gold", 0xDE9B2D),
      DIAMOND("diamond", 0x6EBF92),
      NETHERITE("netherite", 0x626059);

      public static final Pool<Grade> WEIGHTED_POOL = Pool.of(
         new Weighted<>(GRASS, 100), new Weighted<>(STONE, 50),
         new Weighted<>(GOLD, 25), new Weighted<>(DIAMOND, 5), new Weighted<>(NETHERITE, 1)
      );
      private final String id;
      private final int color;

      Grade(final String id, final int color) {
         this.id = id;
         this.color = color;
      }

      @Override
      public String asString() { return this.id; }

      public int getColor() { return this.color; }

      public String getTranslationKey() { return "item.minecraft.mob_trophy.grade." + this.id; }
   }
}
