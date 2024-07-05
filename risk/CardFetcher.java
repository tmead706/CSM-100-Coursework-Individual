package risk;
import java.io.*;


public interface CardFetcher{

    String[][] getCardFile(String riskcards) throws IOException;
    void printCardArray(String[][] array);


}