/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.dice.active;

import ch.quantasy.iot.dice.simple.SimpleDice;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author reto
 */
public class ActiveDice extends SimpleDice {

    private Timer timer;
    private final ActiveDiceCallback callback;
    private long dicePlayPeriod;
    private boolean playState;

    public ActiveDice(ActiveDiceCallback callback) {
        this.callback = callback;
    }

    public void setDicePlayPeriod(long milliseconds) {
        if (milliseconds < 0) {
            return;
        }
        this.dicePlayPeriod = milliseconds;
        activePlay();
    }

    public long getDicePlayPeriod() {
        return dicePlayPeriod;
    }

    public void setPlayState(boolean playState) {
        if (this.playState == playState) {
            return;
        }
        this.playState = playState;
        this.callback.playStateChanged(this.playState);
        this.activePlay();
    }

    public boolean getPlayState() {
        return playState;
    }

    private synchronized void activePlay() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        if (this.playState == false || this.dicePlayPeriod < 1) {
            return;
        }
        this.timer = new Timer(true);
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                play();
                callback.played(getChosenSide());
            }
        }, 0, this.dicePlayPeriod);
    }

}
