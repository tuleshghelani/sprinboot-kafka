package com.trim.kafkarest;

import java.util.*;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        String s1 = "swiss";

        Character[] characters = s1.chars()
                .mapToObj(c -> (char) c)
                .toArray(Character[]::new);
        HashMap<Character, Integer> map= new LinkedHashMap<>();
        Arrays.stream(characters)
                .forEach(character -> map.put(character, map.getOrDefault(character, 0)+1));

        Character result = map.entrySet().stream()
                .filter(characterIntegerEntry -> characterIntegerEntry.getValue()==1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        System.out.println("result : "+result);
//        char[] tempCharacters = new char[s1.length()];
//        characters= s1.toCharArray();
//        List<Character> listOfCharacter = new ArrayList<>();
//        listOfCharacter.addAll(s1.toCharArray());
//        listOfCharacter.st

        String s2= "ABC";
        s2="XYZ";

    }
}
