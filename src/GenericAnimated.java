import processing.core.PImage;
import java.util.List;

public class GenericAnimated extends AnimatedEntity {

    public GenericAnimated(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, 0, actionPeriod, animationPeriod);
    }


}
