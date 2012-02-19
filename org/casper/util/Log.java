package org.casper.util;

import org.casper.Loader;
/**
 *
 * @author Colt
 */

public class Log {
    
    public static void log(String s) {
        Loader.logItems.add(Loader.logItems.size(),s);
    }
    
}
