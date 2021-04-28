import processing.core.PImage;

import java.util.List;

public class TerminatorBrain extends Moveable {

    private int LEVEL;
    private static int CloneCount = 0;

    public TerminatorBrain(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod, int LEVEL) {
        super(id, position, images, imageIndex, actionPeriod, animationPeriod);
        this.LEVEL = LEVEL;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        while (CloneCount <= (LEVEL)) {

            TerminatorClone b = new TerminatorClone("terminator", new Point(17, 24), getV().getImageStore().getImageList("terminatorU"), 2600 - (LEVEL*400), 800);
            getV().getWorld().addEntity(b);
            b.scheduleActions(getV().getScheduler(), getV().getWorld(), getV().getImageStore());
            CloneCount++;
        }


//        String suffix = getDirectionToTarget(getV().getMain().getPosition(), getPosition());
//        setImageIndex(0);
//        setImages(getV().getImageStore().getImageList("terminator" + suffix));
    }

    public void decreaseCloneCount() {this.CloneCount -= 1;}
    public void setCloneCount(int i) {this.CloneCount = i;}


}
