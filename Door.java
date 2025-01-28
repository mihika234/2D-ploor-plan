import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;

public class Door implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int x1, y1, x2, y2;
    protected Color color;
    protected String direction;

    public Door(int x1, int y1, int x2, int y2, String direction) {
        this.x1 = Math.min(x1, x2);  // Ensure x1 is the smaller value
        this.y1 = Math.min(y1, y2);  // Ensure y1 is the smaller value
        this.x2 = Math.max(x1, x2);  // Ensure x2 is the larger value
        this.y2 = Math.max(y1, y2);  // Ensure y2 is the larger value
        this.direction = direction;
        this.color = Color.ORANGE;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x1, y1, x2 - x1, y2 - y1);
        g.setColor(Color.BLACK);
        g.drawRect(x1, y1, x2 - x1, y2 - y1);
    }

    public Rectangle getBounds() {
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    public void setPosition(int x, int y) {
        int width = x2 - x1;
        int height = y2 - y1;
        this.x1 = x;
        this.y1 = y;
        this.x2 = x + width;
        this.y2 = y + height;
    }

    public boolean checkOverlap(Door other) {
        return getBounds().intersects(other.getBounds());
    }
}