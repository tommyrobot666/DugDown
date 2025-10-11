package lommie.dugdown;

import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Events {
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event){
        CommonClass.onPlayerDig(event.getPos(), (Level) event.getLevel(),event.getPlayer(),event.getState());
    }

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event){
        CommonClass.onWorldTick(event.level);
    }
}
