package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class Trail {

    private final Station stationStart;
    private final Station stationEnd;
    private final List<Route> routes;


    private Trail(Station stationStart, Station stationEnd, List<Route> routes){
        this.stationStart = stationStart;
        this.stationEnd = stationEnd;
        this.routes = routes;


    }

    /**
     * return the longest trail composed of the routes.
     * If the list of trails is empty, it return a trail of length 0 and the stations are both null.
     *
     * @param routes, list of all the routes of a player
     * @return longest, the maximal length of a trail
     */

    public static Trail longest(List<Route> routes){
        List<Trail> oneRoadTrails = new ArrayList<>();
        for(Route r : routes){
            oneRoadTrails.add(new Trail(r.station1(), r.station2(), List.of(r)));
            oneRoadTrails.add(new Trail(r.station2(), r.station1(), List.of(r)));
        }

        List<Trail> longerTrails = new ArrayList<>(oneRoadTrails);

        if(routes.isEmpty()){
            return new Trail(null, null, routes);

        }else{
            while (!oneRoadTrails.isEmpty()){
                List<Trail> timeTrail = new ArrayList<>();

                for (Trail t : oneRoadTrails){
                    List<Route> possibleRoad = new ArrayList<>();

                    for (Route r : routes){

                        if((t.station2().equals(r.station1()) || t.station2().equals(r.station2())) && (!t.routes.contains(r))){
                            possibleRoad.add(r);
                        }
                    }

                    for(Route prolongation:possibleRoad){
                        List<Route> route = new ArrayList<>();
                        route.addAll(t.routes);
                        route.add(prolongation);
                        Trail newTrail = new Trail(t.station1(), prolongation.stationOpposite(t.station2()), route);
                        longerTrails.add(newTrail);
                        timeTrail.add(newTrail);

                    }
                }
                oneRoadTrails = timeTrail;
            }
        }

        Trail longest = longerTrails.get(0);
        for (Trail l : longerTrails){
            if(l.length() > longest.length()){
                longest = l;
            }
        }
        return longest;
    }

    /**
     *
     * return the length of the trail (the sum of the routes)
     * @return length of the trail
     */
    public int length(){
        int length = 0;
        for(Route listRoute : routes){
            length += listRoute.length();
        }
        return length;
    }

    /**
     * return the first station of the trail or null if and only if the trail is the length of 0
     * @return station1 , the first station of the trail
     */
    public Station station1(){
        if(routes.isEmpty()){
            return null;
        }
        return stationStart;
    }

    /**
     * return the last station of the trail or null if and only if the trail is the length 0
     * @return station2, the last station of the trail
     */
    public Station station2(){
        if(routes.isEmpty()){
            return null;
        }
        return stationEnd;
    }


    /**
     *return the textual representation of a trail which contains the first, last and all the intermediaries stations
     * @return toString, the textual representation of a trail
     */
    @Override
    public String toString(){
        List<String> allStation2 = new ArrayList<>();
        String text;
        Station secondStation = routes.get(0).stationOpposite(this.station1());
        allStation2.add(secondStation.name());

        for(int i = 1; i < routes.size(); ++i){
            if(secondStation.equals(routes.get(i).station1())){
                secondStation = routes.get(i).station2();
                allStation2.add(secondStation.name());
            }else if (secondStation.equals(routes.get(i).station2())){
                secondStation = routes.get(i).station1();
                allStation2.add(secondStation.name());
            }
        }

        text = String.format("%s - %s", stationStart.name(), String.join(" - ", allStation2));

        return text + " (" + length() + ")";
    }
}