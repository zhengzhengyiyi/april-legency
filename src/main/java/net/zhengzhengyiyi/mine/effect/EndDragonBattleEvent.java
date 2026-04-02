package net.zhengzhengyiyi.mine.effect;

//public class EndDragonBattleEvent implements WorldEvent {
public class EndDragonBattleEvent {
//   public static final MapCodec<EndDragonBattleEvent> CODEC = RecordCodecBuilder.mapCodec(
//      instance -> instance.group(
//            Codec.BOOL.fieldOf("completed").forGetter(event -> event.completed), 
//            BlockPos.CODEC.fieldOf("pos").forGetter(event -> event.portalPos)
//         )
//         .apply(instance, EndDragonBattleEvent::new)
//   );
//   private boolean completed;
//   private BlockPos portalPos;
//
//   public EndDragonBattleEvent(boolean completed, BlockPos portalPos) {
//      this.completed = completed;
//      this.portalPos = portalPos;
//   }
//
//   public EndDragonBattleEvent() {
//      this(false, BlockPos.ORIGIN);
//   }
//
//   public void complete() {
//      this.completed = true;
//   }
//
//   @SuppressWarnings("deprecation")
//@Override
//   public void tick(ServerWorld world) {
//      if (!this.completed && world.getEnderDragonFight() == null) {
//         world.setEnderDragonFight(new EnderDragonFight(
//            world, world.getSeed(), new EnderDragonFight.Data(false, true, true, false, Optional.empty(), Optional.empty(), Optional.empty())
//         ));
//
//         world.getEnderDragonFight().respawnDragon();
//      } else if (!this.completed && world.getEnderDragonFight() != null && world.getEnderDragonFight().exitPortalLocation != null) {
//         this.portalPos = world.getEnderDragonFight().exitPortalLocation;
//      }
//   }
//
//   @SuppressWarnings("deprecation")
//   @Override
//   public void finish(ServerWorld world, boolean force) {
//      UUID dragonUuid = world.getEnderDragonFight() != null ? world.getEnderDragonFight().getDragonUuid() : null;
//      Entity dragon = dragonUuid != null ? world.getEntity(dragonUuid) : null;
//      if (dragon != null) {
//         dragon.kill(world);
//      }
//
//      world.setEnderDragonFight(null);
//   }
//
//   @Override
//   public BlockPos getPos() {
//      return this.portalPos;
//   }
//
//   @Override
//   public WorldEvent.Status getStatus() {
//      return this.completed ? WorldEvent.Status.WON : WorldEvent.Status.ACTIVE;
//   }
//
//   @Override
//   public MapCodec<EndDragonBattleEvent> getCodec() {
//      return CODEC;
//   }
}
