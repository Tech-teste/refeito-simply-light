package br.lumaworks.client;

import br.lumaworks.LumaWorks;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = LumaWorks.ID, dist = Dist.CLIENT)
public final class LumaClient {
    public LumaClient(IEventBus bus, ModContainer container) { container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new); bus.addListener(LumaClient::screens); bus.addListener(RgbControls::bindings); }
    private static void screens(RegisterMenuScreensEvent event) {
        event.register(LumaWorks.EDGE_EDITOR.get(), EdgeEditorScreen::new);
    }
}
