import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    int x, y, width, height;
    Color color;
    String name;
    private ArrayList<Door> doors;
    private ArrayList<Window> windows;

    public Room(int x, int y, int width, int height, Color color, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.name = name;
        this.doors = new ArrayList<>();
        this.windows = new ArrayList<>();
    }

    public void draw(Graphics g) {
        // Draw shadow effect
        g.setColor(new Color(0, 0, 0, 50));
        g.fillRect(x + 5, y + 5, width, height);

        // Draw room
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // Draw room name
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(name, x + 5, y + 15);

        // Draw doors and windows
        for (Door door : doors) {
            door.draw(g);
        }
        for (Window window : windows) {
            window.draw(g);
        }
    }

    private boolean isPointOnWall(Point p) {
        int tolerance = 5;
        Rectangle bounds = getBounds();

        return (Math.abs(p.x - bounds.x) <= tolerance ||
                Math.abs(p.x - (bounds.x + bounds.width)) <= tolerance ||
                Math.abs(p.y - bounds.y) <= tolerance ||
                Math.abs(p.y - (bounds.y + bounds.height)) <= tolerance);
    }

    private Point snapToNearestWall(Point p) {
        Rectangle bounds = getBounds();
        Point snapped = new Point(p);

        int distToLeft = Math.abs(p.x - bounds.x);
        int distToRight = Math.abs(p.x - (bounds.x + bounds.width));
        int distToTop = Math.abs(p.y - bounds.y);
        int distToBottom = Math.abs(p.y - (bounds.y + bounds.height));

        int minDist = Math.min(Math.min(distToLeft, distToRight),
                Math.min(distToTop, distToBottom));

        if (minDist == distToLeft) {
            snapped.x = bounds.x;
            snapped.y = Math.max(bounds.y, Math.min(bounds.y + bounds.height, p.y));
        } else if (minDist == distToRight) {
            snapped.x = bounds.x + bounds.width;
            snapped.y = Math.max(bounds.y, Math.min(bounds.y + bounds.height, p.y));
        } else if (minDist == distToTop) {
            snapped.y = bounds.y;
            snapped.x = Math.max(bounds.x, Math.min(bounds.x + bounds.width, p.x));
        } else {
            snapped.y = bounds.y + bounds.height;
            snapped.x = Math.max(bounds.x, Math.min(bounds.x + bounds.width, p.x));
        }

        return snapped;
    }

    private String determineWallDirection(Point p) {
        Rectangle bounds = getBounds();
        int tolerance = 5;

        if (Math.abs(p.x - bounds.x) <= tolerance) return "west";
        if (Math.abs(p.x - (bounds.x + bounds.width)) <= tolerance) return "east";
        if (Math.abs(p.y - bounds.y) <= tolerance) return "north";
        if (Math.abs(p.y - (bounds.y + bounds.height)) <= tolerance) return "south";

        return "";
    }

    public boolean addDoor(Door door) {
        if (!isOnWall(door)) {
            return false;
        }

        for (Door existingDoor : doors) {
            if (door.checkOverlap(existingDoor)) {
                return false;
            }
        }
        for (Window window : windows) {
            if (door.checkOverlap(window)) {
                return false;
            }
        }
        doors.add(door);
        return true;
    }

    public boolean addWindow(Window window) {
        if (!isOnWall(window)) {
            return false;
        }

        for (Door door : doors) {
            if (window.checkOverlap(door)) {
                return false;
            }
        }
        for (Window existingWindow : windows) {
            if (window.checkOverlap(existingWindow)) {
                return false;
            }
        }

        windows.add(window);
        return true;
    }

    private boolean isOnWall(Door element) {
        Rectangle bounds = element.getBounds();
        Rectangle roomBounds = getBounds();
        int tolerance = 2; // Small tolerance for alignment

        // Check if the element is aligned with any wall
        boolean onNorthWall = Math.abs(bounds.y - roomBounds.y) <= tolerance;
        boolean onSouthWall = Math.abs((bounds.y + bounds.height) - (roomBounds.y + roomBounds.height)) <= tolerance;
        boolean onWestWall = Math.abs(bounds.x - roomBounds.x) <= tolerance;
        boolean onEastWall = Math.abs((bounds.x + bounds.width) - (roomBounds.x + roomBounds.width)) <= tolerance;

        // Check if the element is within the room's horizontal bounds
        boolean withinHorizontalBounds = bounds.x >= roomBounds.x - tolerance &&
                (bounds.x + bounds.width) <= roomBounds.x + roomBounds.width + tolerance;

        // Check if the element is within the room's vertical bounds
        boolean withinVerticalBounds = bounds.y >= roomBounds.y - tolerance &&
                (bounds.y + bounds.height) <= roomBounds.y + roomBounds.height + tolerance;

        // Element must be on a wall and within the room's bounds
        return ((onNorthWall || onSouthWall) && withinHorizontalBounds) ||
                ((onEastWall || onWestWall) && withinVerticalBounds);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void setPosition(int x, int y) {
        int dx = x - this.x;
        int dy = y - this.y;
        this.x = x;
        this.y = y;

        for (Door door : doors) {
            door.setPosition(door.x1 + dx, door.y1 + dy);
        }
        for (Window window : windows) {
            window.setPosition(window.x1 + dx, window.y1 + dy);
        }
    }
}