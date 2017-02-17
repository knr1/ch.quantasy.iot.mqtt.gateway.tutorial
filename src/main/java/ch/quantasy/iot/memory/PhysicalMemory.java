/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.memory;

/**
 *
 * @author reto
 */
public class PhysicalMemory {
    private long total;
    private long free;

    private PhysicalMemory() {
    }

    public PhysicalMemory(long total, long free) {
        this.total = total;
        this.free = free;
    }

    public long getFree() {
        return free;
    }

    public long getTotal() {
        return total;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.total ^ (this.total >>> 32));
        hash = 59 * hash + (int) (this.free ^ (this.free >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PhysicalMemory other = (PhysicalMemory) obj;
        if (this.total != other.total) {
            return false;
        }
        if (this.free != other.free) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PhysicalMemory{" + "total=" + total + ", free=" + free + '}';
    }

   

   
    
    
    
    
}
