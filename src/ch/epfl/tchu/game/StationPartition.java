package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class StationPartition implements StationConnectivity {

    private int[] stationRepresentative;

    private StationPartition(int[] stationRepresentative) {
        this.stationRepresentative = stationRepresentative.clone();
    }

    @Override
    /**
     *return true if and only if s1 and s2 have the same representative id. The station can take
     * another id than the tab of station.
     * @return connected, connection between the station1 and the station2
     */
    public boolean connected(Station s1, Station s2) {

        return (s1.id() >= stationRepresentative.length || s2.id() >= stationRepresentative.length) ? s1.id() == s2.id()
                : (stationRepresentative[s1.id()] == stationRepresentative[s2.id()]) ? true : false;

    }

    /**
     * Builder of StationPartition
     */
    public static final class Builder {

        private int[] stationRepresentative;


        /**
         * build a set of a station partition which the id is between 0 and stationCount
         * @param stationCount, the number of station in a set
         * @throws IllegalArgumentException, if stationCount is strictly inferior to 0;
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            stationRepresentative = new int[stationCount];

            for (int i = 0; i < stationRepresentative.length; i++) {
                stationRepresentative[i] = i;
            }

        }

        /**
         * join the 2 subsets which contain s1 and s2 and elect a representative station of the subset.
         * @param s1, station that belong to the player
         * @param s2, another station that belong to the player
         * @return connect, the subset of the station s1 and s2 and the builder
         */
        public Builder connect(Station s1, Station s2) {
            stationRepresentative[representative(s2.id())] = representative(s1.id());
            return this;
        }

        /**
         * @return build, the flattened partition of a station
         */
        public StationPartition build() {

            for (int i = 0; i < stationRepresentative.length; i++) {
                stationRepresentative[i] = representative(stationRepresentative[i]);
            }
            return new StationPartition(stationRepresentative);
        }

        /**
         * @param id, the representative id station of the subset
         * @return representative, the representative id station of the subset
         */
        private int representative(int id) {
            int next = id;
            while (stationRepresentative[next] != stationRepresentative[stationRepresentative[next]]) {
                next = stationRepresentative[next];
            }
            return stationRepresentative[next];
        }
    }
}