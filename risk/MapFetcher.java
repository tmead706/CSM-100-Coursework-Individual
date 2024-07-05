package risk;
import java.io.*;


public interface MapFetcher{

    public String[][][] getMapFile(String map) throws IOException;
    public void printMapArray(String[][][] array);


}