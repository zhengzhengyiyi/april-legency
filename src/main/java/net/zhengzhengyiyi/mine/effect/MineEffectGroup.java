package net.zhengzhengyiyi.mine.effect;

import java.util.ArrayList;
import java.util.List;

import net.zhengzhengyiyi.mine.MineEffect;

public final class MineEffectGroup {
   private final List<MineEffect> field_59156 = new ArrayList<>();
   private final boolean field_59157;

   public MineEffectGroup(boolean bl) {
      this.field_59157 = bl;
   }

   public List<MineEffect> method_69965() {
      return this.field_59156;
   }

   public boolean method_69967() {
      return this.field_59157;
   }

   public void add(MineEffect arg) {
      this.field_59156.add(arg);
   }
}
