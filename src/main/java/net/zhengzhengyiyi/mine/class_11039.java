package net.zhengzhengyiyi.mine;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class class_11039 {
   private final List<class_11039.class_11040> field_58793;
   private final double field_58794;

   public class_11039(List<Vec2f> list) {
      List<Vec2f> list2 = new LinkedList<>(list);
      list2.addAll(list.reversed());
      this.field_58793 = new LinkedList<>();
      double d = 0.0;

      for (int i = 0; i < list2.size(); i++) {
         Vec2f vec2f = list2.get(i);
         Vec2f vec2f2 = i + 1 < list2.size() ? list2.get(i + 1) : list2.get(i);
         double e = MathHelper.sqrt(vec2f.distanceSquared(vec2f2));
         this.field_58793.add(new class_11039.class_11040(vec2f, e));
         d += e;
      }

      this.field_58794 = d;
   }

   public Vec2f method_69513(float f) {
      double d = this.field_58794 * (f % 1.0);

      for (int i = 0; i < this.field_58793.size(); i++) {
         class_11039.class_11040 lv = this.field_58793.get(i);
         d -= lv.distance;
         if (d <= 0.0) {
            class_11039.class_11040 lv2 = i + 1 < this.field_58793.size() ? this.field_58793.get(i + 1) : this.field_58793.get(i - 1);
            Vec2f vec2f = lv2.pos.add(lv.pos.negate()).normalize();
            float g = (float)(lv.distance + d);
            return lv.pos.add(vec2f.multiply(g));
         }
      }

      return this.field_58793.getFirst().pos;
   }

   record class_11040(Vec2f pos, double distance) {
   }
}
