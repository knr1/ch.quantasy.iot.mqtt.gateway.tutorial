/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.dice.active;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
public class ActiveDiceTest {

    public ActiveDiceTest() {
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
     * Test of setDicePlayPeriod method, of class ActiveDice.
     */
    @Test
    public void testSetDicePlayPeriod0() {
        System.out.println("setDicePlayPeriod");
        long milliseconds = 0L;
        ActiveDice instance = new ActiveDice(new ActiveDiceCallback() {
            @Override
            public void played(int chosenSide) {
                fail("Callback called without a reason");
            }

            @Override
            public void playStateChanged(boolean playState) {
                fail("Callback called without a reason");
            }
        });
        instance.setDicePlayPeriod(milliseconds);
    }

    /**
     * Test of setDicePlayPeriod method, of class ActiveDice.
     */
    @Test
    public void testSetDicePlayPeriod1() {
        System.out.println("setDicePlayPeriod");
        long milliseconds = 1L;
        ActiveDice instance = new ActiveDice(new ActiveDiceCallback() {
            @Override
            public void played(int chosenSide) {
                fail("Callback called without a reason");
            }

            @Override
            public void playStateChanged(boolean playState) {
                //Not tested here
            }
        });
        instance.setDicePlayPeriod(milliseconds);
    }

    /**
     * Test of getDicePlayPeriod method, of class ActiveDice.
     */
    @Test
    public void testGetDicePlayPeriod0() {
        System.out.println("getDicePlayPeriod");
        long milliseconds = 0L;
        ActiveDice instance = new ActiveDice(new ActiveDiceCallback() {
            @Override
            public void played(int chosenSide) {
                fail("Callback called without a reason");
            }

            @Override
            public void playStateChanged(boolean playState) {
                fail("Callback called without a reason");
            }
        });
        long expResult = 0L;
        long result = instance.getDicePlayPeriod();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDicePlayPeriod method, of class ActiveDice.
     */
    @Test
    public void testGetDicePlayPeriod1() {
        System.out.println("getDicePlayPeriod");
        long milliseconds = 1L;
        ActiveDice instance = new ActiveDice(new ActiveDiceCallback() {
            @Override
            public void played(int chosenSide) {
                fail("Callback called without a reason");
            }

            @Override
            public void playStateChanged(boolean playState) {
            }
        });
        instance.setDicePlayPeriod(milliseconds);
        long expResult = 1L;
        long result = instance.getDicePlayPeriod();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDicePlayPeriod method, of class ActiveDice.
     */
    @Test
    public void testSetGetSetGetDicePlayPeriod1100() {
        System.out.println("getAndSetDicePlayPeriod from 0 to 1 to 0 again");
        long milliseconds = 1L;
        ActiveDice instance = new ActiveDice(new ActiveDiceCallback() {
            @Override
            public void played(int chosenSide) {
                fail("Callback called without a reason");
            }

            @Override
            public void playStateChanged(boolean playState) {
                fail("Callback called without a reason");
            }
        });
        long expResult = 1L;
        instance.setDicePlayPeriod(milliseconds);
        long result = instance.getDicePlayPeriod();
        assertEquals(expResult, result);
        milliseconds = 0L;
        expResult = 0L;
        instance.setDicePlayPeriod(milliseconds);
        result = instance.getDicePlayPeriod();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPlayState method, of class ActiveDice.
     */
    @Test
    public void testSetPlayStateFalseToFalse() {
        System.out.println("setPlayStateFalseToFalse");
        boolean playState = false;
        ActiveDice instance = new ActiveDice(new ActiveDiceCallback() {
            @Override
            public void played(int chosenSide) {
                fail("Callback called without a reason");
            }

            @Override
            public void playStateChanged(boolean playState) {
                fail("Callback called without a reason");
            }
        });
        instance.setPlayState(false);
    }

    /**
     * Test of setPlayState method, of class ActiveDice.
     *
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSetPlayStateFalseToTrue() throws InterruptedException {
        System.out.println("setPlayStateFalseToTrue");
        boolean playState = true;
        final boolean[] result = new boolean[1];
        ActiveDice instance = new ActiveDice(new ActiveDiceCallback() {
            @Override
            public void played(int chosenSide) {
                fail("Callback called without a reason");
            }

            @Override
            public void playStateChanged(boolean playState) {
                result[0] = playState;
                synchronized (this) {
                    this.notifyAll();
                }
            }
        });
        instance.setPlayState(playState);
        synchronized (this) {
            this.wait(1000);
        }
        final boolean expResult = true;
        assertEquals(expResult, result[0]);
    }

    /**
     * Test of getPlayState method, of class ActiveDice.
     */
    @Test
    public void testGetPlayState() {
        System.out.println("getPlayState");
        boolean playState = true;
        ActiveDice instance = new ActiveDice(new ActiveDiceCallback() {
            @Override
            public void played(int chosenSide) {
                fail("Callback called without a reason");
            }

            @Override
            public void playStateChanged(boolean playState) {
            }
        });
        boolean expResult = playState;
        instance.setPlayState(playState);
        boolean result = instance.getPlayState();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPlayState method, of class ActiveDice.
     */
    @Test
    public void testSetPlayStatePeriod1() {
        System.out.println("setPlayState false with period >0");
        boolean playState = false;
        ActiveDice instance = new ActiveDice(new ActiveDiceCallback() {
            @Override
            public void played(int chosenSide) {
                //Not tested here
            }

            @Override
            public void playStateChanged(boolean playState) {
            }
        });
        instance.setDicePlayPeriod(1);
        instance.setPlayState(true);

    }

    /**
     * Test of setPlayState method, of class ActiveDice.
     *
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSetPlayStatePeriod100True() throws InterruptedException {
        System.out.println("setPlayState true with period >10");
        boolean playState = true;
        int playPeriod=10;
        final List<Integer> playList = new LinkedList<Integer>();
        ActiveDice instance = new ActiveDice(new ActiveDiceCallback() {
            @Override
            public void played(int chosenSide) {
                playList.add(chosenSide);
            }

            @Override
            public void playStateChanged(boolean playState) {
            }
        });
        instance.setDicePlayPeriod(playPeriod);
        instance.setPlayState(playState);
        Thread.sleep(1000);
        instance.setPlayState(!playState);
        System.out.println(playList.size());
        assertTrue(playList.size()<102 && playList.size()>98);
    }
}
