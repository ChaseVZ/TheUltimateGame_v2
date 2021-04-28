public class SanHoloFactory {

    public SanHoloFactory(){}

    protected VirtualWorld getV() {return VirtualWorld.getVirtualWorld();}

    public void create(int LEVEL)
    {
        SanHolo sanH = new SanHolo("sanHolo", new Point (30,15), getV().getImageStore().getImageList("sanHoloR"), 3000 / LEVEL, 1100);
        getV().getWorld().addEntity(sanH);
    }
}
