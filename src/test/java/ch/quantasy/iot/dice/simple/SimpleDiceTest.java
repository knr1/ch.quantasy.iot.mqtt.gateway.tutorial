/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.dice.simple;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author reto
 */
public class SimpleDiceTest {
    
    public SimpleDiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getAmountOfSides method, of class SimpleDice.
     */
    @Test
    public void testGetAmountOfSides() {
        System.out.println("getAmountOfSides");
        SimpleDice instance = new SimpleDice();
        int expResult = 6;
        int result = instance.getAmountOfSides();
        assertEquals(expResult, result);
    }

    /**
     * Test of play method, of class SimpleDice.
     */
    @Test
    public void testPlay() {
        System.out.println("play");
        SimpleDice instance = new SimpleDice();
        instance.play();
    }

    /**
     * Test of getChosenSide method, of class SimpleDice.
     */
    @Test
    public void testGetChosenSide() {
        System.out.println("getChosenSide");
        SimpleDice instance = new SimpleDice();
        Set<Integer> expResultSet = new HashSet<>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6}));
        int result = instance.getChosenSide();
        assertTrue(expResultSet.contains(result));
    }

    /**
     * Test of getChosenSide method multiple times, of class SimpleDice.
     */
    @Test
    public void testGetChosenSideMultipleTimes() {
        System.out.println("getChosenSideMultipleTimes");
        SimpleDice instance = new SimpleDice();
        int result = instance.getChosenSide();
        for (int i = 0; i < 10; i++) {
            assertEquals(result, instance.getChosenSide());
        }
    }

    /**
     * Test if play - getChosenSide returns all possible dice-faces.
     */
    @Test
    public void testPlayAndGetAllChosenSides() {
        System.out.println("play, getChosenSide ... Caution, this test is probabilistic and fails with a probability of 1/1000 even if the program runs perfect.");
        SimpleDice instance = new SimpleDice();
        Set<Integer> expResultSet = new HashSet<>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6}));
        for (int i = 0; i < 6000; i++) {
            expResultSet.remove(instance.getChosenSide());
            instance.play();
        }
        assertTrue(expResultSet.isEmpty());
    }
}
