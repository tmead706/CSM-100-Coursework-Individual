package risk;
import java.io.*;
import java.util.*;



public interface GameSetup{

    String[][][] loadMap(MapFetcher mapLoader) throws IOException;
    String[][] loadCards(CardFetcher cardLoader)throws IOException;
    String[][][] allocateCards(String[][] cards);

    HashMap loadRiskContinents(String[][][] map);//Simplified game has 2-playes only
    String[][][] allocateTerritories(String[][] countries);
    void printthreeDArray(String[][][] array);
    List<List<String>> twoDArrayToList (String[][] array);


}