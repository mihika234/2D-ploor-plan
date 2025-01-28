import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class RoomTypeSelectionListener implements ActionListener {
    private final JFrame frame;
    private final JComboBox<String> roomTypeDropdown;
    private final ArrayList<Room> rooms;
    private final RoomCanvas canvas;

    public RoomTypeSelectionListener(JFrame frame, JComboBox<String> roomTypeDropdown, ArrayList<Room> rooms, RoomCanvas canvas) {
        this.frame = frame;
        this.roomTypeDropdown = roomTypeDropdown;
        this.rooms = rooms;
        this.canvas = canvas;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String selectedRoom = (String) roomTypeDropdown.getSelectedItem();

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Length (in grid units):"));
        JTextField lengthField = new JTextField();
        inputPanel.add(lengthField);
        inputPanel.add(new JLabel("Width (in grid units):"));
        JTextField widthField = new JTextField();
        inputPanel.add(widthField);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel,
                "Enter Dimensions for " + selectedRoom, JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int length = Integer.parseInt(lengthField.getText()) * 10; // Each grid unit is 10 pixels
                int width = Integer.parseInt(widthField.getText()) * 10;   // Each grid unit is 10 pixels

                Color roomColor = getRoomColor(selectedRoom);

                Room newRoom = new Room(0, 0, length, width, roomColor, selectedRoom);
                if (!CollisionChecker.checkCollision(rooms, newRoom)) {
                    rooms.add(newRoom);
                    canvas.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Room collides with an existing room!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid dimensions.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Color getRoomColor(String roomType) {
        switch (roomType) {
            case "Kitchen":
                return new Color(255, 229, 204);
            case "Bedroom":
                return new Color(204, 255, 229);
            case "Living Room":
                return new Color(229, 204, 255);
            case "Bathroom":
                return new Color(204, 229, 255);
            default:
                return Color.GRAY;
        }
    }
}
