package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */

public interface Serde<T> {


    /**
     * @param t, the object to serialize
     * @return serialize, is the process of translating an object state into a string (a format that can be stored)
     */
    abstract String serialize(T t);

    /**
     * this method is the reverse of the serialization process
     * @param s, the string to deserialize
     * @return deserialize, an object ( which is the reverse of the serialization process)
     */
    abstract T deserialize(String s);

    /**
     * @param serialization, function of serialization
     * @param deserialization, function of deserialization
     * @param <T>, object of the Serde
     * @return of, the corresponding Serde
     */

    public static <T> Serde<T> of(Function<T, String> serialization , Function<String, T> deserialization) {

        return new Serde<>() {
            @Override
            public String serialize(T t) {
                return serialization.apply(t);
            }

            @Override
            public T deserialize(String s) {
                return deserialization.apply(s);
            }
        };

    }

    /**
     * @param enumValues, list of all the values of a set
     * @param <T>, a type argument to form a Serde
     * @return oneOf, the corresponding Serde
     * @throws IllegalArgumentException, if the list is empty
     */

    public static <T> Serde<T> oneOf(List<T> enumValues){
        Preconditions.checkArgument(!enumValues.isEmpty());

        Function<T, String> fs = s -> Integer.toString(enumValues.indexOf(s));
        Function<String, T> fd = s -> enumValues.get(Integer.parseInt(s));

        Serde <T> serde = Serde.of(fs, fd);

        return serde;
    }

    /**
     *
     * @param serde, a Serde
     * @param separate, a character of separation
     * @param <T>, a type argument to form a Serde
     * @return listOf, a serde that can (de)serialize lists of (de)serialize values with the given serde
     */

    public static <T> Serde<List<T>> listOf(Serde<T> serde, String separate){

        Function<List<T>, String> fs = c -> {
            StringJoiner joiner = new StringJoiner(separate);
            for(T e : c){
                joiner.add(serde.serialize(e));
            }

            return joiner.toString();
        };
        Function<String, List<T>> cs = f -> {
            if (f.isEmpty()){
                return List.of();
            }

            String[] separator = f.split(Pattern.quote(separate), -1);
            List<T> list = new ArrayList<>();
            for (String s : separator) {
                list.add(serde.deserialize(s));
            }
            return list;
        };

        return of(fs, cs);
    }

    /**
     * @param s, a Serde
     * @param separate, a character of separation
     * @param <T>, a type argument to form a Serde
     * @return bagOf, a serde that can (de)serialize SortedBags of (de)serialize values with the given serde
     */
    public static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> s, String separate){
        Function<SortedBag<T>, String> stringF = sb -> Serde.listOf(s, separate).serialize(sb.toList());
        Function<String, SortedBag<T>> sortedBagF = str -> SortedBag.of(Serde.listOf(s, separate).deserialize(str));
        return of(stringF, sortedBagF);
    }
}