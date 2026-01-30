package lommie.dugdown;


import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Dugdown {

    public Dugdown(IEventBus eventBus) {
        CommonClass.DIG_DOWN_EVENT_REGISTRY = new MappedRegistry<>(CommonClass.DIG_DOWN_EVENT_REGISTRY_KEY, Lifecycle.stable());

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        CommonClass.init();
    }
}
