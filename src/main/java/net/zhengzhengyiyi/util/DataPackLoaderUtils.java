package net.zhengzhengyiyi.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.slf4j.Logger;
// TODO - UNFINISHED
public class DataPackLoaderUtils {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static DataConfiguration loadNextDataConfiguration(ResourcePackManager manager, DataConfiguration currentConfig, boolean resetFeatures, boolean forceVanilla) {
      DataPackSettings currentDataPacks = currentConfig.dataPacks();
      FeatureSet baseFeatures = resetFeatures ? FeatureSet.empty() : currentConfig.enabledFeatures();
      FeatureSet availableFeatures = resetFeatures ? FeatureFlags.FEATURE_MANAGER.getFeatureSet() : currentConfig.enabledFeatures();
      
      manager.scanPacks();
      if (forceVanilla) {
         return createConfigFromProfiles(manager, List.of("vanilla"), baseFeatures, false);
      } else {
         Set<String> enabledIds = Sets.newLinkedHashSet();

         for (String id : currentDataPacks.getEnabled()) {
            if (manager.hasProfile(id)) {
               enabledIds.add(id);
            } else {
               LOGGER.warn("Missing data pack {}", id);
            }
         }

         for (ResourcePackProfile profile : manager.getProfiles()) {
            String profileId = profile.getId();
            if (!currentDataPacks.getDisabled().contains(profileId)) {
               FeatureSet requestedFeatures = profile.getRequestedFeatures();
               boolean isAlreadyEnabled = enabledIds.contains(profileId);
               
               if (!isAlreadyEnabled && profile.getSource().canBeEnabledLater()) {
                  if (requestedFeatures.isSubsetOf(availableFeatures)) {
                     LOGGER.info("Found new data pack {}, loading it automatically", profileId);
                     enabledIds.add(profileId);
                  } else {
                     LOGGER.info(
                        "Found new data pack {}, but can't load it due to missing features {}",
                        profileId,
                        FeatureFlags.printMissingFlags(availableFeatures, requestedFeatures)
                     );
                  }
               }

               if (isAlreadyEnabled && !requestedFeatures.isSubsetOf(availableFeatures)) {
                  LOGGER.warn(
                     "Pack {} requires features {} that are not enabled for this world, disabling pack.",
                     profileId,
                     FeatureFlags.printMissingFlags(availableFeatures, requestedFeatures)
                  );
                  enabledIds.remove(profileId);
               }
            }
         }

         if (enabledIds.isEmpty()) {
            LOGGER.info("No datapacks selected, forcing vanilla");
            enabledIds.add("vanilla");
         }

         return createConfigFromProfiles(manager, enabledIds, baseFeatures, true);
      }
   }

   private static DataConfiguration createConfigFromProfiles(ResourcePackManager manager, Collection<String> profiles, FeatureSet baseFeatures, boolean includeDisabled) {
      manager.setEnabledProfiles(profiles);
      forceEnableFeaturePacks(manager, baseFeatures);
      DataPackSettings settings = createDataPackSettings(manager, includeDisabled);
      FeatureSet combinedFeatures = manager.getRequestedFeatures().combine(baseFeatures);
      return new DataConfiguration(settings, combinedFeatures);
   }

   private static void forceEnableFeaturePacks(ResourcePackManager manager, FeatureSet targetFeatures) {
      FeatureSet currentFeatures = manager.getRequestedFeatures();
      FeatureSet missingFeatures = targetFeatures.subtract(currentFeatures);
      
      if (!missingFeatures.isEmpty()) {
         Set<String> enabledIds = new ObjectArraySet<String>(manager.getEnabledIds());

         for (ResourcePackProfile profile : manager.getProfiles()) {
            if (missingFeatures.isEmpty()) {
               break;
            }

            if (profile.getSource() == ResourcePackSource.FEATURE) {
               String profileId = profile.getId();
               FeatureSet profileFeatures = profile.getRequestedFeatures();
               if (!profileFeatures.isEmpty() && profileFeatures.intersects(missingFeatures) && profileFeatures.isSubsetOf(targetFeatures)) {
                  if (!enabledIds.add(profileId)) {
                     throw new IllegalStateException("Tried to force '" + profileId + "', but it was already enabled");
                  }

                  LOGGER.info("Found feature pack ('{}') for requested feature, forcing to enabled", profileId);
                  missingFeatures = missingFeatures.subtract(profileFeatures);
               }
            }
         }

         manager.setEnabledProfiles(enabledIds);
      }
   }

   public static DataPackSettings createDataPackSettings(ResourcePackManager manager, boolean includeDisabled) {
      Collection<String> enabled = manager.getEnabledIds();
      List<String> enabledList = ImmutableList.copyOf(enabled);
      List<String> disabledList = includeDisabled ? manager.getIds().stream().filter(id -> !enabled.contains(id)).toList() : List.of();
      return new DataPackSettings(enabledList, disabledList);
   }
}
