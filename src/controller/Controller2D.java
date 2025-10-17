package controller;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import model.objectdata.Point2D;
import model.objectdata.ShapeType;
import model.rasterops.Filler;
import model.rasterops.LineRasterizerBresenham;
import model.rasterops.PolygonRasterizer;
import model.rasterops.ShapeRasterizer;
import model.rasterops.SolidFiller;
import model.rasterops.DashedFiller;
import model.rasterops.TransitionFiller;
import view.Panel;

public class Controller2D implements Controller {

    private final Panel panel;

    private Point2D start;
    private Point2D end;
    private boolean shift = false;
    private boolean control = false;

    private int primaryColorIndex = 6;
    private Color primaryColor = Color.ORANGE;

    private int secondaryColorIndex = 2;
    private Color secondaryColor = Color.GREEN;

    private int shapeTypeIndex = 0;
    
    private LineRasterizerBresenham line = new LineRasterizerBresenham();

    private PolygonRasterizer polygon = new PolygonRasterizer();

    private int fillerIndex = 2;
    private Filler filler = new SolidFiller(primaryColor);

    private ArrayList<ShapeRasterizer> shapes = new ArrayList<>();

    public Controller2D(Panel panel) {
        this.panel = panel;

        initObjects();
        initListeners(panel);
    }

    @Override
    public void initObjects() {
        panel.clear();
        panel.repaint();
    }
    private Point2D editEndPoint(Point2D start, Point2D end, boolean shift, ShapeType shape) {
        if (shift) {
            int lenX = Math.abs(start.x - end.x);
            int lenY = Math.abs(start.y - end.y);
            int minLen = Math.min(lenX, lenY);
            int maxLen = Math.max(lenX, lenY);
            int signX = (int)Math.signum((float)end.x - start.x);
            int signY = (int)Math.signum((float)end.y - start.y);
            int notStraight = (shape == ShapeType.Usecka && minLen < maxLen / 2) ? 0 : 1;

            if (lenX > lenY) {
                int y = start.y + lenX * signY * notStraight;
                return new Point2D(end.x, y);
            }  
            else {
                int x = start.x + lenY * signX * notStraight;
                return new Point2D(x, end.y);
            }
        }
        return new Point2D(end.x, end.y);
    }
    private void reDraw() {

        panel.clear();

        for (ShapeRasterizer shape : shapes) {
            shape.draw(panel.getRaster());
        }

        filler.setColors(primaryColor, secondaryColor);

        if (start != null) {
            Point2D newEnd = editEndPoint(start, end, shift, ShapeType.Usecka);
            
            line.setPoints(start, newEnd);
            line.setFiller(filler);

            line.draw(panel.getRaster());
        }

        polygon.setFiller(filler);
        polygon.draw(panel.getRaster());

        panel.repaint();
    }
    private int switchFiller(int index) {

        int len = 3;

        if (index < 0) 
            index += len;

        switch (index) {
            case 0:
                filler = new TransitionFiller();
                break;
            case 1:
                filler = new SolidFiller();
                break;
            case 2:
                filler = new DashedFiller();
                break;
            default:
                throw new RuntimeException("barva");
        }
        return (index + 1) % len;
    }
    private int switchShapeType(int index) {

        int len = 2;

        if (index < 0) 
            index += len;

        switch (index) {
            case 0:
                break;
            case 1:
                break;
            default:
                throw new RuntimeException("shape type");
        }
        return (index + 1) % len;
    }
    private Color switchColor(int index) {
        switch (index) {
            case 0:
                return Color.BLACK;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.RED;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.BLUE;
            case 5:
                return Color.ORANGE;
            case 6:
                return Color.CYAN;
            case 7:
                return Color.MAGENTA;
        }
        throw new RuntimeException("barva");
    }
    @Override
    public void initListeners(Panel panel) {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // shift
                if (e.getKeyCode() == 16) {
                    shift = true;
                } 
                // ctrl
                if (e.getKeyCode() == 17) {
                    control = true;
                } 
            }
            @Override
            public void keyReleased(KeyEvent e) {
                // enter
                if (e.getKeyCode() == 10) {
                    shapes.add(polygon);
                    polygon = new PolygonRasterizer();
                } 
                // shift
                if (e.getKeyCode() == 16) {
                    shift = false;
                } 
                // ctrl
                if (e.getKeyCode() == 17) {
                    control = false;
                } 
                // levá šipka
                if (e.getKeyCode() == 37) {
                    primaryColor = switchColor(primaryColorIndex++);
                    primaryColorIndex = primaryColorIndex % 8;
                }
                // pravá šipka
                if (e.getKeyCode() == 39) {
                    secondaryColor = switchColor(secondaryColorIndex++);
                    secondaryColorIndex = secondaryColorIndex % 8;
                }
                // dolů šipka
                if (e.getKeyCode() == 40) {
                    if (polygon.Size() > 0) {
                        shapes.add(polygon);
                        polygon = new PolygonRasterizer();
                    }
                    shapeTypeIndex = switchShapeType(shapeTypeIndex);
                }
                // nahoru šipka
                if (e.getKeyCode() == 38) {
                    fillerIndex = switchFiller(fillerIndex);
                }
                // C
                if (e.getKeyCode() == 67) {
                    shapes.clear();
                }
                // Z
                if (e.getKeyCode() == 90) {
                    if (control && shapes.size() > 0) 
                        shapes.remove(shapes.size() - 1);
                }
                reDraw();
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (start != null) {
                    end = new Point2D(e.getX(), e.getY());

                    if (shapeTypeIndex == 0) {
                        shapes.add(line);
                        line = new LineRasterizerBresenham();
                    }
                    else {
                        end = editEndPoint(start, end, shift, ShapeType.Usecka);
                        polygon.setPoints(start, end);
                    }

                    switchShapeType(shapeTypeIndex - 1);
                    primaryColor = new Color(primaryColor.getRGB());
                    secondaryColor = new Color(secondaryColor.getRGB());
                    switchFiller(fillerIndex - 1);

                    start = null;              
                    
                    reDraw();
                }
            }
            public void mouseDragged(MouseEvent e) {
                if (start == null) {
                    if (shapeTypeIndex == 0 || polygon.Size() == 0) {
                        start = new Point2D(e.getX(), e.getY());
                    }
                    else {
                        start = new Point2D(end.x, end.y);
                    }
                }
                    
                end = new Point2D(e.getX(), e.getY());
               
                reDraw();
            };
        });
    }
}
