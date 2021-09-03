package test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestClass {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestClass.class);

    public static void scan() {
        Scanner myObj = new Scanner(System.in);
        String userName;

        // Enter username and press Enter
        LOGGER.info("Enter username");
        userName = myObj.nextLine();

        LOGGER.info("Username is: " + userName);
    }

    public static void scanFile(final String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(new File(file.getAbsolutePath()));
        List<String> tokens = new ArrayList<>();
        String token = "";

        LOGGER.info("scanFile ()");

        assertTrue(scanner.hasNext());

        assertEquals("Hello", token = scanner.next());
        tokens.add(token);
        assertEquals("world", token = scanner.next());
        tokens.add(token);

        scanner.close();

        tokens.stream().forEach(S -> LOGGER.info(S));

        Iterator iterator = tokens.iterator();
        LOGGER.info("The ArrayList elements are:");
        while (iterator.hasNext()) {
            LOGGER.info(iterator.next().toString());
        }

        for (int i = 0; i < tokens.size(); i++) {
            LOGGER.info(tokens.get(i));
        }

        int index = 0;
        while (tokens.size() > index) {
            LOGGER.info(tokens.get(index++));
        }

        for (String name : tokens) {
            LOGGER.info(name);
        }
        tokens.forEach(tkn -> LOGGER.info(tkn));
    }

    @Test
    public void mytest() throws FileNotFoundException {

        scanFile("src/test/resources/test.txt");

    }

}
