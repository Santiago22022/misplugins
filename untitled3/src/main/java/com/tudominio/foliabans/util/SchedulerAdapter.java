package com.tudominio.foliabans.util;

import com.tudominio.foliabans.FoliaBans;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public class SchedulerAdapter {

    private final FoliaBans plugin;
    private final boolean folia;

    public SchedulerAdapter(FoliaBans plugin) {
        this.plugin = plugin;
        this.folia = isFoliaPresent();
    }

    private boolean isFoliaPresent() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    /* ------------------------------- API pública ------------------------------ */

    public void runTaskForPlayer(Player player, Runnable task) {
        if (folia) {
            try {
                // player.getScheduler().run(plugin, scheduledTask -> task.run(), null);
                Object scheduler = player.getClass().getMethod("getScheduler").invoke(player);

                Method runMethod = scheduler.getClass().getMethod(
                        "run",
                        Plugin.class,
                        Consumer.class,
                        Object.class
                );

                // Consumer<ScheduledTask> que ejecuta nuestra runnable
                runMethod.invoke(
                        scheduler,
                        plugin,
                        (Consumer<?>) scheduledTask -> task.run(),
                        null
                );
            } catch (Throwable ex) {
                fallbackSync(task, ex);
            }
        } else {
            fallbackSync(task, null);
        }
    }

    public void runTask(Runnable task) {
        if (folia) {
            try {
                Object globalScheduler = plugin.getServer()
                        .getClass()
                        .getMethod("getGlobalRegionScheduler")
                        .invoke(plugin.getServer());

                Method runMethod = globalScheduler.getClass().getMethod(
                        "run",
                        Plugin.class,
                        Consumer.class
                );

                runMethod.invoke(
                        globalScheduler,
                        plugin,
                        (Consumer<?>) scheduledTask -> task.run()
                );
            } catch (Throwable ex) {
                fallbackSync(task, ex);
            }
        } else {
            fallbackSync(task, null);
        }
    }

    public void runTaskAsync(Runnable task) {
        if (folia) {
            try {
                Object asyncScheduler = plugin.getServer()
                        .getClass()
                        .getMethod("getAsyncScheduler")
                        .invoke(plugin.getServer());

                Method runNow = asyncScheduler.getClass().getMethod(
                        "runNow",
                        Plugin.class,
                        Consumer.class
                );

                runNow.invoke(
                        asyncScheduler,
                        plugin,
                        (Consumer<?>) scheduledTask -> task.run()
                );
            } catch (Throwable ex) {
                fallbackAsync(task, ex);
            }
        } else {
            fallbackAsync(task, null);
        }
    }

    public void runTaskLater(Runnable task, long delay) {
        if (folia) {
            try {
                Object globalScheduler = plugin.getServer()
                        .getClass()
                        .getMethod("getGlobalRegionScheduler")
                        .invoke(plugin.getServer());

                Method runDelayed = globalScheduler.getClass().getMethod(
                        "runDelayed",
                        Plugin.class,
                        Consumer.class,
                        long.class
                );

                runDelayed.invoke(
                        globalScheduler,
                        plugin,
                        (Consumer<?>) scheduledTask -> task.run(),
                        delay
                );
            } catch (Throwable ex) {
                fallbackLater(task, delay, ex);
            }
        } else {
            fallbackLater(task, delay, null);
        }
    }

    /* --------------------------- Métodos de respaldo -------------------------- */

    private void fallbackSync(Runnable task, Throwable cause) {
        if (cause != null)
            plugin.getLogger().warning("Fallo el scheduler de Folia, usando Bukkit: " + cause);
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    private void fallbackAsync(Runnable task, Throwable cause) {
        if (cause != null)
            plugin.getLogger().warning("Fallo el async-scheduler de Folia, usando Bukkit: " + cause);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    private void fallbackLater(Runnable task, long delay, Throwable cause) {
        if (cause != null)
            plugin.getLogger().warning("Fallo el delayed-scheduler de Folia, usando Bukkit: " + cause);
        plugin.getServer().getScheduler().runTaskLater(plugin, task, delay);
    }
}
