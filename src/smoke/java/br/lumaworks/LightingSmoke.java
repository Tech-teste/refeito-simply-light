package br.lumaworks;

import java.nio.file.*;
import net.minecraft.core.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/** Included only in the development smoke-test run, never in the release jar. */
@EventBusSubscriber(modid = LumaWorks.ID)
public final class LightingSmoke {
    private static int checks;
    private static void require(boolean value, String message) {
        checks++;
        if (!value) throw new AssertionError(message);
    }
    @SubscribeEvent
    public static void started(ServerStartedEvent event) {
        if (!Boolean.getBoolean("lumaworks.smoke")) return;
        var server = event.getServer();
        var level = server.overworld();
        String result;
        try {
            require(LumaWorks.LIGHTS.size() == 432, "432 registered lights");
            int totalStates=LumaWorks.LIGHTS.values().stream().mapToInt(b -> b.get().getStateDefinition().getPossibleStates().size()).sum();
            require(totalStates <= 21000,"bounded state count: "+totalStates);
            BlockPos origin = new BlockPos(0, 220, 0);
            var movableEmpty = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK,
                net.minecraft.resources.Identifier.fromNamespaceAndPath("create", "movable_empty_collider"));
            int emptyColliderLights = 0;
            for (var holder : LumaWorks.LIGHTS.values()) {
                var block = holder.get();
                for (var candidate : block.getStateDefinition().getPossibleStates()) {
                    boolean empty = candidate.getCollisionShape(level, origin).isEmpty();
                    require(candidate.is(movableEmpty) == empty, "Create movable empty-collider coverage: " + candidate);
                }
                if (block.defaultBlockState().is(movableEmpty)) emptyColliderLights++;
            }
            require(emptyColliderLights == 240, "all 240 collisionless lamps tagged for Create");
            for (var entry : LumaWorks.LIGHTS.entrySet()) {
                Block block = entry.getValue().get();
                BlockState state = block.defaultBlockState();
                var itemStack=block.asItem().getDefaultInstance();
                var equipment=itemStack.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
                require(equipment!=null && equipment.slot()==net.minecraft.world.entity.EquipmentSlot.HEAD,"every light can be worn on the head");
                if(entry.getKey().endsWith("_column")) {
                    var attributes=itemStack.get(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS);
                    require(attributes!=null && attributes.modifiers().stream().anyMatch(a -> a.attribute().equals(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) && a.modifier().amount()==4.0 && a.slot()==net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND),"every post retains the weapon attribute");
                }
                require(!state.hasBlockEntity(), entry.getKey() + " has no block entity");
                require(!state.isRandomlyTicking(), entry.getKey() + " has no random-tick workload");
                if(state.hasProperty(ArchitecturalLight.LIT)) for(boolean lit:new boolean[]{false,true})
                    require(state.setValue(ArchitecturalLight.LIT,lit).getLightEmission()==(lit?(entry.getKey().matches(".*_(bulb|strip_low|strip_high|strip_wall)_redstone.*")?14:15):0),"correct cached light level for every color and finish");
                require(!state.getShape(level, origin).isEmpty(), entry.getKey() + " has selection shape");
                if (block instanceof ArchitecturalLight) {
                    BlockState wet = state.setValue(ArchitecturalLight.WATER, true);
                    require(wet.getFluidState().is(Fluids.WATER), entry.getKey() + " water state");
                    if(state.hasProperty(ArchitecturalLight.FACING)) for (Direction direction : Direction.values()) require(!state.setValue(ArchitecturalLight.FACING, direction).getShape(level, origin).isEmpty(), "oriented shape");
                    if(state.hasProperty(ArchitecturalLight.FRAMED)) {
                        var smooth=state.setValue(ArchitecturalLight.FRAMED,false);
                        require(smooth.getLightEmission()==state.getLightEmission(),"frame does not change light level");
                        require(smooth.getShape(level,origin).equals(state.getShape(level,origin)),"frame does not change shape");
                    }
                    if(state.hasProperty(ArchitecturalLight.EDGES)) for (int mask = 0; mask < 16; mask++) {
                        BlockState edged = state.setValue(ArchitecturalLight.EDGES, mask);
                        BlockState rotated = edged;
                        for (int i = 0; i < 4; i++) rotated = rotated.rotate(Rotation.CLOCKWISE_90);
                        require(rotated == edged, "four rotations preserve state");
                        require(edged.mirror(Mirror.LEFT_RIGHT).mirror(Mirror.LEFT_RIGHT) == edged, "mirror round trip");
                    }
                }
            }
            // Full lamps must hide internal faces, including different colors and finishes.
            for(var first:LumaWorks.LIGHTS.values()) {
                var state=first.get().defaultBlockState();
                if(!state.hasProperty(ArchitecturalLight.LIT)||!state.hasProperty(ArchitecturalLight.FRAMED))continue;
                for(var second:LumaWorks.LIGHTS.values()) {
                    var neighbor=second.get().defaultBlockState();
                    if(!neighbor.hasProperty(ArchitecturalLight.LIT)||!neighbor.hasProperty(ArchitecturalLight.FRAMED))continue;
                    for(var direction:Direction.values())require(!Block.shouldRenderFace(state,neighbor,direction),"internal full lamp face hidden");
                }
                for(var direction:Direction.values())require(Block.shouldRenderFace(state,Blocks.AIR.defaultBlockState(),direction),"outer lamp face remains visible");
            }
            var fake=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);
            fake.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
            fake.setShiftKeyDown(true);
            fake.setPos(.5,220,2.5);
            checks += WallStripSmoke.run(level, fake, origin);
            for(var registration:LumaWorks.LIGHTS.values()) {
                var block=registration.get();
                if(!block.defaultBlockState().hasProperty(ArchitecturalLight.EDGES)) continue;
                for(int mask=1;mask<16;mask++) for(int button=0;button<4;button++) {
                    level.setBlock(origin,block.defaultBlockState().setValue(ArchitecturalLight.EDGES,mask).setValue(ArchitecturalLight.WATER,true),3);
                    var editor=new EdgeEditorMenu(1,fake.getInventory(),origin);
                    int expected=mask^(1<<button);
                    require(editor.clickMenuButton(fake,button)==(expected!=0),"direction edit result");
                    require(level.getBlockState(origin).getValue(ArchitecturalLight.EDGES)==(expected==0?mask:expected),"direction mask persisted");
                    require(level.getBlockState(origin).getValue(ArchitecturalLight.WATER),"editor preserves water");
                }
            }
            // Placement must reproduce the old mod's mixed slab/panel alignment and crouch override.
            for(String from:new String[]{"white_slab","cyan_panel_seamless"}) for(String to:new String[]{"red_panel","blue_slab_seamless"}) {
                var target=LumaWorks.LIGHTS.get(to).get();
                level.removeBlock(origin,false);
                level.setBlock(origin.below(),LumaWorks.LIGHTS.get(from).get().defaultBlockState().setValue(ArchitecturalLight.FACING,Direction.NORTH),3);
                fake.setShiftKeyDown(false);
                var hit=new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(origin),Direction.UP,origin,false);
                var context=new net.minecraft.world.item.context.BlockPlaceContext(fake,net.minecraft.world.InteractionHand.MAIN_HAND,target.asItem().getDefaultInstance(),hit);
                require(target.getStateForPlacement(context).getValue(ArchitecturalLight.FACING)==Direction.NORTH,"mixed thin lights align without crouching");
                fake.setShiftKeyDown(true);
                context=new net.minecraft.world.item.context.BlockPlaceContext(fake,net.minecraft.world.InteractionHand.MAIN_HAND,target.asItem().getDefaultInstance(),hit);
                require(target.getStateForPlacement(context).getValue(ArchitecturalLight.FACING)==Direction.UP,"crouching uses clicked face");
            }
            level.removeBlock(origin.below(),false);
            level.removeBlock(origin,false);
            level.setBlock(origin.north(),Blocks.DIRT_PATH.defaultBlockState(),3);
            require(Block.canSupportCenter(level,origin.north(),Direction.SOUTH),"thin support has a solid center");
            require(!level.getBlockState(origin.north()).isFaceSturdy(level,origin.north(),Direction.SOUTH),"thin support lacks a full face");
            var low=LumaWorks.LIGHTS.get("white_strip_low").get();
            var stripPlacementContext=new net.minecraft.world.item.context.BlockPlaceContext(fake,net.minecraft.world.InteractionHand.MAIN_HAND,low.asItem().getDefaultInstance(),new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(origin),Direction.UP,origin,false));
            require(low.getStateForPlacement(stripPlacementContext).getValue(ArchitecturalLight.EDGES)==1,"strip detects centered support, as the reference mod does");
            level.removeBlock(origin.north(),false);
            for(var direction:Direction.values()) {
                var panel=LumaWorks.LIGHTS.get("white_panel").get().defaultBlockState().setValue(ArchitecturalLight.FACING,direction);
                var box=panel.getShape(level,origin).bounds();
                require(Math.abs(direction.getAxis().choose(box.maxX-box.minX,box.maxY-box.minY,box.maxZ-box.minZ)-.25)<.00001,"panel is a quarter block on every face");
            }
            for(var type:net.minecraft.world.level.pathfinder.PathComputationType.values()) {
                require(!LumaWorks.LIGHTS.get("white_tube").get().defaultBlockState().isPathfindable(type),"rods are not a walk-through path");
                require(!LumaWorks.LIGHTS.get("white_column").get().defaultBlockState().isPathfindable(type),"posts are not a walk-through path");
            }
            var strip=LumaWorks.LIGHTS.get("white_strip_high").get();
            level.setBlock(origin,strip.defaultBlockState().setValue(ArchitecturalLight.EDGES,1),3);
            var editor=new EdgeEditorMenu(1,fake.getInventory(),origin);
            require(editor.clickMenuButton(fake,4) && editor.mask()==15,"all sides button");
            require(!editor.clickMenuButton(fake,-1) && !editor.clickMenuButton(fake,5) && !editor.clickMenuButton(fake,Integer.MAX_VALUE),"invalid button ids rejected");
            fake.setPos(100,220,100);
            require(!editor.stillValid(fake) && !editor.clickMenuButton(fake,0),"remote edits rejected");
            fake.setPos(.5,220,2.5);
            fake.getAbilities().mayBuild=false;
            require(!editor.clickMenuButton(fake,0),"non-builder rejected");
            fake.getAbilities().mayBuild=true;
            java.util.function.Consumer<net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock> cancel=e->{if(e.getPos().equals(origin))e.setCanceled(true);};
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(cancel);
            try {require(!editor.clickMenuButton(fake,0) && editor.mask()==15,"claim-style event cancellation honored");}
            finally {net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(cancel);}
            require(editor.clickMenuButton(fake,0) && editor.mask()==14,"edit allowed after protection listener removed");
            editor.clickMenuButton(fake,4);
            java.util.function.Consumer<net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock> deny=e->{if(e.getPos().equals(origin))e.setUseBlock(net.minecraft.util.TriState.FALSE);};
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(deny);
            try {require(!editor.clickMenuButton(fake,0),"explicit block-use denial honored");}
            finally {net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(deny);}
            var other=net.neoforged.neoforge.common.util.FakePlayerFactory.get(level,new com.mojang.authlib.GameProfile(java.util.UUID.fromString("94badb4f-3294-4660-a113-50ae17a76061"),"LumaTestOther"));
            other.setPos(.5,220,2.5);
            other.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
            require(!editor.stillValid(other) && !editor.clickMenuButton(other,0),"other player cannot use the menu");
            fake.setGameMode(net.minecraft.world.level.GameType.SPECTATOR);
            require(!editor.clickMenuButton(fake,0),"spectator edits rejected");
            fake.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
            level.setBlock(origin,Blocks.STONE.defaultBlockState(),3);
            require(!editor.stillValid(fake) && !editor.clickMenuButton(fake,0),"replaced block invalidates menu");
            level.removeBlock(origin,false);
            var key=LumaWorks.FINISHING_KEY.get();
            require(key.getCraftingRemainder(key.getDefaultInstance()).create().is(key),"crafting returns the finishing key");
            var namedKey=key.getDefaultInstance();
            namedKey.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Minha chave"));
            require("Minha chave".equals(key.getCraftingRemainder(namedKey).create().getHoverName().getString()),"crafting preserves key custom name");
            require("Minha chave".equals(key.getCraftingRemainder(net.minecraft.world.item.ItemStackTemplate.fromNonEmptyStack(namedKey)).create().getHoverName().getString()),"template crafting preserves key metadata");
            // Exercise the actual vanilla crafter, not just a recipe's remainder helper.
            BlockPos machine=origin.east(10),output=machine.north();
            level.setBlock(machine,Blocks.CRAFTER.defaultBlockState(),3);
            level.setBlock(output,Blocks.CHEST.defaultBlockState(),3);
            var crafter=(net.minecraft.world.level.block.entity.CrafterBlockEntity)level.getBlockEntity(machine);
            var chest=(net.minecraft.world.level.block.entity.ChestBlockEntity)level.getBlockEntity(output);
            var pulse=CrafterBlock.class.getDeclaredMethod("dispenseFrom",BlockState.class,net.minecraft.server.level.ServerLevel.class,BlockPos.class);pulse.setAccessible(true);
            for(var color:net.minecraft.world.item.DyeColor.values()) for(String form:new String[]{"fixture","bulb","tube","strip_low","strip_high"}) {
                String base=color.getName()+"_"+form;
                for(int conversion=0;conversion<3;conversion++) {
                    String from=base+(conversion==0?"":conversion==1?"_redstone":"_redstone_inverted");
                    String to=base+(conversion==1?"_redstone_inverted":"_redstone");
                    crafter.clearContent();chest.clearContent();
                    crafter.setItem(0,LumaWorks.LIGHTS.get(from).get().asItem().getDefaultInstance());
                    if(conversion==0)crafter.setItem(1,net.minecraft.world.item.Items.REDSTONE.getDefaultInstance());
                    pulse.invoke(Blocks.CRAFTER,level.getBlockState(machine),level,machine);
                    int count=0;for(int slot=0;slot<chest.getContainerSize();slot++)if(chest.getItem(slot).is(LumaWorks.LIGHTS.get(to).get().asItem()))count+=chest.getItem(slot).getCount();
                    require(count==1&&crafter.isEmpty(),"actual Crafter upgrades/toggles small lights "+from);
                }
                if(form.startsWith("strip")) for(String suffix:new String[]{"_redstone","_redstone_inverted"}) {
                    String from=base+suffix,to=color.getName()+"_"+(form.equals("strip_low")?"strip_high":"strip_low")+suffix;
                    crafter.clearContent();chest.clearContent();
                    crafter.setItem(0,LumaWorks.LIGHTS.get(from).get().asItem().getDefaultInstance());crafter.setItem(1,LumaWorks.LIGHTS.get(from).get().asItem().getDefaultInstance());
                    pulse.invoke(Blocks.CRAFTER,level.getBlockState(machine),level,machine);
                    int count=0;for(int slot=0;slot<chest.getContainerSize();slot++)if(chest.getItem(slot).is(LumaWorks.LIGHTS.get(to).get().asItem()))count+=chest.getItem(slot).getCount();
                    require(count==2&&crafter.isEmpty(),"actual Crafter changes strip height without loss");
                }
            }
            for(boolean reverse:new boolean[]{false,true}) {
                String inputName="cyan_panel"+(reverse?"_seamless":""),outputName="cyan_panel"+(reverse?"":"_seamless");
                crafter.clearContent();chest.clearContent();
                crafter.setItem(0,LumaWorks.LIGHTS.get(inputName).get().asItem().getDefaultInstance());crafter.setItem(1,namedKey.copy());
                pulse.invoke(Blocks.CRAFTER,level.getBlockState(machine),level,machine);
                int tools=0,lights=0;
                for(int slot=0;slot<chest.getContainerSize();slot++) {
                    var stack=chest.getItem(slot);
                    if(stack.is(key)){tools+=stack.getCount();require(stack.getHoverName().getString().equals("Minha chave"),"crafter returns named key unchanged");}
                    if(stack.is(LumaWorks.LIGHTS.get(outputName).get().asItem()))lights+=stack.getCount();
                }
                require(tools==1 && lights==1,"vanilla crafter outputs exactly one light and one reusable key");
                require(crafter.isEmpty(),"vanilla crafter consumed the two inputs exactly once");
            }
            crafter.clearContent();chest.clearContent();level.removeBlock(machine,false);level.removeBlock(output,false);
            fake.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,key.getDefaultInstance());
            for(var color:net.minecraft.world.item.DyeColor.values()) for(String family:new String[]{"signal","inverse","slab","panel"}) {
                String name=color.getName()+"_"+family;
                var block=(ArchitecturalLight)LumaWorks.LIGHTS.get(name).get();
                var seamless=LumaWorks.LIGHTS.get(name+"_seamless").get();
                for(boolean reverse:new boolean[]{false,true}) {
                    var input=net.minecraft.world.item.crafting.CraftingInput.of(2,1,java.util.List.of((reverse?seamless:block).asItem().getDefaultInstance(),key.getDefaultInstance()));
                    var recipe=server.getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING,input,level).orElseThrow().value();
                    require(recipe.assemble(input).is((reverse?block:seamless).asItem()),"crafting produces distinct finish "+name);
                    require(recipe.getRemainingItems(input).get(1).is(key),"recipe returns reusable key "+name);
                }
                var initial=block.defaultBlockState().setValue(ArchitecturalLight.WATER,true);
                if(initial.hasProperty(ArchitecturalLight.FACING))initial=initial.setValue(ArchitecturalLight.FACING,Direction.EAST);
                level.setBlock(origin,initial,3);
                var hit=new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(origin),Direction.UP,origin,false);
                var context=new net.minecraft.world.item.context.UseOnContext(fake,net.minecraft.world.InteractionHand.MAIN_HAND,hit);
                key.useOn(context);
                require(level.getBlockState(origin).is(seamless),"tool converts to distinct seamless block "+name);
                require(level.getBlockState(origin).getValue(ArchitecturalLight.WATER),"conversion retains water");
                if(initial.hasProperty(ArchitecturalLight.FACING))require(level.getBlockState(origin).getValue(ArchitecturalLight.FACING)==Direction.EAST,"conversion retains facing");
                key.useOn(context);
                require(level.getBlockState(origin).is(block),"tool restores frame "+name);
                fake.getAbilities().mayBuild=false;
                key.useOn(context);
                require(level.getBlockState(origin).is(block),"tool rejects non-builders");
                fake.getAbilities().mayBuild=true;
                // Legacy unframed states must migrate to the new item on breaking.
                level.setBlock(origin,initial.setValue(ArchitecturalLight.FRAMED,false),3);
                var bounds=new net.minecraft.world.phys.AABB(origin).inflate(5);
                level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,bounds).forEach(e->e.discard());
                level.destroyBlock(origin,true);
                var drops=level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,bounds);
                require(drops.size()==1 && drops.getFirst().getItem().is(seamless.asItem()),"legacy drop migrates to seamless item "+name);
            }
            // All five small forms, every color and both modes: physical power changes,
            // orientation/directions, water and actual crafting outputs.
            for(var color:net.minecraft.world.item.DyeColor.values()) for(String form:new String[]{"fixture","bulb","tube","strip_low","strip_high"}) {
                String base=color.getName()+"_"+form;
                for(String suffix:new String[]{"redstone","redstone_inverted"}) {
                    var block=LumaWorks.LIGHTS.get(base+"_"+suffix).get();
                    var initial=block.defaultBlockState().setValue(ArchitecturalLight.WATER,true);
                    if(initial.hasProperty(ArchitecturalLight.FACING))initial=initial.setValue(ArchitecturalLight.FACING,Direction.WEST);
                    if(initial.hasProperty(ArchitecturalLight.EDGES))initial=initial.setValue(ArchitecturalLight.EDGES,5);
                    level.removeBlock(origin.east(),false);
                    level.setBlock(origin,initial,3);
                    for(boolean powered:new boolean[]{true,false,true,false}) {
                        level.setBlock(origin.east(),powered?Blocks.REDSTONE_BLOCK.defaultBlockState():Blocks.AIR.defaultBlockState(),3);
                        var actual=level.getBlockState(origin);
                        boolean lit=powered^suffix.equals("redstone_inverted");
                        require(actual.getValue(ArchitecturalLight.LIT)==lit,"small light power mode "+base+suffix);
                        require((actual.getLightEmission()>0)==lit,"small emission follows power");
                        require(actual.getValue(ArchitecturalLight.WATER),"power preserves water");
                        if(initial.hasProperty(ArchitecturalLight.FACING))require(actual.getValue(ArchitecturalLight.FACING)==Direction.WEST,"power preserves facing");
                        if(initial.hasProperty(ArchitecturalLight.EDGES))require(actual.getValue(ArchitecturalLight.EDGES)==5,"power preserves strip mask");
                    }
                    var input=net.minecraft.world.item.crafting.CraftingInput.of(1,1,java.util.List.of(block.asItem().getDefaultInstance()));
                    var recipe=server.getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING,input,level).orElseThrow().value();
                    require(recipe.assemble(input).is(LumaWorks.LIGHTS.get(base+(suffix.equals("redstone")?"_redstone_inverted":"_redstone")).get().asItem()),"single-item mode toggle");
                    level.removeBlock(origin,false);
                }
                var input=net.minecraft.world.item.crafting.CraftingInput.of(2,1,java.util.List.of(LumaWorks.LIGHTS.get(base).get().asItem().getDefaultInstance(),net.minecraft.world.item.Items.REDSTONE.getDefaultInstance()));
                var upgrade=server.getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING,input,level).orElseThrow().value();
                require(upgrade.assemble(input).is(LumaWorks.LIGHTS.get(base+"_redstone").get().asItem()),"vanilla redstone upgrade");
            }
            for (String type : new String[]{"signal", "inverse"}) {
                Block block = LumaWorks.LIGHTS.get("white_" + type).get();
                level.setBlock(origin, block.defaultBlockState().setValue(ArchitecturalLight.FRAMED,false), 3);
                level.setBlock(origin.east(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
                require(level.getBlockState(origin).getValue(ArchitecturalLight.LIT) == type.equals("signal"), "powered " + type);
                require(!level.getBlockState(origin).getValue(ArchitecturalLight.FRAMED),"redstone preserves seamless style");
                level.removeBlock(origin.east(), false);
                require(level.getBlockState(origin).getValue(ArchitecturalLight.LIT) == type.equals("inverse"), "unpowered " + type);
                level.removeBlock(origin, false);
            }
            // Replacement with suppressed drops (commands/rollback) must not manufacture a post item.
            {
                var bounds=new net.minecraft.world.phys.AABB(origin).inflate(5);
                level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,bounds).forEach(e->e.discard());
                var column=LumaWorks.LIGHTS.get("white_column").get();
                level.setBlock(origin,column.defaultBlockState(),2);
                column.setPlacedBy(level,origin,column.defaultBlockState(),null,column.asItem().getDefaultInstance());
                level.setBlock(origin.above(),Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
                require(level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,bounds).stream().noneMatch(e->e.getItem().is(column.asItem())),"suppressed replacement of middle post must not drop an item");
                for(int part=0;part<3;part++)level.removeBlock(origin.above(part),false);
            }
            for (int broken = 0; broken < 3; broken++) {
                var bounds = new net.minecraft.world.phys.AABB(origin).inflate(5);
                level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, bounds).forEach(e -> e.discard());
                var column = LumaWorks.LIGHTS.get("white_column").get();
                BlockState base = column.defaultBlockState().setValue(ColumnLight.WATER, true);
                level.setBlock(origin.above(), Blocks.WATER.defaultBlockState(), 2);
                level.setBlock(origin, base, 2);
                column.setPlacedBy(level, origin, base, null, column.asItem().getDefaultInstance());
                require(level.getBlockState(origin.above()).getValue(ColumnLight.WATER), "middle retains water");
                require(!level.getBlockState(origin.above(2)).getValue(ColumnLight.WATER), "dry top remains dry");
                level.destroyBlock(origin.above(broken), true);
                int drops = level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, bounds).stream().filter(e -> e.getItem().is(column.asItem())).mapToInt(e -> e.getItem().getCount()).sum();
                require(drops == 1, "exactly one column drop from segment " + broken + ": " + drops);
                for (int i = 0; i < 3; i++) require(!level.getBlockState(origin.above(i)).is(column), "column dismantles from segment " + broken + " remaining=" + i + " " + level.getBlockState(origin.above(i)));
                require(level.getFluidState(origin).is(Fluids.WATER), "base water restored");
                require(level.getFluidState(origin.above()).is(Fluids.WATER), "middle water restored");
                for (int i = 0; i < 3; i++) level.removeBlock(origin.above(i), false);
            }
            for(var mode:new net.minecraft.world.level.GameType[]{net.minecraft.world.level.GameType.SURVIVAL,net.minecraft.world.level.GameType.CREATIVE}) for(int segment=0;segment<3;segment++) {
                var bounds=new net.minecraft.world.phys.AABB(origin).inflate(5);
                level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,bounds).forEach(e->e.discard());
                for(int part=0;part<3;part++)level.setBlock(origin.above(part),Blocks.AIR.defaultBlockState(),Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
                var column=LumaWorks.LIGHTS.get("white_column").get();
                level.setBlock(origin,column.defaultBlockState(),2);column.setPlacedBy(level,origin,column.defaultBlockState(),null,column.asItem().getDefaultInstance());
                fake.setGameMode(mode);require(fake.gameMode.destroyBlock(origin.above(segment)),"normal player break succeeds");
                int count=level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,bounds).stream().filter(e->e.getItem().is(column.asItem())).mapToInt(e->e.getItem().getCount()).sum();
                require(count==(mode==net.minecraft.world.level.GameType.SURVIVAL?1:0),"post drop count through actual player break: "+mode+" segment "+segment+" count "+count);
                for(int part=0;part<3;part++)require(!level.getBlockState(origin.above(part)).is(column),"no orphan post segment");
            }
            result = "PASS: " + checks + " runtime assertions; 432 lights; shapes, fluids, redstone, columns and directional editor (144 strip types, permissions and invalid requests).\n";
        } catch (Throwable failure) {
            failure.printStackTrace();
            result = "FAIL after " + checks + " checks: " + failure + "\n";
        }
        System.out.println("LUMA_SMOKE " + result);
        try { Files.writeString(Path.of("smoke-result.txt"), result); } catch (Exception e) { throw new RuntimeException(e); }
        server.halt(false);
    }
}
