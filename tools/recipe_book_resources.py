"""Vanilla recipe-book discovery, grouped by light shape and finish."""
from pathlib import Path
import json
def generate(root):
 recipes=root/'data/lumaworks/recipe';count=0
 for path in recipes.glob('*.json'):
  recipe=json.loads(path.read_text(encoding='utf-8'))
  result=recipe['result']['id'].split(':')[1]
  base=result.removesuffix('_redstone_inverted').removesuffix('_redstone')
  parts=base.split('_');form=next((f for f in ('strip_wall','strip_low','strip_high','signal','inverse','slab','panel','fixture','bulb','column','tube') if base.endswith('_'+f) or base.endswith('_'+f+'_seamless')),'finishing_key')
  recipe['group']='lumaworks:'+form+('_frameless' if result.endswith('_seamless') else '')
  if form=='finishing_key':recipe['category']='equipment'
  path.write_text(json.dumps(recipe,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
  ingredients=list(recipe.get('key',{}).values()) or recipe.get('ingredients',[])
  material=next((i for i in ingredients if i.startswith('#lumaworks:') or i.startswith('lumaworks:')),None)
  material=material or next((i for i in ('minecraft:glowstone_dust','minecraft:iron_ingot','minecraft:glass') if i in ingredients),ingredients[0])
  rid='lumaworks:'+path.stem
  advancement={'parent':'minecraft:recipes/root','criteria':{'has_material':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':material}]}},'has_recipe':{'trigger':'minecraft:recipe_unlocked','conditions':{'recipe':rid}}},'requirements':[['has_material','has_recipe']],'rewards':{'recipes':[rid]}}
  dest=root/'data/lumaworks/advancement/recipes'/form/(path.stem+'.json');dest.parent.mkdir(parents=True,exist_ok=True)
  dest.write_text(json.dumps(advancement,ensure_ascii=False,indent=2)+'\n',encoding='utf-8');count+=1
 assert count==1081,count
 print('Generated vanilla recipe-book discovery for all 1081 recipes.')
if __name__=='__main__':generate(Path(__file__).resolve().parents[1]/'src/main/resources')
