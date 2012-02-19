package org.casper;

/**
 *
 * @author Colt
 */

public class Boot {
    
    public static void main(String[] args) {
        try {
            new Loader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
