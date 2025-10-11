package lommie.dugdown;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Dugdown {

    public Dugdown() {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        CommonClass.init();

    }
}
