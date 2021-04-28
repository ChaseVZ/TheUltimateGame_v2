import java.util.Scanner;

public class Parsing {

    private static final int PROPERTY_KEY = 0;

    private static final int OBSTACLE_NUM_PROPERTIES = 4;
    private static final String OBSTACLE_KEY = "obstacle";
    private static final int OBSTACLE_ID = 1;
    private static final int OBSTACLE_COL = 2;
    private static final int OBSTACLE_ROW = 3;

    private static final String INTER_KEY = "interactive";
    private static final String INTER_KEY_ANVIL = "BlackAnvil";
    private static final int INTER_NUM_PROPERTIES = 4;
    private static final int INTER_ID = 1;
    private static final int INTER_COL = 2;
    private static final int INTER_ROW = 3;

    private static final String BGND_KEY = "background";
    private static final int BGND_NUM_PROPERTIES = 4;
    private static final int BGND_ID = 1;
    private static final int BGND_COL = 2;
    private static final int BGND_ROW = 3;

    private static final String MAIN_KEY = "main";
    private static final int MAIN_NUM_PROPERTIES = 6;
    private static final int MAIN_ID = 1;
    private static final int MAIN_COL = 2;
    private static final int MAIN_ROW = 3;
    private static final int MAIN_ACTION_PERIOD = 4;
    private static final int MAIN_ANIMATION_PERIOD = 5;

    private static final String ANIM_KEY = "animated";
    private static final int ANIM_NUM_PROPERTIES = 6;
    private static final int ANIM_ID = 1;
    private static final int ANIM_COL = 2;
    private static final int ANIM_ROW = 3;
    private static final int ANIM_ACTION_PERIOD = 4;
    private static final int ANIM_ANIMATION_PERIOD = 5;

    private static final String PORTAL_KEY = "portal";
    private static final int PORTAL_NUM_PROPERTIES = 10;
    private static final int PORTAL_ID = 1;
    private static final int PORTAL_COL = 2;
    private static final int PORTAL_ROW = 3;
    private static final int PORTAL_DIRECTION_OUT = 4;
    private static final int PORTAL_POS_OUT1 = 5;
    private static final int PORTAL_POS_OUT2 = 6;
    private static final int PORTAL_WORLD_TYPE = 7;
    private static final int PORTAL_ARROW_DIR = 8;
    private static final int PORTAL_NON_ARROW = 9;

    private WorldModel world;


    public Parsing(WorldModel world){
        this.world = world;
    }


