package edu.iastate.linux.git.cyswapper;

/**
 * Created by Kristina on 10/9/2017.
 */

public class Item {
    String name;
    String availability;

    Item()
    {

    }

    Item(String Name, String Availability)
    {
        this.name = Name;
        this.availability = Availability;
    }

    /**
     * returns item name
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     *returns availability of each item
     * @return
     */
    public String getAvailability()
    {
        return availability;
    }
}
