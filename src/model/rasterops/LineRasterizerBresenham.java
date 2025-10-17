package model.rasterops;

import java.awt.Color;

import model.objectdata.Point2D;
import model.objectdata.ShapeType;
import model.rasterdata.Raster;

public class LineRasterizerBresenham extends ShapeRasterizer {
    public LineRasterizerBresenham(Filler filler, Point2D start, Point2D end) {
        super(filler, start, end);
    }
    public LineRasterizerBresenham() {
        super(null, null, null);
    }
    @Override
    public ShapeType getShapeType() {
        return ShapeType.Usecka;
    }
    @Override
    public void draw(Raster raster) {

        // použit Bresenhamův algoritmus

        int count = 0;

        int x1 = start.x;
        int x2 = end.x;
        int y1 = start.y;
        int y2 = end.y;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = 1;
        
        if (x1 >= x2)
            sx = -1;
        
        int sy = 1;
        
        if (y1 >= y2) 
            sy =  -1;
    
        int err = dx - dy;

        while (true) {
            double k = ((double) (end.y - start.y)) / (end.x - start.x);
            float t = 0;

            if (Math.abs(k) < 1)
                t = (float)(x1 - start.x) / (end.x - start.x);
            else
                t = (float)(y1 - start.y) / (end.y - start.y);

            Color color = filler.getColor(t, count++, new Point2D(x1, y1), raster);
            raster.setPixel(x1, y1, color.getRGB());
            
            if (x1 == x2 && y1 == y2)
                break;

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
                
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }
}