    public void load(Scanner in, ImageStore imageStore) {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                if (!processLine(in.nextLine(), imageStore)) {
                    System.err.println(String.format("invalid entry on line %d",
                            lineNumber));
                }
            } catch (NumberFormatException e) {
                System.err.println(String.format("invalid entry on line %d",
                        lineNumber));
            } catch (IllegalArgumentException e) {
                System.err.println(String.format("issue on line %d: %s",
                        lineNumber, e.getMessage()));
            }
            lineNumber++;
        }
    }

    public boolean parseBackground(String[] properties, ImageStore imageStore) {
        if (properties.length == BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]), Integer.parseInt(properties[BGND_ROW]));
            Point transposed = new Point(pt.x + 10, pt.y + 4);
            String id = properties[BGND_ID];

            if(!(id.equals("blackTile")))
                world.setBackground(transposed, new Background(id, imageStore.getImageList(id)));
            else
                world.setBackground(pt, new Background(id, imageStore.getImageList(id)));

        }
        return properties.length == BGND_NUM_PROPERTIES;
    }

    public boolean parseAnimated(String[] properties, ImageStore imageStore) {
        if (properties.length == ANIM_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[ANIM_COL]), Integer.parseInt(properties[ANIM_ROW]));
            Point transposed = new Point(pt.x + 10, pt.y + 4);
            String id = properties[ANIM_ID];

                GenericAnimated f = new GenericAnimated(id, transposed, imageStore.getImageList(properties[ANIM_ID]),
                        Integer.parseInt(properties[ANIM_ACTION_PERIOD]), Integer.parseInt(properties[ANIM_ANIMATION_PERIOD]));
                world.tryAddEntity(f);
        }
        return properties.length == ANIM_NUM_PROPERTIES;
    }

    public boolean parseObstacle(String[] properties, ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[OBSTACLE_COL]), Integer.parseInt(properties[OBSTACLE_ROW]));
            Point transposed = new Point(pt.x + 10, pt.y + 4);

                world.tryAddEntity(new Obstacle(properties[OBSTACLE_ID], transposed, imageStore.getImageList(properties[OBSTACLE_ID])));
        }
        return properties.length == OBSTACLE_NUM_PROPERTIES;
    }

    public boolean parsePortal(String[] properties, ImageStore imageStore)
    {
        if (properties.length == PORTAL_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[PORTAL_COL]), Integer.parseInt(properties[PORTAL_ROW]));
            Point transposed = new Point(pt.x + 10, pt.y + 4);
            Point ptOut = new Point(Integer.parseInt(properties[PORTAL_POS_OUT1]), Integer.parseInt(properties[PORTAL_POS_OUT2]));
            VirtualWorld.getVirtualWorld().getWorld().removeEntityAt(transposed);  //remove Barrier
            String worldType = properties[PORTAL_WORLD_TYPE];

            world.tryAddEntity(
                    new Portal(properties[PORTAL_ID], transposed, imageStore.getImageList(properties[PORTAL_ID]),
                            properties[PORTAL_DIRECTION_OUT], ptOut, worldType, properties[PORTAL_ARROW_DIR], properties[PORTAL_NON_ARROW]));
        }
        return properties.length == PORTAL_NUM_PROPERTIES;

    }


    public boolean parseMain(String[] properties, ImageStore imageStore) {
        if (properties.length == MAIN_NUM_PROPERTIES) {
                Point pt = new Point(Integer.parseInt(properties[MAIN_COL]), Integer.parseInt(properties[MAIN_ROW]));
                Point transposed = new Point(pt.x + 10, pt.y + 4);

                MainCharacter m = MainCharacter.getMainCharacter();
                m.setId(properties[MAIN_ID]);
                m.setPosition(pt);
                m.setImages(imageStore.getImageList("mainRight"));
                m.setResourceLimit(0);
                m.setResourceCount(0);
                m.setActionPeriod(Integer.parseInt(properties[MAIN_ACTION_PERIOD]));
                m.setAnimationPeriod(Integer.parseInt(properties[MAIN_ANIMATION_PERIOD]));
                world.tryAddEntity(m);
        }
        return properties.length == MAIN_NUM_PROPERTIES;
    }

    public boolean parseInteractive(String[] properties, ImageStore imageStore) {
        if (properties.length == INTER_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[INTER_COL]), Integer.parseInt(properties[INTER_ROW]));
            Point transposed = new Point(pt.x + 10, pt.y + 4);

            if(properties[INTER_ID].equals(INTER_KEY_ANVIL))
                ItemShop.getItemShop().setPosition(transposed);
                ItemShop.getItemShop().setImages(imageStore.getImageList(properties[INTER_ID]));
                ItemShop.getItemShop().setImageIndex(0);
                world.tryAddEntity(ItemShop.getItemShop());
        }
        return properties.length == INTER_NUM_PROPERTIES;
    }

    public boolean processLine(String line, ImageStore imageStore) {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[PROPERTY_KEY]) {
                case BGND_KEY:
                    return this.parseBackground(properties, imageStore);
                case OBSTACLE_KEY:
                    return this.parseObstacle(properties, imageStore);
                case MAIN_KEY:
                    return this.parseMain(properties, imageStore);
                case INTER_KEY:
                    return this.parseInteractive(properties, imageStore);
                case ANIM_KEY:
                    return this.parseAnimated(properties, imageStore);
                case PORTAL_KEY:
                    return this.parsePortal(properties, imageStore);
            }
        }
        return false;
    }
}
