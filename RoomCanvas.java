import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class RoomCanvas extends JPanel {
    private final ArrayList<Room> rooms;
    private final ArrayList<Furniture> furniture;  // Changed from furnituree to furniture
    private Room selectedRoom;
    private Furniture selectedFurniture;
    private Point dragStartPoint;
    private boolean isDragging = false;
    private boolean isAddingDoor = false;
    private boolean isAddingWindow = false;
    private String currentWallDirection;
    private Point wallElementStart = null;  // Added for two-point wall element placement

    public RoomCanvas(ArrayList<Room> rooms, ArrayList<Furniture> furniture) {  // Fixed parameter name
        this.rooms = rooms;
        this.furniture = furniture;  // Fixed variable name
        setPreferredSize(new Dimension(600, 600));
        setBackground(new Color(240, 240, 240));
        setFocusable(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isAddingDoor || isAddingWindow) {
                    handleWallElementPlacement(e);
                    return;
                }

                isDragging = false;
                // Prioritize furniture selection
                selectedFurniture = null;
                selectedRoom = null;

                for (Furniture f : furniture) {
                    if (f.getBounds().contains(e.getPoint())) {
                        selectedFurniture = f;
                        dragStartPoint = e.getPoint();
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        requestFocusInWindow();
                        repaint();
                        return;
                    }
                }

                // Then check rooms
                for (Room room : rooms) {
                    if (room.getBounds().contains(e.getPoint())) {
                        selectedRoom = room;
                        dragStartPoint = e.getPoint();
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        repaint();
                        return;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedRoom != null && isDragging) {
                    int newX = (e.getX() / 10) * 10;
                    int newY = (e.getY() / 10) * 10;
                    selectedRoom.setPosition(newX, newY);

                    if (CollisionChecker.checkCollision(rooms, selectedRoom)) {
                        JOptionPane.showMessageDialog(null, "Room collides with an existing room!", "Error", JOptionPane.ERROR_MESSAGE);
                        selectedRoom.setPosition((dragStartPoint.x / 10) * 10, (dragStartPoint.y / 10) * 10);
                    }
                } else if (selectedFurniture != null) {
                    int newX = (e.getX() / 10) * 10;
                    int newY = (e.getY() / 10) * 10;
                    selectedFurniture.setPosition(newX, newY);

                    if (CollisionChecker.checkCollision(furniture, selectedFurniture)) {
                        JOptionPane.showMessageDialog(null, "Furniture collides with existing furniture!", "Error", JOptionPane.ERROR_MESSAGE);
                        selectedFurniture.setPosition((dragStartPoint.x / 10) * 10, (dragStartPoint.y / 10) * 10);
                    }
                    selectedFurniture = null;
                }

                isDragging = false;
                if (!isAddingDoor && !isAddingWindow) {
                    setCursor(Cursor.getDefaultCursor());
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                isDragging = true;
                if (selectedRoom != null) {
                    int newX = (e.getX() / 10) * 10;
                    int newY = (e.getY() / 10) * 10;
                    selectedRoom.setPosition(newX, newY);
                    repaint();
                } else if (selectedFurniture != null) {
                    int newX = (e.getX() / 10) * 10;
                    int newY = (e.getY() / 10) * 10;
                    selectedFurniture.setPosition(newX, newY);
                    repaint();
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (selectedFurniture != null) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_R:
                            selectedFurniture.rotate();
                            repaint();
                            break;
                        case KeyEvent.VK_DELETE:
                            furniture.remove(selectedFurniture);
                            selectedFurniture = null;
                            repaint();
                            break;
                    }
                }
            }
        });
    }

    private void handleWallElementPlacement(MouseEvent e) {
        if (selectedRoom != null) {
            Point clickPoint = e.getPoint();
            clickPoint.x = (clickPoint.x / 10) * 10;  // Snap to grid
            clickPoint.y = (clickPoint.y / 10) * 10;

            if (wallElementStart == null) {
                // Store first point
                wallElementStart = clickPoint;
            } else {
                // Calculate width and height based on direction
                int width = 30;  // Default door/window width
                int height = 10; // Default door/window thickness
                int x = wallElementStart.x;
                int y = wallElementStart.y;

                switch (currentWallDirection.toLowerCase()) {
                    case "north":
                    case "south":
                        // Horizontal placement
                        width = 30;
                        height = 10;
                        break;
                    case "east":
                    case "west":
                        // Vertical placement
                        width = 10;
                        height = 30;
                        break;
                }

                boolean success;
                if (isAddingDoor) {
                    Door door = new Door(x, y, x + width, y + height, currentWallDirection);
                    success = selectedRoom.addDoor(door);
                } else {
                    Window window = new Window(x, y, x + width, y + height, currentWallDirection);
                    success = selectedRoom.addWindow(window);
                }

                if (!success) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot add " + (isAddingDoor ? "door" : "window") + " at this location. " +
                                    "Make sure it's placed on a wall and doesn't overlap with existing elements.",
                            "Invalid Position",
                            JOptionPane.ERROR_MESSAGE);
                }

                wallElementStart = null;
                isAddingDoor = false;
                isAddingWindow = false;
                setCursor(Cursor.getDefaultCursor());
            }
            repaint();
        }
    }

    public void startAddingDoor(String direction) {
        isAddingDoor = true;
        isAddingWindow = false;
        currentWallDirection = direction;
        wallElementStart = null;
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void startAddingWindow(String direction) {
        isAddingWindow = true;
        isAddingDoor = false;
        currentWallDirection = direction;
        wallElementStart = null;
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);

        // Draw rooms first
        for (Room room : rooms) {
            room.draw(g);
            if (room == selectedRoom) {
                g.setColor(new Color(0, 255, 0, 50));
                g.fillRect(room.getBounds().x, room.getBounds().y,
                        room.getBounds().width, room.getBounds().height);
            }
        }

        // Draw furniture on top
        for (Furniture f : furniture) {
            f.draw(g);
        }

        // Draw preview line for wall element placement
        if (wallElementStart != null && (isAddingDoor || isAddingWindow)) {
            g.setColor(Color.RED);
            Point mousePos = getMousePosition();
            if (mousePos != null) {
                g.drawLine(wallElementStart.x, wallElementStart.y,
                        mousePos.x, mousePos.y);
            }
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < getWidth(); i += 10) {
            g.drawLine(i, 0, i, getHeight());
        }
        for (int j = 0; j < getHeight(); j += 10) {
            g.drawLine(0, j, getWidth(), j);
        }
    }

    public Room getSelectedRoom() {
        return selectedRoom;
    }

    public void setSelectedRoom(Room room) {
        this.selectedRoom = room;
        this.selectedFurniture = null;
        repaint();
    }
}