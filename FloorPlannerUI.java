import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FloorPlannerUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FloorPlannerUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("2D Floor Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        // Lists for managing rooms and furniture
        ArrayList<Room> rooms = new ArrayList<>();
        ArrayList<Furniture> furniture = new ArrayList<>();

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("About"));
        menuBar.add(helpMenu);

        frame.setJMenuBar(menuBar);

        // Taskbar
        JPanel taskbar = new JPanel();
        taskbar.setLayout(new BoxLayout(taskbar, BoxLayout.Y_AXIS));
        taskbar.setPreferredSize(new Dimension(200, frame.getHeight()));
        taskbar.setBackground(new Color(240, 240, 240));

        // Room selection
        String[] roomTypes = {"Kitchen", "Bedroom", "Living Room", "Bathroom"};
        JComboBox<String> roomTypeDropdown = new JComboBox<>(roomTypes);
        roomTypeDropdown.setMaximumSize(new Dimension(180, 30));

        // Canvas for drawing
        RoomCanvas canvas = new RoomCanvas(rooms, furniture);

        // Room Type Selection Listener
        RoomTypeSelectionListener roomListener = new RoomTypeSelectionListener(frame, roomTypeDropdown, rooms, canvas);
        JButton addRoomButton = new JButton("Add Room");
        addRoomButton.addActionListener(roomListener);

        // Furniture selection dropdown
        String[] furnitureTypes = {
                "D:\\jAVA\\Project_Spam_Save1\\Furniture Images\\bed.png",
                "D:\\jAVA\\Project_Spam_Save1\\Furniture Images\\Chair.png",
                "D:\\jAVA\\Project_Spam_Save1\\Furniture Images\\Commode.png",
                "D:\\jAVA\\Project_Spam_Save1\\Furniture Images\\DiningSet.png",
                "D:\\jAVA\\Project_Spam_Save1\\Furniture Images\\Shower.png",
                "D:\\jAVA\\Project_Spam_Save1\\Furniture Images\\Sofa.png",
                "D:\\jAVA\\Project_Spam_Save1\\Furniture Images\\Stove.png",
                "D:\\jAVA\\Project_Spam_Save1\\Furniture Images\\Table.png",
                "D:\\jAVA\\Project_Spam_Save1\\Furniture Images\\WashBasin.png"
        };

        JComboBox<String> furnitureDropdown = new JComboBox<>();
        for (String path : furnitureTypes) {
            furnitureDropdown.addItem(extractFurnitureType(path));
        }
        furnitureDropdown.setMaximumSize(new Dimension(180, 30));

        // Add Furniture button
        JButton addFurnitureButton = new JButton("Add Furniture");
        addFurnitureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) furnitureDropdown.getSelectedItem();
                String imagePath = getFurnitureImagePath(selectedType);

                Furniture newFurniture = new Furniture(0, 0, imagePath, selectedType);
                if (!CollisionChecker.checkCollision(furniture, newFurniture)) {
                    furniture.add(newFurniture);
                    canvas.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Furniture collides with an existing object!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Wall Elements Panel
        JPanel wallElementsPanel = new JPanel();
        wallElementsPanel.setLayout(new BoxLayout(wallElementsPanel, BoxLayout.Y_AXIS));
        wallElementsPanel.setBorder(BorderFactory.createTitledBorder("Wall Elements"));

        String[] directions = {"north", "south", "east", "west"};
        JComboBox<String> directionDropdown = new JComboBox<>(directions);
        directionDropdown.setMaximumSize(new Dimension(180, 30));

        // Updated Door Button Action Listener
        JButton addDoorButton = new JButton("Add Door");
        addDoorButton.addActionListener(e -> {
            Room selectedRoom = canvas.getSelectedRoom();
            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(frame,
                        "Please select a room first",
                        "No Room Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String direction = (String) directionDropdown.getSelectedItem();
            canvas.startAddingDoor(direction);
        });

        // Updated Window Button Action Listener
        JButton addWindowButton = new JButton("Add Window");
        addWindowButton.addActionListener(e -> {
            Room selectedRoom = canvas.getSelectedRoom();
            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(frame,
                        "Please select a room first",
                        "No Room Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String direction = (String) directionDropdown.getSelectedItem();
            canvas.startAddingWindow(direction);
        });

        wallElementsPanel.add(new JLabel("Direction:"));
        wallElementsPanel.add(directionDropdown);
        wallElementsPanel.add(Box.createVerticalStrut(5));
        wallElementsPanel.add(addDoorButton);
        wallElementsPanel.add(Box.createVerticalStrut(5));
        wallElementsPanel.add(addWindowButton);

        // File menu actions
        saveItem.addActionListener(e -> {
            FloorPlanData data = new FloorPlanData(rooms, furniture);
            FloorPlanHandler.saveFloorPlan(data, frame);
        });

        openItem.addActionListener(e -> {
            FloorPlanData loadedData = FloorPlanHandler.loadFloorPlan(frame);
            if (loadedData != null) {
                rooms.clear();
                furniture.clear();
                rooms.addAll(loadedData.getRooms());
                furniture.addAll(loadedData.getFurniture());
                canvas.repaint();
            }
        });

        exitItem.addActionListener(e -> System.exit(0));

        // Interaction Instructions
        JTextArea instructionsArea = new JTextArea();
        instructionsArea.setEditable(false);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setText("Interaction Instructions:\n" +
                "1. Click and drag to move rooms or furniture\n" +
                "2. When furniture is selected:\n" +
                "   - Click and Press 'R' to ROTATE furniture\n" +
                "   - Click and Press 'DELETE' to REMOVE furniture\n" +
                "3. Furniture is placed on a 10-pixel grid\n" +
                "4. Avoid overlapping rooms and furniture\n" +
                "5. Select a room before adding doors or windows\n" +
                "6. Choose direction and click where you want to add doors/windows");
        instructionsArea.setBackground(new Color(240, 240, 240));
        instructionsArea.setFont(new Font("Arial", Font.PLAIN, 12));

        // Add components to taskbar
        taskbar.add(Box.createVerticalStrut(10));
        taskbar.add(new JLabel("Add Room:"));
        taskbar.add(roomTypeDropdown);
        taskbar.add(addRoomButton);
        taskbar.add(Box.createVerticalStrut(20));
        taskbar.add(new JLabel("Select Furniture:"));
        taskbar.add(furnitureDropdown);
        taskbar.add(addFurnitureButton);
        taskbar.add(Box.createVerticalStrut(20));
        taskbar.add(wallElementsPanel);
        taskbar.add(Box.createVerticalStrut(20));
        taskbar.add(instructionsArea);

        frame.add(taskbar, BorderLayout.WEST);
        frame.add(canvas, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static String extractFurnitureType(String path) {
        String filename = path.substring(path.lastIndexOf("\\") + 1);
        return filename.substring(0, filename.lastIndexOf("."));
    }

    private static String getFurnitureImagePath(String type) {
        return "D:\\jAVA\\Project_Spam_Save1\\Furniture Images\\" + type + ".png";
    }
}