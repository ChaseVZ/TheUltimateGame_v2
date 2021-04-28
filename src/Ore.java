import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Ore extends InteractiveEntity {

    private int health;
    private int CashValue;
    private boolean targeted;
    private BabyYoda targetee;

    public Ore(String id, Point position, List<PImage> images, int imageIndex, int CashValue) {
        super(id, position, images, imageIndex);
        this.health = 4;
        this.CashValue = CashValue;
        this.targeted = false;
        this.targetee = null;
    }

    public boolean isTargeted() {return this.targeted;}
    public void setTargeted() {this.targeted = true;}
    public void setTargetee(BabyYoda awwCute) {this.targetee = awwCute;}
    public BabyYoda getTargetee() {return this.targetee;}

    public boolean destroyed()
    {
        if(health != 0)
            return false;
        return true;
    }

    public void takeHit() {this.health -=1;}

   public void Interact()
   {
        takeHit();
        ItemShop.getItemShop().incrementCurrency(CashValue);
   }
}