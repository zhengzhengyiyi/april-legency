package net.zhengzhengyiyi.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class TickScheduler {
    private static final List<ScheduledTask> TASKS = new ArrayList<>();
    private static final List<ScheduledTask> TO_ADD = new ArrayList<>();

    record ScheduledTask(int runTick, Runnable task) {}

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            synchronized (TO_ADD) {
                if (!TO_ADD.isEmpty()) {
                    TASKS.addAll(TO_ADD);
                    TO_ADD.clear();
                }
            }

            int currentTick = server.getTicks();
            
            Iterator<ScheduledTask> iterator = TASKS.iterator();
            while (iterator.hasNext()) {
                ScheduledTask entry = iterator.next();
                if (currentTick >= entry.runTick) {
                    entry.task().run();
                    iterator.remove();
                }
            }
        });
    }

    public static void schedule(MinecraftServer server, int delayTicks, Runnable task) {
        synchronized (TO_ADD) {
            TO_ADD.add(new ScheduledTask(server.getTicks() + delayTicks, task));
        }
    }
}
