package risk;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.*;
import java.util.*;
import java.nio.*;


public class Risk {

    public static void main(String[] args) throws Exception {

        // get the bean factory
        BeanFactory factory = getBeanFactory();
        MessageRenderer mr = (MessageRenderer) factory.getBean("renderer");
        mr.render();
        MapFetcher mf = (MapFetcher) factory.getBean("mapMaker");

        CardFetcher cf = (CardFetcher) factory.getBean("cardMaker");

        GameSetup gameSetter = new GameSetupGenerate();
        String[][][] gameMap = gameSetter.loadMap(mf);
        mf.printMapArray(gameMap);
        String[][] gameCards = gameSetter.loadCards(cf);
        cf.printCardArray(gameCards);
        gameSetter.loadRiskContinents(gameMap);
        gameSetter.printthreeDArray(gameSetter.allocateTerritories(gameMap[1]));
        gameSetter.allocateCards(gameCards);


    }

    private static BeanFactory getBeanFactory() throws Exception {
        // create a bean factory from dixmlcons.xml
        BeanFactory factory = new ClassPathXmlApplicationContext("risk_beans.xml");
        return factory;
    }

}