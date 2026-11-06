"""Presentation-only held/ground models; keep placed geometry and head context intact."""
from pathlib import Path
import copy,json

def generate(root):
 root=Path(root);assets=root/'assets/lumaworks'
 cache={}
 for path in sorted((assets/'items').glob('*.json')):
  name=path.stem
  if name=='finishing_key' or '_signal' in name or '_inverse' in name:continue
  definition=json.loads(path.read_text(encoding='utf-8'));node=definition['model']
  original=node.get('fallback',node)
  source=original['model']
  # Thin floor luminaires use their centered inventory geometry, preserving all four edges.
  if node.get('cases') and any(f in name for f in ['_fixture','_bulb','_strip_low','_strip_high']):
   source=node['cases'][0]['model']['model']
  if source not in cache:
   model=copy.deepcopy(json.loads((assets/'models'/(source.split(':')[1]+'.json')).read_text(encoding='utf-8')))
   for axis in range(3):
    lo=min(e['from'][axis] for e in model['elements']);hi=max(e['to'][axis] for e in model['elements'])
    for e in model['elements']:
     e['from'][axis]+=8-(lo+hi)/2;e['to'][axis]+=8-(lo+hi)/2
   small='_bulb' in name or '_fixture' in name
   strip='_strip_' in name
   wall='_strip_wall' in name
   scale=.85 if small else .5 if ('_column' in name or '_tube' in name) else .4
   rise=3 if small else 4 if strip else 3
   model['display']={
    'firstperson_righthand':{'rotation':[0 if wall else 20 if strip else 0,180 if wall else 45,0],'translation':[0,rise,0],'scale':[scale]*3},
    'firstperson_lefthand':{'rotation':[0 if wall else 20 if strip else 0,180 if wall else 225,0],'translation':[0,rise,0],'scale':[scale]*3},
    'ground':{'rotation':[0,0,0],'translation':[0,3,0],'scale':[.5 if small else .3]*3}}
   target='item/context_'+source.split('/')[-1]
   (assets/'models'/ (target+'.json')).write_text(json.dumps(model,indent=2)+'\n',encoding='utf-8')
   cache[source]='lumaworks:'+target
  context={'when':['firstperson_righthand','firstperson_lefthand','ground'],'model':{'type':'minecraft:model','model':cache[source]}}
  if node.get('type')=='minecraft:select':
   node['cases'].append(context)
  else:definition['model']={'type':'minecraft:select','property':'minecraft:display_context','cases':[context],'fallback':original}
  path.write_text(json.dumps(definition,indent=2)+'\n',encoding='utf-8')
