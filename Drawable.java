import java.awt.*;
import java.io.Serializable;

public interface Drawable extends Serializable {
    void draw(Graphics g);
    Rectangle getBounds();
    Rectangle getRotatedBounds();
    void setPosition(int x, int y);
    void rotate();
    boolean isWallElement(); // New method to identify wall elements (doors/windows)
}