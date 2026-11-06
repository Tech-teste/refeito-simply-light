package br.lumaworks;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.*;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.util.FakePlayer;

/** New-family checks, additional to the existing family regression suite. */
public final class WallStripSmoke {
 private static int checks;
 private static void require(boolean v,String what){checks++;if(!v)throw new AssertionError(what);}
 public static int run(ServerLevel level,FakePlayer player,BlockPos p){
  checks=0;int blocks=0,states=0;
  for(var e:LumaWorks.LIGHTS.entrySet()){
   if(!(e.getValue().get() instanceof ArchitecturalLight light)||!light.isWallStrip())continue;
   blocks++;int count=light.getStateDefinition().getPossibleStates().size();states+=count;
   require(count==(light.redstoneMode()==0?128:256),"bounded wall-state definition");
   for(BlockState state:light.getStateDefinition().getPossibleStates()){
    require(!state.hasBlockEntity()&&!state.isRandomlyTicking(),"no entity/ticking wall strip");
    require(!state.getShape(level,p).isEmpty(),"nonempty cached wall shape");
    require(state.getCollisionShape(level,p).isEmpty(),"wall strips do not obstruct movement");
    require(state.getFluidState().is(Fluids.WATER)==state.getValue(ArchitecturalLight.WATER),"wall fluid matches property");
    BlockState r=state;for(int i=0;i<4;i++)r=r.rotate(Rotation.CLOCKWISE_90);
    require(r==state,"wall four-rotation identity");
    for(Mirror m:Mirror.values())require(state.mirror(m).mirror(m)==state,"wall mirror identity");
   }
   for(Direction face:new Direction[]{Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST}){
    level.removeBlock(p,false);var support=p.relative(face.getOpposite());level.setBlock(support,Blocks.STONE.defaultBlockState(),3);
    var hit=new BlockHitResult(Vec3.atCenterOf(support).add(face.getStepX()*.5,0,face.getStepZ()*.5),face,support,false);
    var ctx=new BlockPlaceContext(player,InteractionHand.MAIN_HAND,light.asItem().getDefaultInstance(),hit);
    BlockState placed=light.getStateForPlacement(ctx);
    require(placed.getValue(ArchitecturalLight.WALL_FACING)==face,"clicked wall face determines frame orientation");
    for(int mask=1;mask<16;mask++){
     BlockState s=placed.setValue(ArchitecturalLight.EDGES,mask).setValue(ArchitecturalLight.WATER,true);level.setBlock(p,s,3);
     var menu=new EdgeEditorMenu(1,player.getInventory(),p);
     require(menu.isWallStrip()&&menu.sideDirection(0)==Direction.UP&&menu.sideDirection(2)==Direction.DOWN,"vertical editor sides");
     require(menu.sideDirection(1)==face.getCounterClockWise()&&menu.sideDirection(3)==face.getClockWise(),"left/right seen from mounted face");
     for(int button=0;button<4;button++){
      level.setBlock(p,s,3);int expected=mask^(1<<button);
      require(menu.clickMenuButton(player,button)==(expected!=0),"wall editor preserves last visible side");
      var actual=level.getBlockState(p);require(actual.getValue(ArchitecturalLight.WALL_FACING)==face&&actual.getValue(ArchitecturalLight.WATER),"wall edit preserves orientation and water");
     }
    }
    if(light.redstoneMode()!=0){
     level.setBlock(p,placed.setValue(ArchitecturalLight.EDGES,10),3);
     for(boolean powered:new boolean[]{true,false,true,false}){
      level.setBlock(support,powered?Blocks.REDSTONE_BLOCK.defaultBlockState():Blocks.STONE.defaultBlockState(),3);
      var s=level.getBlockState(p);require(s.getLightEmission()==((powered^(light.redstoneMode()==2))?14:0),"wall redstone emission");
      require(s.getValue(ArchitecturalLight.EDGES)==10&&s.getValue(ArchitecturalLight.WALL_FACING)==face,"wall signal preserves sides and face");
     }
    }
    level.removeBlock(p,false);level.removeBlock(support,false);
   }
  }
  require(blocks==48&&states==10240,"exact additive family count and state ceiling");
  return checks;
 }
}
