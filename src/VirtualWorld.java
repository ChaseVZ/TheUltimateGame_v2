import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import processing.core.*;

/*
VirtualWorld is our main wrapper
It keeps track of data necessary to use Processing for drawing but also keeps track of the necessary
components to make our world run (eventScheduler), the data in our world (WorldModel) and our
current view (think virtual camera) into that world (WorldView)
 */

public final class VirtualWorld
   extends PApplet
{
   private static final int TIMER_ACTION_PERIOD = 100;
   private static final String QUAKE_KEY = "quake";

   private static final int VIEW_WIDTH = 1920;
   private static final int VIEW_HEIGHT = 1080;
   private static final int TILE_WIDTH = 32;
   private static final int TILE_HEIGHT = 32;
   private static final int WORLD_WIDTH_SCALE = 1;
   private static final int WORLD_HEIGHT_SCALE = 1;

   private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   private static final int WORLD_COLS = (VIEW_COLS * WORLD_WIDTH_SCALE) + 1;
   private static final int WORLD_ROWS = (VIEW_ROWS * WORLD_HEIGHT_SCALE) + 1;

   private static final String IMAGE_LIST_FILE_NAME = "imagelist";
   private static final String DEFAULT_IMAGE_NAME = "background_default";
   private static final int DEFAULT_IMAGE_COLOR = 0x808080;

   private static final String LOAD_FILE_INIT = "Initial.sav";
   private static final String LOAD_FILE_CAVE = "CaveWorld.sav";
   private static final String LOAD_FILE_TOWN = "TownWorld.sav";
   private static final String LOAD_FILE_COMBAT = "CombatWorld.sav";
   private static final String LOAD_FILE_REMOVE_STORE = "RemoveStore.sav";
   private static final String LOADING = "BeginGame.sav";
   private static final String END = "End.sav";
   private static final String WIN = "Win.sav";
   private static final String LOAD_FILE_STORE = "Store.sav";

   private static final String FAST_FLAG = "-fast";
   private static final String FASTER_FLAG = "-faster";
   private static final String FASTEST_FLAG = "-fastest";
   private static final double FAST_SCALE = 0.5;
   private static final double FASTER_SCALE = 0.25;
   private static final double FASTEST_SCALE = 0.10;

   private static double timeScale = 1.0;
   private String flag = "mainRight";
   private int LEVEL = 1;

   private ImageStore imageStore;
   private WorldModel world;
   private WorldView view;
   private EventScheduler scheduler;
   private Parsing parsing;
   private MainCharacter main;
   private ItemShop itemShop;
   private boolean StoreActive;
   private boolean Loading;
   private boolean redraw;
   private long next_time;
   private boolean playerDead;

   private static VirtualWorld v1;
   private static OreFactory oreFactory = new OreFactory();
   private static TerminatorBrainFactory terminatorBrainFactory = new TerminatorBrainFactory();
   private static BabyYodaFactory babyYodaFactory = new BabyYodaFactory();
   private static SanHoloFactory sanHoloFactory = new SanHoloFactory();

   private boolean TerminatorBrainFlag = false;
   private boolean SanHoloFlag = false;
   private boolean LEVEL_OVER = false;

   private VirtualWorld(ImageStore imageStore, WorldModel world, WorldView view, EventScheduler scheduler, Parsing parsing, MainCharacter main, ItemShop itemShop){
      this.imageStore = imageStore;
      this.world = world;
      this.view = view;
      this.scheduler = scheduler;
      this.parsing = parsing;
      this.main = main;
      this.StoreActive = false;
      this.Loading = false;
      this.itemShop = itemShop;
      this.playerDead = false;
   }

   public VirtualWorld() {}

   public static VirtualWorld getVirtualWorld(){ return v1; }

   public WorldView getView(){ return v1.view;}
   public WorldModel getWorld(){ return v1.world;}
   public ImageStore getImageStore(){ return v1.imageStore;}
   public EventScheduler getScheduler(){ return v1.scheduler;}
   public Parsing getParsing(){ return v1.parsing;}
   public MainCharacter getMain() {return v1.main;}
   public ItemShop getItemShop() {return v1.itemShop;}
   public OreFactory getOre() {return oreFactory;}
   public TerminatorBrainFactory getTerminator() {return terminatorBrainFactory;}

   public void setLEVEL_OVER() {this.LEVEL_OVER = true;}
   public boolean getLEVEL_OVER() {return this.LEVEL_OVER;}
   public void setStoreActive() {this.StoreActive = true;}
   public void setStoreInactive() {this.StoreActive = false;}
   public void setLoadingOff() {this.Loading = true;}

   public void setTerminatorBrainFlag(boolean flag) {TerminatorBrainFlag = flag;}
   public void setSanHoloFlag(boolean flag) {SanHoloFlag = flag;}

   public void incrementLevel(){
      this.LEVEL += 1;
      getItemShop().NewBabyYodaMoney(LEVEL);
      getItemShop().NewSoloMoney(LEVEL);
      getItemShop().TerminatorBrainMoney(LEVEL);
      getItemShop().TerminatorMoney(LEVEL);

//      if(LEVEL == 3)
//         win();
   }


   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
      // fullScreen();
   }

   public void setup()
   {
      ImageStore imageStore1 = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      WorldModel world1 = new WorldModel(WORLD_ROWS, WORLD_COLS, createDefaultBackground(imageStore1));
      WorldView view1 = new WorldView(VIEW_ROWS, VIEW_COLS, this, world1, TILE_WIDTH, TILE_HEIGHT);
      EventScheduler scheduler1 = new EventScheduler(timeScale);
      Parsing parsing1 = new Parsing(world1);
      MainCharacter m1 = MainCharacter.getMainCharacter();
      ItemShop itemShop = ItemShop.getItemShop();

      v1 = new VirtualWorld(imageStore1, world1, view1, scheduler1, parsing1, m1, itemShop);

      loadImages(IMAGE_LIST_FILE_NAME, getImageStore(), this);
      loadWorld(LOADING, imageStore1, parsing1);
      scheduleActions(getWorld(), getScheduler(), getImageStore());

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
//      m1.setWorldType("Init");
      m1.LoadHearts();
   }

   public void Init()
   {
      getMain().setPosition(new Point (18, 14));
      setLoadingOff();
      loadImages(IMAGE_LIST_FILE_NAME, getImageStore(), this);
      loadWorld(LOAD_FILE_INIT, getImageStore(), getParsing());
      scheduleActions(getWorld(), getScheduler(), getImageStore());
      getMain().scheduleActions(getScheduler(), getWorld(), getImageStore());
//      getMain().setWorldType("Init");
   }

   public void Town()
   {
      loadWorld(LOAD_FILE_TOWN, getImageStore(), getParsing());
      if(!SanHoloFlag)  //if not killed
         sanHoloFactory.create(LEVEL);
      scheduleActions(getWorld(), getScheduler(), getImageStore());
      getMain().scheduleActions(getScheduler(), getWorld(), getImageStore());
//      getMain().setWorldType("Town");
   }

   public void Cave()
   {
      loadWorld(LOAD_FILE_CAVE, getImageStore(), getParsing());
      oreFactory.setCount(0);
      oreFactory.create(LEVEL);
      babyYodaFactory.create(LEVEL);
      scheduleActions(getWorld(), getScheduler(), getImageStore());
      getMain().scheduleActions(getScheduler(), getWorld(), getImageStore());
//      getMain().setWorldType("Cave");
   }

   public void Combat()
   {
      loadWorld(LOAD_FILE_COMBAT, getImageStore(), getParsing());
      if(!TerminatorBrainFlag) {
         terminatorBrainFactory.create(LEVEL);
         terminatorBrainFactory.getMainBossSpawner().setCloneCount(0);
      }
      scheduleActions(getWorld(), getScheduler(), getImageStore());
      getMain().scheduleActions(getScheduler(), getWorld(), getImageStore());
//      getMain().setWorldType("Combat");/
   }

   public void Store()
   {
      setStoreActive();
      loadWorld(LOAD_FILE_STORE, getImageStore(), getParsing());
   }

   public void ExitStore() {
      setStoreInactive();
      loadWorld(LOAD_FILE_REMOVE_STORE, getImageStore(), getParsing());
   }

   public void mainDied()
   {
      loadWorld(END, getImageStore(), getParsing());
      this.playerDead = true;
   }

   public void win()
   {
      setLEVEL_OVER();
      world.removeAllEntities();
      for (Entity e : world.getEntities())
         scheduler.unscheduleAllEvents(e);
      loadWorld(WIN, getImageStore(), getParsing());
   }

   public void mousePressed()
   {
      int x = Math.round(mouseX/TILE_WIDTH);
      int y = Math.round(mouseY/TILE_HEIGHT);
      int THRESHOLD = 2;

      Point p = new Point(x, y);
      if (getWorld().isOccupied(p) && getWorld().getOccupancyCell(p) instanceof InteractiveEntity)
      {
         if (getWorld().withinReach(MainCharacter.getMainCharacter().getPosition(), p, THRESHOLD)) { //if the character is next to the anvil
            if (getWorld().getOccupancyCell(p) instanceof ItemShop) {
               ((InteractiveEntity) getWorld().getOccupancyCell(p)).Interact();
               setStoreActive();
            }
            else
               ((InteractiveEntity) getWorld().getOccupancyCell(p)).Interact();
         }
      }
   }


   public void draw()
   {
      if(redraw)
      {
         textSize(32);
         fill(255, 242, 0);
         text("$" + ItemShop.getItemShop().getCurrency(), 0, 32);
         redraw = false;
      }
      if(!StoreActive) {
         long time = System.currentTimeMillis();
         if (time >= next_time) {
            getScheduler().updateOnTime(time);
            next_time = time + TIMER_ACTION_PERIOD;
         }

         getView().drawViewport();

         textSize(38);
         text(" ", 1088, 96);

         textSize(32);
         fill(255, 242, 0);
         text("$" + ItemShop.getItemShop().getCurrency(), 0, 32);
      }

      if(StoreActive)
      {
         MainCharacter m = MainCharacter.getMainCharacter();
         long time = System.currentTimeMillis();
         if (time >= next_time) {
            getScheduler().updateOnTime(time);
            next_time = time + TIMER_ACTION_PERIOD;
         }
         getView().drawViewport();

         String AddHeartCost = Integer.toString(ItemShop.getItemShop().getAddHeartCost());
         boolean addH = true;
         String IncreaseHeartLvlCost = Integer.toString(ItemShop.getItemShop().getHeartLevelCost());
         boolean IncreH = true;
         String WeaponRangeCost = Integer.toString(ItemShop.getItemShop().getWeaponRangeCost());
         boolean Weapon = true;

          String HeartNum = Integer.toString(m.getH().getNum_hearts());
          if (HeartNum.equals("10")) {
              HeartNum = "MAX";
              addH = false;
          }
          String HeartLvl = Integer.toString(m.getH().getHeart_level());
          if(HeartLvl.equals("3")) {
              HeartLvl = "MAX";
              IncreH = false;
          }
          String Range = Integer.toString(m.getWeaponRange() + 1);
          if (Range.equals("20")) {
              Range = "MAX";
              Weapon = false;
          }

         textSize(36);
         fill(0, 162, 232);
         text("Store", 1448, 224);

         textSize(28);
         fill(0, 0, 0);
         if(addH)
          text("+1 Heart (Q): " + HeartNum, 1384, 256);
         else
             text("+1 Heart (Q)", 1384, 256);
         if(IncreH)
          text("Heart Lvl (F): " + HeartLvl, 1384, 320);
         else
             text("Heart Lvl (F)" , 1384, 320);
         if(Weapon)
          text("Range (R): " + Range, 1384, 384);
         else
             text("Range (R)", 1384, 384);

         textSize(28);
         fill(255, 242, 0);
         if(addH)
            text("$" + AddHeartCost, 1390, 288);
         else
         {
             fill(34, 177, 76);
             text(HeartNum, 1390, 288);
         }

          fill(255, 242, 0);
         if(IncreH)
            text("$" + IncreaseHeartLvlCost, 1390, 352);
         else{
             fill(34, 177, 76);
             text(HeartLvl, 1390, 352);

         }

          fill(255, 242, 0);
         if(Weapon)
            text("$" + WeaponRangeCost, 1390, 416);
         else{
             fill(34, 177, 76);
             text(Range, 1390, 416);
         }

         textSize(32);
         text("$" + ItemShop.getItemShop().getCurrency(), 0, 32);
      }
   }

   public void keyPressed()
   {
         MainCharacter m = MainCharacter.getMainCharacter();
         WorldModel w;
         ImageStore i;

         w = getWorld();
         i = getImageStore();

      if (key == '\n')
      {
         if(!Loading) {
            for (Entity e : getWorld().getEntities()) {
               if (!(e instanceof MainCharacter))
                  getScheduler().unscheduleAllEvents(e);
            }
            getWorld().removeAllEntities();

            Init();
         }

      }

         if(!StoreActive && !playerDead) {

            Entity b;
            Ore ore;

            if(key == ' ') {
               m.Attack(flag);
               redraw = true;
            }


            if(key == 'b') {
               b = getWorld().OreWithinReach(m.getPosition(), flag);
               if (b != null) {
                  ore = (Ore) b;
                  ore.Interact();
                  if (ore.destroyed()) {
                     Point p = ore.getPosition();
                     getWorld().removeEntityAt(ore.getPosition());
                     Quake quake = new Quake(QUAKE_KEY, p, getImageStore().getImageList(QUAKE_KEY), 6000, 1000);
                     getWorld().addEntity(quake);
                     quake.scheduleActions(getScheduler(), getWorld(), getImageStore());
                  }
               }
            }



            if (key == 'w' || key == 'W') {
               if (flag.equals("up"))
                  m.move(w, 0, -1);
               else
                  flag = "up";

               m.setImageIndex(0);
               m.setImages(i.getImageList("mainUpward"));
            }
            if (key == 's' || key == 'S') {
               if (flag.equals("down"))
                  m.move(w, 0, 1);
               else
                  flag = "down";
               m.setImageIndex(0);
               m.setImages(i.getImageList("mainDown"));
            }

            if (key == 'a' || key == 'A') {
               if (flag.equals("left"))
                  m.move(w, -1, 0);
               else
                  flag = "left";
               m.setImageIndex(0);
               m.setImages(i.getImageList("mainLeft"));
            }

            if (key == 'd' || key == 'D') {
               if (flag.equals("right"))
                  m.move(w, 1, 0);
               else
                  flag = "right";
               m.setImageIndex(0);
               m.setImages(i.getImageList("mainRight"));
            }


         }
         if(StoreActive) {
            if (key == 'f' && ItemShop.getItemShop().HeartLevelAvailable()) {
               ItemShop.getItemShop().makePurchase(ItemShop.getItemShop().getHeartLevelCost(), "HeartLevel");
            }
            if (key == 'q' && ItemShop.getItemShop().AddHeartAvailable()) {
               ItemShop.getItemShop().makePurchase(ItemShop.getItemShop().getAddHeartCost(), "AddHeart");
            }
            if (key == 'r' && ItemShop.getItemShop().WeaponRangeAvailable()) {
               ItemShop.getItemShop().WeaponRangePurchase();
            }
            if (key == 'e') {
               ExitStore();
            }
         }


   }


   public static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME,
         imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   public static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void loadWorld(String filename, ImageStore imageStore, Parsing parsing)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         parsing.load(in, imageStore);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void scheduleActions(WorldModel world,
      EventScheduler scheduler, ImageStore imageStore)
   {
      for (Entity entity : world.getEntities()) {
         //Only start actions for entities that include action (not those with just animations)
            if (entity instanceof ActiveEntity)
               ((ActiveEntity) entity).scheduleActions(scheduler, world, imageStore);
      }
   }

   public static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }
}
