package model.rasterops;

import model.objectdata.Point2D;
import model.objectdata.ShapeType;
import model.rasterdata.Raster;

public abstract class ShapeRasterizer {

    protected Filler filler;
    protected Point2D start;
    protected Point2D end;

    public ShapeRasterizer(Filler filler, Point2D start, Point2D end) {
        this.filler = filler;

        this.start = start;
        this.end = end;
    }
    public void setPoints(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
    }
    public void setFiller(Filler filler) {
        this.filler = filler;
    }

    public abstract ShapeType getShapeType();

    public abstract void draw(Raster raster);
}
