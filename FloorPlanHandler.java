import javax.swing.*;
import java.io.*;

public class FloorPlanHandler {
    public static void saveFloorPlan(FloorPlanData data, JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Floor Plan");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".flp");
            }

            @Override
            public String getDescription() {
                return "Floor Plan Files (*.flp)";
            }
        });

        int userChoice = fileChooser.showSaveDialog(parent);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".flp")) {
                file = new File(file.getPath() + ".flp");
            }

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(data);
                JOptionPane.showMessageDialog(parent,
                        "Floor plan saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                        "Error saving floor plan: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static FloorPlanData loadFloorPlan(JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Floor Plan");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".flp");
            }

            @Override
            public String getDescription() {
                return "Floor Plan Files (*.flp)";
            }
        });

        int userChoice = fileChooser.showOpenDialog(parent);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()))) {
                FloorPlanData data = (FloorPlanData) in.readObject();
                JOptionPane.showMessageDialog(parent,
                        "Floor plan loaded successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                return data;
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(parent,
                        "Error loading floor plan: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}