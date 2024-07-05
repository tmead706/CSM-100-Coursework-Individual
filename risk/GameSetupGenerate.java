package risk;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.lang.*;

public class GameSetupGenerate implements GameSetup {
    private MapFetcher mapLoader = null;
    private CardFetcher cardLoader = null;

    @Override
    public String[][][] loadMap(MapFetcher mapLoader) throws IOException{
        this.mapLoader = mapLoader;
        //User - Select map file to load
        String[][][] mapArray = this.mapLoader.getMapFile("riskmap.txt");
        return mapArray;


    }

    @Override
    public String[][] loadCards(CardFetcher cardLoader) throws IOException{
        this.cardLoader = cardLoader;
        //User - Select card file to load
        String[][] cardArray = this.cardLoader.getCardFile("riskcards.txt");
        return cardArray;

    }

    @Override
    public HashMap loadRiskContinents(String[][][] map){
        HashMap<Integer, String[]> riskContinents = new HashMap<>();
        for(int i=0; i <= map[0].length-1; i++){
            riskContinents.put(i, map[0][i]);
        }

        return riskContinents;
    }
    @Override
    public String[][][] allocateCards(String[][] cards){
        //Copy original array
        String[][] copyOfCards = new String[cards.length][];
        //Copy each row of original into copy
        for (int i = 0; i < cards.length; i++) {
            copyOfCards[i] = Arrays.copyOf(cards[i], cards[i].length);
        }
        // Convert the 2D array to a list of lists
        List<List<String>> twoDList = new ArrayList<>();
        twoDList = twoDArrayToList(copyOfCards);
        List<List<String>> player1 = new ArrayList<>();
        List<List<String>> player2 = new ArrayList<>();
        List<List<List<String>>> players = new ArrayList<>();
        for (int i = 0; i < twoDList.size(); i++) {
            player1.add(twoDList.get(i));
            player2.add(twoDList.get(i));
        }
        players.add(player1);
        players.add(player2);

        printthreeDArray(threeDListToArray(players));

        return threeDListToArray(players);

    }

    @Override
    public String[][][] allocateTerritories(String[][] territories){
        //Copy array
        String[][] copyOfTerritories = new String[territories.length][];
        for (int i = 0; i < territories.length; i++) {
            copyOfTerritories[i] = Arrays.copyOf(territories[i], territories[i].length);
        }
        for (String[] rowArray : copyOfTerritories) {
            System.out.println(Arrays.toString(rowArray));
        }
        // Convert the 2D array to a list of lists
        List<List<String>> doomList = new ArrayList<>();
        doomList = twoDArrayToList(copyOfTerritories);

        // Randomize the list
        Collections.shuffle(doomList, new Random());
        // Print the randomized list
        System.out.println("\n" + "Randomized territories: ");
        for (List<String> row : doomList) {
            System.out.println(row);
        }
        List<List<String>> player1 = new ArrayList<>();
        List<List<String>> player2 = new ArrayList<>();
        List<List<List<String>>> players = new ArrayList<>();

        //List<List<String>> playerTerritories = new ArrayList<>();
        for (int i = 0; i < doomList.size(); i++) {
            if (i % 2 == 0) {
                player1.add(doomList.get(i));
            } else {
                player2.add(doomList.get(i));
            }
        }
        players.add(player1);
        players.add(player2);

        //threeDListToArray(players);
        return threeDListToArray(players);

    }

    public static String[][][] threeDListToArray(List<List<List<String>>> threeDList) {
        String[][][] threeDArray = new String[threeDList.size()][][];

        for (int i = 0; i < threeDList.size(); i++) {
            List<List<String>> twoDList = threeDList.get(i);
            threeDArray[i] = new String[twoDList.size()][];

            for (int j = 0; j < twoDList.size(); j++) {
                List<String> rowList = twoDList.get(j);
                threeDArray[i][j] = rowList.toArray(new String[0]);
            }
        }

        return threeDArray;
    }
    @Override
    public void printthreeDArray(String[][][] array){
        for (int i = 0; i < array.length; i++) {
            System.out.println("Player "+(i+Integer.valueOf("1"))+":");
            for (String[] row : array[i]) {
                System.out.println(Arrays.toString(row));
            }
            System.out.println();
        }

    }
    @Override
    public List<List<String>> twoDArrayToList (String[][] array){
        // Convert  2D array to a list of lists
        List<List<String>> twoDList = new ArrayList<>();
        for (String[] row : array) {
            List<String> listRow = new ArrayList<>();
            Collections.addAll(listRow, row);
            twoDList.add(listRow);
        }
        return twoDList;
    }

}