package lommie.dugdown;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

public class Dugdown implements ModInitializer {

    @Override
    public void onInitialize() {

        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> CommonClass.onPlayerDig(pos,world,player,state));

        ServerTickEvents.START_WORLD_TICK.register(CommonClass::onWorldTick);
    }
}
