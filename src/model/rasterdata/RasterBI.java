package model.rasterdata;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RasterBI implements Raster {

    private final BufferedImage img;
    private final Color backgroundColor = new Color(0x16161D);

    public RasterBI(int width, int height) {
        this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void clear() {
        Graphics g = img.getGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
    }

    @Override
    public void repaint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    @Override
    public int getWidth() {
        return img.getWidth();
    }

    @Override
    public int getHeight() {
        return img.getHeight();
    }

    @Override
    public int getPixel(int x, int y) {
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight())
            return img.getRGB(x, y);
        else 
            return 0;
    }

    @Override
    public void setPixel(int x, int y, int color) {
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight())
            img.setRGB(x, y, color);
    }

    public BufferedImage getImg() {
        return img;
    }
}
