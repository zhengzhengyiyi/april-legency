package net.zhengzhengyiyi.generator;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class DimensionHasher {
   private static String lastUsedText = "";
   
   public static int hash(String text) {
      lastUsedText = text;
      return Hashing.sha256()
            .hashString(text + ":why_so_salty#LazyCrypto", StandardCharsets.UTF_8)
            .asInt() & Integer.MAX_VALUE;
   }
   
   public static String getLastUsedText() {
      return lastUsedText;
   }
}
