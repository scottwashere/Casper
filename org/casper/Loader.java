package org.casper;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import org.casper.util.Log;

/**
 *
 * @author Colt
 */

public class Loader extends JFrame implements AppletStub {
    
    private static final HashMap<String, String> params = new HashMap<String, String>();
    private static final String baseLink = "http://world2.runescape.com/";
    private static String HTML = null, URL = null;
    public static DefaultListModel logItems;
    private Applet canvas;
    
    public Loader() {
        try {
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            logItems = new DefaultListModel();
            JList logWindow = new JList(logItems);
            JScrollPane scrollPane = new JScrollPane();
            logItems.add(0, "Starting up...");
            scrollPane.setViewportView(logWindow);
            this.setTitle("Casper Loader by s0beit");
            this.setSize(790, 690);
            parseParams();
            downloadFile(URL);
            canvas = (Applet) new URLClassLoader(new URL[] {
                new File("runescape.jar").toURL()}).loadClass(getMainClass()).newInstance();
            canvas.setStub(this);
            canvas.init();
            canvas.start();
            this.add(canvas, BorderLayout.CENTER);
            this.add(scrollPane, BorderLayout.SOUTH);
            this.setResizable(false);
            this.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void appletResize(int width, int height) {
    }

    public final URL getCodeBase() {
        try {
            return new URL(baseLink);
        } catch (Exception e) {
            return null;
        }
    }
    
    public final URL getDocumentBase() {
        try {
            return new URL(baseLink);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public final String getParameter(String name) {
        return params.get(name);
    }
    
    public final AppletContext getAppletContext() {
        return null;
    }
    
    private final String getContent(String link) {
        try {
            URL url = new URL(link);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String myParams = null, inputLine;
            while ((inputLine = in.readLine()) != null) {
                myParams += inputLine;
            }
            in.close();
            return myParams;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private final String getJAR() throws Exception {
        String s = baseLink + ext("archive=", " ", HTML);
        Log.log("Found JAR: " + s);
        return s;
    }
    
    private final String getMainClass() throws Exception {
        String s = ext("code=", ".", HTML);
        Log.log("Found main class: " + s);
        return s;
    }
    
    private void downloadFile(final String url) {
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
            FileOutputStream fos = new FileOutputStream("runescape.jar");
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] data = new byte[1024];
            int x = 0;
            while((x=in.read(data, 0, 1024))>=0) {
                bout.write(data, 0, x);
            }
            bout.close();
            in.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private String ext(String from, String to, String str1) {
        int p = 0;
        p = str1.indexOf(from,p) + from.length();
        return str1.substring(p,str1.indexOf(to,p));
    }
    
    private void parseParams() {
        try {
            HTML = getContent(baseLink);
            Pattern regex = Pattern.compile("<param name=\"?([^\\s]+)\"?\\s+value=\"?([^>]*)\"?>", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher regexMatcher = regex.matcher(HTML);
            while (regexMatcher.find()) {
                if (!params.containsKey(regexMatcher.group(1))) {
                    params.put(remove(regexMatcher.group(1)), remove(regexMatcher.group(2)));
                }
            }
            Log.log("Successfully parsed parameters.");
            URL = getJAR();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
    private String remove(String str) {
        return str.replaceAll("\"", "");
    }

    @Override
    public boolean isActive() {
        return true;
    }
    
}
