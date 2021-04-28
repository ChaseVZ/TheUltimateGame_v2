import processing.core.PImage;
import java.util.List;

public abstract class Entity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public Entity(String id, Point position, List<PImage> images, int imageIndex)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = imageIndex;
    }

    //getter functions
    public Point getPosition() {return this.position;}
    public void setPosition(Point p){this.position = p;}

    public List<PImage> getImages() {return this.images;}
    public void setImages(List<PImage> images) {this.images = images;}

    public int getImageIndex() {return this.imageIndex;}
    public void setImageIndex(int idx) {this.imageIndex = idx;}

    public String getId() {return this.id;}
    public void setId(String s) {this.id = s;}

    public VirtualWorld getV() {return VirtualWorld.getVirtualWorld();}

    //other methods
    public PImage getCurrentImage() {return (getImages().get(getImageIndex()));}

    public void SpawnQuake(Point pos)
    {
        Quake quake = new Quake("quake", pos, getV().getImageStore().getImageList("quake"), 7000, 1000);

        getV().getWorld().addEntity(quake);
        quake.scheduleActions(getV().getScheduler(), getV().getWorld(), getV().getImageStore());
    }

    //abstract methods
}
