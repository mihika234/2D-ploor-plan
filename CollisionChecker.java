import java.util.ArrayList;

public class CollisionChecker {
    public static <T> boolean checkCollision(ArrayList<T> objects, T newObject) {
        for (T object : objects) {
            if (newObject != object) {
                if (newObject instanceof Room && object instanceof Room) {
                    if (((Room)newObject).getBounds().intersects(((Room)object).getBounds())) {
                        return true;
                    }
                } else if (newObject instanceof Furniture && object instanceof Furniture) {
                    if (((Furniture)newObject).getBounds().intersects(((Furniture)object).getBounds())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}