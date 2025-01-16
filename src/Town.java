/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean dug;
    private boolean easy;
    private boolean townSearch;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness, boolean e) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        townSearch = false;
        dug = false;
        easy = e;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        return printMessage;
    }

    public boolean isDug() { return dug; }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        dug = false;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (!easy) {
                if (checkItemBreak()) {
                    hunter.removeItemFromKit(item);
                    printMessage += "\nUnfortunately, you lost your " + item;
                }
            }
            return true;
        } else {
            printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
            return false;
        }
    }


    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\n" + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (hunter.hasItemInKit("sword")){
                printMessage += Colors.RED + "Wait, is that a SWORD?! Never mind! You can take my gold!";
                printMessage += "\nYou won the \"brawl\" and receive " + Colors.RESET + Colors.YELLOW + goldDiff + Colors.RESET + Colors.RED + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "Oof! Umph! Ow!\n" + Colors.RED;
                if (Math.random() > noTroubleChance) {
                    printMessage += Colors.RED + "Okay, stranger! You proved yer mettle. Here, take my gold.";
                    printMessage += "\nYou won the brawl and receive " + Colors.RESET + Colors.YELLOW + goldDiff + Colors.RESET + Colors.RED + " gold." + Colors.RESET;
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                    printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold." + Colors.RESET;
                    hunter.changeGold(-goldDiff);
                }
            }
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    public void dig(){
        if (!isDug()) {
            if (hunter.hasItemInKit("shovel")) {
                int randomNum = (int) (Math.random() * 2) + 1;
                if (randomNum == 1) {
                    int goldDug = (int)(Math.random()*20)+1;
                    System.out.println("You dug up " +goldDug+" gold!");
                    hunter.changeGold(goldDug);
                } else {
                    System.out.println("You dug but only found dirt");
                }
                dug = true;
            } else {
                System.out.println("You can't dig for gold without a shovel!");
            }
        } else {
            System.out.println("You already dug for gold in this town.");
        }
    }

    public String getTreasure() {
        if (!townSearch) {
            String treasureItem;
            int rand = (int) (Math.random() * 4) + 1;
            townSearch = true;
            if (rand == 1) {
                treasureItem = "a crown";
            } else if (rand == 2) {
                treasureItem = "a trophy";
            } else if (rand == 3){
                treasureItem = "a gem";
            } else {
                treasureItem = "dust";
                return "You found " + treasureItem + "...\n";
            }
            if (!hasTreasure(treasureItem)){
                addTreasure(treasureItem);
                return "You found " + treasureItem + "!\n";
            } else {
                return "You have already collected this treasure\n";
            }
        } else {
            return "You have already searched this town\n";
        }
    }

    private void addTreasure(String treasure1) {
        if (!hasTreasure(treasure1)) {
            int idx = emptyPos();
            hunter.getTreasureList()[idx] = treasure1;
        }
    }

    private boolean hasTreasure(String treasure) {
        for (String item : hunter.getTreasureList()) {
            if (treasure.equals(item)) {
                return true;
            }
        }
        return false;
    }

    private int emptyPos() {
        for (int i = 0; i < hunter.getTreasureList().length; i++) {
            if (hunter.getTreasureList()[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public String treasureinfoString() {
        String str = "Treasures found: ";
        if (!treasureIsEmpty()) {
            str += getInventory();
        }
        return str;
    }

    private boolean treasureIsEmpty() {
        for (String string : hunter.getTreasureList()) {
            if (string != null) {
                return false;
            }
        }
        return true;
    }

    public String getInventory() {
        String print = "";
        String space = " ";
        for (String item :  hunter.getTreasureList()) {
            if (item != null) {
                print += Colors.BLUE + item + Colors.RESET + space;
            }
        }
        return print;
    }

//    public boolean completedTreasures() {
//        hunter.getTreasureList();
//        boolean check1 = false;
//        boolean check2 = false;
//        boolean check3 = false;
//        for (String item :  hunter.getTreasureList()) {
//            if (item.equals("a trophy")) {
//            check1 = true;
//            }
//            if (item.equals("a crown")) {
//                check1 = true;
//            }
//            if (item.equals("a gem")) {
//                check1 = true;
//            }
//        }
//        if (check1 && check2 && check3) {
//            return true;
//        }
//        return false;
//    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        int rnd = (int) (Math.random()*6)+1;
        if (rnd == 1) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd == 2) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd == 3) {
            return new Terrain("Plains", "Horse");
        } else if (rnd == 4) {
            return new Terrain("Desert", "Water");
        }  else if (rnd == 5) {
            return new Terrain("Marsh", "Boots");
        } else {
            return new Terrain("Jungle", "Machete");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
}