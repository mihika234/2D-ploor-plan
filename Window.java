import java.awt.Color;

public class Window extends Door {
    private static final long serialVersionUID = 1L;

    public Window(int x1, int y1, int x2, int y2, String direction) {
        super(x1, y1, x2, y2, direction);
        this.color = Color.CYAN;
    }
}