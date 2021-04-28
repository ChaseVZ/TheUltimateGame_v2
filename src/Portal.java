import com.sun.tools.javac.Main;
import processing.core.PImage;

import java.util.List;

public class Portal extends Entity {

    private String nextWorldType;
    private String directionOut;
    private Point positionOut;
    private String ArrowDir;
    private String NonArrowTileKey;

    public Portal(String id, Point position, List<PImage> images, String directionOut, Point positionOut, String nextWorldType, String ArrowDir, String NonArrowTileKey)
    {
        super(id, position, images, 0);
        this.directionOut = directionOut;
        this.positionOut = positionOut;
        this.nextWorldType = nextWorldType;
        this.ArrowDir = ArrowDir;
        this.NonArrowTileKey = NonArrowTileKey;
    }

    public void PortalMePapa()
    {
        MainCharacter m = MainCharacter.getMainCharacter();
        Point oldPos = m.getPosition();
        m.setImages(getV().getImageStore().getImageList(directionOut));
        m.setPosition(positionOut);
        getV().getWorld().setOccupancyCell(oldPos, null);
        WorldGen();
    }

    public boolean hasArrow()
    {
        if (this.ArrowDir.equals("none"))
            return false;
        return true;
    }


    private void WorldGen()
    {
        for (Entity e: getV().getWorld().getEntities()) {
                if (!(e instanceof MainCharacter) && !(e.getId().equals("barrier")))
                    getV().getScheduler().unscheduleAllEvents(e);
            }
        getV().getWorld().removeAllEntities();

        if (nextWorldType.equals("Init"))
            getV().Init();
        else if (nextWorldType.equals("Town"))
            getV().Town();
        else if (nextWorldType.equals("Cave"))
            getV().Cave();
        else if (nextWorldType.equals("Combat"))
            getV().Combat();
    }

    public void setArrow()
    {
        if(ArrowDir.equals("Right"))
            getV().getParsing().processLine("background " + "Arrow" + ArrowDir + " " + String.valueOf(getPosition().x - 11) + " " + String.valueOf(getPosition().y - 4), getV().getImageStore());
        else if(ArrowDir.equals("Left"))
            getV().getParsing().processLine("background " + "Arrow" + ArrowDir + " " + String.valueOf(getPosition().x - 9) + " " + String.valueOf(getPosition().y - 4), getV().getImageStore());
        else if(ArrowDir.equals("Down"))
            getV().getParsing().processLine("background " + "Arrow" + ArrowDir + " " + String.valueOf(getPosition().x - 10) + " " + String.valueOf(getPosition().y - 5), getV().getImageStore());
        else if(ArrowDir.equals("Up"))
            getV().getParsing().processLine("background " + "Arrow" + ArrowDir + " " + String.valueOf(getPosition().x - 10) + " " + String.valueOf(getPosition().y - 3), getV().getImageStore());
    }

    public void unSetArrow()
    {
        if(ArrowDir.equals("Right"))
            getV().getParsing().processLine("background " + NonArrowTileKey + " " + String.valueOf(getPosition().x - 11) + " " + String.valueOf(getPosition().y - 4), getV().getImageStore());
        else if(ArrowDir.equals("Left"))
            getV().getParsing().processLine("background " + NonArrowTileKey + " " + String.valueOf(getPosition().x - 9) + " " + String.valueOf(getPosition().y - 4), getV().getImageStore());
        else if(ArrowDir.equals("Down"))
            getV().getParsing().processLine("background " + NonArrowTileKey + " " + String.valueOf(getPosition().x - 10) + " " + String.valueOf(getPosition().y - 5), getV().getImageStore());
        else if(ArrowDir.equals("Up"))
            getV().getParsing().processLine("background " + NonArrowTileKey + " " + String.valueOf(getPosition().x - 10) + " " + String.valueOf(getPosition().y - 3), getV().getImageStore());
    }
}
