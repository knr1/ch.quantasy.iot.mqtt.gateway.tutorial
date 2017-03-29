/*
 *   "TimerMqWay"
 *
 *    TimerMqWay(tm): A gateway to provide a universal timer ability.
 *
 *    Copyright (c) 2016 Bern University of Applied Sciences (BFH),
 *    Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *    Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *    Licensed under Dual License consisting of:
 *    1. GNU Affero General Public License (AGPL) v3
 *    and
 *    2. Commercial license
 *
 *
 *    1. This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *    2. Licensees holding valid commercial licenses for TiMqWay may use this file in
 *     accordance with the commercial license agreement provided with the
 *     Software or, alternatively, in accordance with the terms contained in
 *     a written agreement between you and Bern University of Applied Sciences (BFH),
 *     Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *     Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *     For further information contact <e-mail: reto.koenig@bfh.ch>
 *
 *
 */
package ch.quantasy.iot.gateway.servant.timerDice;

/**
 *
 * @author reto
 */
public class TimerDiceConfiguration {

    private Long epoch;
    private Integer first;
    private Integer interval;
    private Integer last;

    /**
     *
     * @return First time in ms relative to {@link #getEpoch()} when the ticker
     * begins ticking
     */
    public Integer getFirst() {
        return first;
    }

    /**
     *
     * @param epoch Reference time. The value represents ms since Unix epoc.
     * null is considered as 'now'. Negative values are ignored (Old value
     * persists). This is used to indicate which time-reference is used. If the
     * client wants to start 20 seconds from Unix epoch-'client-now' it
     * indicates this by sending its current time.
     */
    public void setEpoch(Long epoch) {
        if (epoch < 0) {
            return;
        }
        this.epoch = epoch;
    }

    /**
     *
     * @return Epoch of this Ticker
     */
    public Long getEpoch() {
        return epoch;
    }

    /**
     *
     * @param first First time in ms relative to
     * {@link #setEpoch(java.lang.Long)} when the ticker begins ticking.
     */
    public void setFirst(Integer first) {
        if (first < 0) {
            return;
        }
        this.first = first;
    }


    /**
     *
     * @return
     */
    public Integer getLast() {
        return last;
    }

    /**
     *
     * @param last Time in ms relative to {@link #setEpoch(java.lang.Long)} when
     * the ticker terminates.
     */
    public void setLast(Integer last) {
        if (last < 0) {
            return;
        }
        this.last = last;
    }

    public Integer getInterval() {
        return interval;
    }

    /**
     *
     * @param interval Interval in ms between two ticks. null is considered as
     * no repetition. Negative values are ignored (old value persists).
     */
    public void setInterval(Integer interval) {
        if (interval < 0) {
            return;
        }
        this.interval = interval;
    }

    private TimerDiceConfiguration() {
    }

    /**
     *
     * @param id Identifier of the ticker to be configured
     * @param epoch {@link #setEpoch(java.lang.Long) }
     * @param first {@link #setFirst(java.lang.Long) }
     * @param interval {@link #setInterval(java.lang.Long) }
     * @param last {@link #setLast(java.lang.Long) }
     */
    public TimerDiceConfiguration(Long epoch, Integer first, Integer interval, Integer last) {
        this.epoch = epoch;
        this.first = first;
        this.interval = interval;
        this.last = last;
    }

    public TimerDiceConfiguration(TimerDiceConfiguration configuration) {
        this(configuration.epoch, configuration.first, configuration.interval, configuration.last);
    }

    @Override
    public String toString() {
        return "DeviceTickerConfiguration{" + "epoch=" + epoch + ", first=" + first + ", interval=" + interval + ", last=" + last + '}';
    }

    public boolean isFinished() {
        if (getLast() == null) {
            if (getInterval() == null || getInterval() < 1) {
                return true;
            }
            return false;
        }
        return getLastInMillisFromNow() <= 0;
    }

    public Long getLastInMillisFromNow() {
        if (getLast() == null) {
            return null;
        }
        return getEpochDelta() + getLast();
    }

    public Long getFirstInMillisFromNow() {
        if (getFirst() == null) {
            return null;
        }
        return getEpochDelta() + getFirst();
    }

    public boolean isFirstReached() {
        if (getFirst() == null) {
            return true;
        }
        return getFirstInMillisFromNow() <= 0;
    }

    public long getEpochDelta() {
        if (getEpoch() == null) {
            return 0;
        }
        return getEpoch() - System.currentTimeMillis();
    }

}
