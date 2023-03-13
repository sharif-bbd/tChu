package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class Station {

    private final int id;
    private final String name;

    /**
     *Construct the station (unique for each one)
     *
     * @param id an identification of each station
     * @param name of a city or a country
     * @throws IllegalArgumentException if the id >= 0
     */
    public Station(int id, String name){
        this.id =id;
        this.name = name;
        Preconditions.checkArgument(id>=0);

    }

    /**
     * Getter for the identification of a station
     * @return id a number of a station
     */
    public int id(){ return this.id;}

    /**
     * Getter for the name of a station
     * @return name
     */
    public String name(){
        return this.name;
    }

    /**
     * Convert the name into a String
     * @return the String of a name
     */
    @Override
    public String toString(){
        return name;
    }
}