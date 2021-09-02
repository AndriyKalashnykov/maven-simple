package test;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestClass {

    protected static final Logger LOGGER = LogManager.getLogger();


    public static void scan() {
        Scanner myObj = new Scanner(System.in);
        String userName;

        // Enter username and press Enter
        System.out.println("Enter username");
        userName = myObj.nextLine();

        System.out.println("Username is: " + userName);
    }

    public static void scanFile(final String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(new File(file.getAbsolutePath()));
        List<String> tokens = new ArrayList<>();
        String token = "";

        LOGGER.debug("scanFile ()");

        assertTrue(scanner.hasNext());

        assertEquals("Hello", token = scanner.next());
        tokens.add(token);
        assertEquals("world", token = scanner.next());
        tokens.add(token);

        scanner.close();

        tokens.stream().forEach(S -> System.out.println(S));

        Iterator iterator = tokens.iterator();
        LOGGER.info("The ArrayList elements are:");
        while (iterator.hasNext()) {
            LOGGER.info(iterator.next());
        }

        for(int i = 0; i < tokens.size(); i++)
        {
            LOGGER.info(tokens.get(i));
        }

        int index = 0;
        while (tokens.size() > index)
        {
            LOGGER.info(tokens.get(index++));
        }

        for(String name : tokens)
        {
            LOGGER.info(name);
        }
        tokens.forEach(tkn ->  LOGGER.info(tkn));
    }

    @Test
    public void mytest() throws FileNotFoundException {

        scanFile("src/test/resources/test.txt");

    }

}
