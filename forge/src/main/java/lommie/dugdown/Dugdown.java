package lommie.dugdown;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Dugdown {

    public Dugdown() {
        CommonClass.DIG_DOWN_EVENT_REGISTRY = new MappedRegistry<>(CommonClass.DIG_DOWN_EVENT_REGISTRY_KEY, Lifecycle.stable());

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        CommonClass.init();

    }
}
