public class TerminatorBrainFactory {

    private Point pos = new Point(18, 24);

    public TerminatorBrainFactory() {}

    protected VirtualWorld getV() {return VirtualWorld.getVirtualWorld();}

    public void create(int LEVEL)
    {
        TerminatorBrain b = new TerminatorBrain("terminatorBrain", pos, getV().getImageStore().getImageList("terminatorU"), 0, 1500, 1000, LEVEL);
        getV().getWorld().addEntity(b);
    }

    public TerminatorBrain getMainBossSpawner()
    {
        if(getV().getWorld().isOccupied(pos)) {
            if (!(getV().getWorld().getOccupancyCell(pos) instanceof Quake))
                return (TerminatorBrain) getV().getWorld().getOccupancyCell(pos);
        }
        return null;
    }

}
