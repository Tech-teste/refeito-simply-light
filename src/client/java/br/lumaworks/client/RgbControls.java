package br.lumaworks.client;

import br.lumaworks.ClientLightingConfig;
import br.lumaworks.LumaWorks;
import br.lumaworks.rgb.NativeRgb;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

/** Local preference only: never sends a command or changes state on the server. */
@EventBusSubscriber(modid=LumaWorks.ID,value=Dist.CLIENT)
public final class RgbControls {
 public static final KeyMapping.Category CATEGORY=new KeyMapping.Category(Identifier.fromNamespaceAndPath(LumaWorks.ID,"lighting"));
 public static KeyMapping toggleKey;
 private static boolean guideRequested;
 public static void bindings(RegisterKeyMappingsEvent event){
  event.registerCategory(CATEGORY);
  // Unbound avoids stealing controls in large packs; the chat command always works.
  toggleKey=new KeyMapping("key.lumaworks.toggle_rgb",InputConstants.UNKNOWN.getValue(),CATEGORY);
  event.register(toggleKey);
 }
 @SubscribeEvent public static void commands(RegisterClientCommandsEvent event){
  event.getDispatcher().register(Commands.literal("lumargb")
   .executes(c->status())
   .then(Commands.literal("on").executes(c->set(true)))
   .then(Commands.literal("off").executes(c->set(false)))
   .then(Commands.literal("toggle").executes(c->set(!ClientLightingConfig.NATIVE_RGB.get())))
   .then(Commands.literal("status").executes(c->status()))
   .then(Commands.literal("guide").executes(c->{guideRequested=true;return 1;})));
 }
 public static int set(boolean enabled){
  ClientLightingConfig.NATIVE_RGB.set(enabled);
  ClientLightingConfig.SPEC.save();
  NativeRgb.setEnabled(enabled);
  var player=Minecraft.getInstance().player;
  if(player!=null)Minecraft.getInstance().gui.setOverlayMessage(Component.translatable(enabled?"message.lumaworks.rgb_on":"message.lumaworks.rgb_off"),false);
  return 1;
 }
 private static int status(){
  var player=Minecraft.getInstance().player;
  if(player!=null){
   if(Boolean.TRUE.equals(NativeRgb.stats().get("rapid_updates_paused")))Minecraft.getInstance().getChatListener().handleSystemMessage(Component.translatable("message.lumaworks.rgb_paused"),false);
   Minecraft.getInstance().getChatListener().handleSystemMessage(Component.translatable(ClientLightingConfig.NATIVE_RGB.get()?"message.lumaworks.rgb_on":"message.lumaworks.rgb_off"),false);
   Minecraft.getInstance().getChatListener().handleSystemMessage(Component.translatable("message.lumaworks.rgb_help"),false);
  }
  return 1;
 }
 @SubscribeEvent public static void tick(ClientTickEvent.Post event){
  var mc=Minecraft.getInstance();
  if(toggleKey!=null)while(toggleKey.consumeClick())if(mc.player!=null&&mc.screen==null)set(!ClientLightingConfig.NATIVE_RGB.get());
  if(guideRequested){guideRequested=false;if(mc.player!=null){
   var pages=new ArrayList<Component>();
   for(int i=1;i<=7;i++)pages.add(Component.translatable("guide.lumaworks.page"+i));
   mc.setScreen(new BookViewScreen(new BookViewScreen.BookAccess(pages)));
  }}
 }
 private RgbControls(){}
}
