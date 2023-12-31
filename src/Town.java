/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */


public class Town {
    // private finals
    private final String[] ALL_TREASURES = {"a crown", "a trophy", "a gem", "dust"};


    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private String treasure;
    private boolean treasureFound;
    private boolean dug;


    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        this.dug = false;
        treasure = ALL_TREASURES[(int) (Math.random() * 4)];
        treasureFound = false;


        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;


        printMessage = "";


        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }


    public String getLatestNews() {
        return printMessage;
    }


    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
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
            if (!TreasureHunter.easyMode) {
                if (checkItemBreak()) {
                    hunter.removeItemFromKit(item);
                    printMessage += "\nUnfortunately, you lost your " + item + ".";
                }
            }
            return true;
        }


        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }


    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
        printMessage = "You left the shop";
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
            if (!hunter.hasItemInKit("sword")) {
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
                int goldDiff = (int) (Math.random() * 10) + 1;
                if (Math.random() > noTroubleChance) {
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                    printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                    hunter.changeGold(goldDiff);
                } else {
                    printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                    printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                    System.out.println(getLatestNews());
                    hunter.changeGold(-goldDiff);


                }
            } else {
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\n" + Colors.RESET ;
                int goldDiff = (int) (Math.random() * 10) + 1;
                printMessage += Colors.WHITE + "Bro picked the wrong guy to fight.\n" + Colors.RESET;
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                hunter.changeGold(goldDiff);
            }
        }
    }


    public String toString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }


    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        int rnd = (int) (Math.random() * 6) + 1;
        if (rnd == 1) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd == 2) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd == 3) {
            return new Terrain("Plains", "Horse");
        } else if (rnd == 4) {
            return new Terrain("Desert", "Water");
        } else if (rnd == 5){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
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


    public void findTreasure() {
        boolean treasureIsDust = treasure.equals("dust");
        boolean hunterAlreadyFound = false;
        if (treasureFound) {
            System.out.println("You have already searched this town.");
        } else {
            System.out.println("You search for the treasure...");
            System.out.println("You found: " + treasure);
            treasureFound = true;
            if (treasureIsDust) {
                System.out.println("No shiny treasure this time :(");
            } else {
                for (String i : hunter.getTreasureList()) {
                    if (i != null) {
                        if (i.equals(treasure)) {
                            hunterAlreadyFound = true;
                        }
                    }
                }
                if (!hunterAlreadyFound) {
                    hunter.updateTreasureList(treasure);
                } else {
                    System.out.println("You already have this treasure!");
                }


            }
            allTreasuresFound();


        }
    }


    public void allTreasuresFound() {
        if (hunter.getTreasureList()[2] != null) {
            System.out.println("Congratulations, you have found the last of the three treasures, you win!");
            System.exit(0);
        }
    }


    public void digForGold() {
        if (!dug) {
            if (hunter.hasItemInKit("shovel")) {
                if (Math.random() < 0.5) {
                    System.out.println("You dug but only found dirt");
                } else {
                    int gold = (int) (Math.random() * 20) + 1;
                    System.out.println("You dug up " + gold + " gold!");
                    hunter.changeGold(gold);
                }
                dug = true;
            } else {
                System.out.println("You can't dig for gold without a shovel!");
            }
        } else {
            System.out.println("You already dug for gold in this town.");
        }
    }
}


