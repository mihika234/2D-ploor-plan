import java.io.Serializable;
import java.util.ArrayList;

public class FloorPlanData implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Room> rooms;
    private ArrayList<Furniture> furniture;

    public FloorPlanData(ArrayList<Room> rooms, ArrayList<Furniture> furniture) {
        this.rooms = rooms;
        this.furniture = furniture;
    }

    public ArrayList<Room> getRooms() { return rooms; }
    public ArrayList<Furniture> getFurniture() { return furniture; }
}