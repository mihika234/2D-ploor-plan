import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;

public class Furniture implements Drawable, Serializable {
    private static final long serialVersionUID = 1L;

    private int x, y, width, height;
    private transient BufferedImage originalImage;
    private transient BufferedImage currentImage;
    private String imagePath;
    private String type;
    private int rotationAngle;

    public Furniture(int x, int y, String imagePath, String type) {
        this.x = x;
        this.y = y;
        this.imagePath = imagePath;
        this.type = type;
        this.rotationAngle = 0;
        loadImage();
        // Reduce image size by 50%
        reduceImageSize();
    }

    private void loadImage() {
        try {
            originalImage = ImageIO.read(new File(imagePath));
            currentImage = originalImage;
            width = originalImage.getWidth();
            height = originalImage.getHeight();
        } catch (IOException e) {
            System.err.println("Error loading image: " + imagePath);
            originalImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = originalImage.createGraphics();
            g2d.setColor(Color.GRAY);
            g2d.fillRect(0, 0, 50, 50);
            g2d.dispose();
            currentImage = originalImage;
            width = 50;
            height = 50;
        }
    }

    private void reduceImageSize() {
        width = width / 2;
        height = height / 2;
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        originalImage = resizedImage;
        currentImage = resizedImage;
    }

    @Override
    public void draw(Graphics g) {
        if (currentImage != null) {
            ((Graphics2D) g).drawImage(currentImage, x, y, null);
        }

        // Draw type text
        g.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(type);
        g.setColor(Color.BLACK);
        g.drawString(type, x + (width - textWidth) / 2, y + height + 15);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public Rectangle getRotatedBounds() {
        return new Rectangle(x, y, height, width);
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void rotate() {
        rotationAngle = (rotationAngle + 90) % 360;

        // Rotate image
        AffineTransform rotation = new AffineTransform();
        rotation.translate(width / 2.0, height / 2.0);
        rotation.rotate(Math.toRadians(rotationAngle));
        rotation.translate(-width / 2.0, -height / 2.0);

        BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.setTransform(rotation);
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();

        currentImage = rotatedImage;

        // Swap width and height
        int temp = width;
        width = height;
        height = temp;
    }

    @Override
    public boolean isWallElement() {
        // Furniture is not a wall element (door/window)
        return false;
    }

    public String getType() {
        return type;
    }

    // Method to handle deserialization and reload images
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        loadImage();
        reduceImageSize();
    }
}