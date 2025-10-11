package lommie.dugdown;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber
public class Events {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event){
        CommonClass.onPlayerDig(event.getPos(), (Level) event.getLevel(),event.getPlayer(),event.getState());
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Pre event){
        CommonClass.onWorldTick(event.getLevel());
    }
}
