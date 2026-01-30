package lommie.dugdown;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

public class Dugdown implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.DIG_DOWN_EVENT_REGISTRY = FabricRegistryBuilder.createSimple(CommonClass.DIG_DOWN_EVENT_REGISTRY_KEY).buildAndRegister();

        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        CommonClass.init();

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> CommonClass.onPlayerDig(pos,world,player,state));

        ServerTickEvents.START_WORLD_TICK.register(CommonClass::onWorldTick);
    }
}
