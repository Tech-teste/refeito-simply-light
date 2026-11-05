from pathlib import Path
import json,sys
root=Path(sys.argv[1]);assets=root/'assets/lumaworks';data=root/'data/lumaworks'
blocks={p.stem for p in (assets/'blockstates').glob('*.json')}
assert len(blocks)==224
en=json.loads((assets/'lang/en_us.json').read_text(encoding='utf-8'));pt=json.loads((assets/'lang/pt_br.json').read_text(encoding='utf-8'))
assert en.keys()==pt.keys()
for key in en:assert en[key].count('%s')==pt[key].count('%s'),key
for block in blocks:
 assert (assets/'items'/f'{block}.json').exists(),block
 assert (data/'loot_table/blocks'/f'{block}.json').exists(),block
tags={p.stem:json.loads(p.read_text(encoding='utf-8'))['values'] for p in (data/'tags/item').glob('*.json')}
recipes=[json.loads(p.read_text(encoding='utf-8')) for p in (data/'recipe').glob('*.json')]
assert len(recipes)==520
known=set()
def ready(ingredient):
 if ingredient.startswith('minecraft:'):return True
 if ingredient.startswith('#lumaworks:'):return any(i in known for i in tags[ingredient.split(':')[1]])
 assert ingredient.startswith('lumaworks:'),ingredient
 return ingredient in known
while True:
 new=known|{r['result']['id'] for r in recipes if all(ready(i) for i in list(r.get('key',{}).values()) or r.get('ingredients',[]))}
 if new==known:break
 known=new
assert known=={'lumaworks:'+b for b in blocks}|{'lumaworks:finishing_key'}
for recipe in recipes:
 assert 1<=recipe['result']['count']<=64
 if 'pattern' in recipe:assert set(''.join(recipe['pattern']))-{' '}==set(recipe['key'])
assert len(list((data/'advancement/recipes').rglob('*.json')))==520
print('PASS: 224 blocks, 225 vanilla-reachable items, 520 recipes, recipe discovery, EN/PT parity.')
