"""Original pixel-art finishing wrench, drawn in the mod's resource pipeline."""
from pathlib import Path
import math,struct,zlib

def pixels():
    size=32
    def material(x,y):
        dx=x-22;dy=y-9;t=(dx-dy)/math.sqrt(2);v=(dx+dy)/math.sqrt(2)
        # Open jaws point to the top-right; broad head and gripped handle read at inventory scale.
        outer=dx*dx+dy*dy<=49 and abs(dx)+abs(dy)<=9.5
        hole=abs(t)<=3.4 and abs(v)<=3.1 and abs(t)+abs(v)<=5.0
        opening=t>=0 and abs(v)<=2.7
        if outer and not(hole or opening):return 'steel'
        if -21.5<=t<=-10 and abs(v)<=3.0 and abs(v)+max(0,-20-t)<=4:return 'grip'
        if -13<=t<=-4 and abs(v)<=2:return 'steel'
        return None
    grid=[[material(x,y) for x in range(size)] for y in range(size)]
    rgba=[]
    for y in range(size):
        row=[]
        for x in range(size):
            mat=grid[y][x]
            if mat is None:row.append((0,0,0,0));continue
            neighbors=[grid[ny][nx] if 0<=nx<size and 0<=ny<size else None for nx,ny in [(x-1,y),(x,y-1),(x+1,y),(x,y+1)]]
            dx=x-22;dy=y-9;t=(dx-dy)/math.sqrt(2);v=(dx+dy)/math.sqrt(2)
            if mat=='steel':
                color=(151,163,176)
                if neighbors[0] is None or neighbors[1] is None:color=(224,233,237)
                if neighbors[2] is None or neighbors[3] is None:color=(55,65,78)
                if x+y<29 and all(n is not None for n in neighbors):color=(188,201,211)
            else:
                color=(45,53,65)
                if neighbors[0] is None or neighbors[1] is None:color=(89,103,117)
                if neighbors[2] is None or neighbors[3] is None:color=(23,28,37)
                if -19<t<-12 and -1.5<v<-.2:color=(65,167,182)
                if -18<t<-11 and int(round(t))%4==0 and v>.3:color=(29,36,46)
                if (x-9)**2+(y-22)**2<1.7:color=(177,193,203)
            row.append((*color,255))
        rgba.append(row)
    return rgba

def generate(path):
    rows=pixels();raw=b''.join(b'\0'+bytes(c for pixel in row for c in pixel) for row in rows)
    def chunk(k,d):return struct.pack('>I',len(d))+k+d+struct.pack('>I',zlib.crc32(k+d))
    path=Path(path);path.parent.mkdir(parents=True,exist_ok=True)
    path.write_bytes(b'\x89PNG\r\n\x1a\n'+chunk(b'IHDR',struct.pack('>IIBBBBB',32,32,8,6,0,0,0))+chunk(b'IDAT',zlib.compress(raw))+chunk(b'IEND',b''))

if __name__=='__main__':
    generate(Path(__file__).resolve().parents[1]/'src/main/resources/assets/lumaworks/textures/item/finishing_key.png')
