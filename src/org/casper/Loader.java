package org.casper;

import javax.swing.plaf.basic.BasicLookAndFeel;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.event.ActionEvent;
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
    private static String HTML = null, URL = null, thisVersion = "1.1";
    public static DefaultListModel logItems;
    private File client = new File("runescape.jar");
    private static boolean debug = false;
    private JTabbedPane pane;
    
    public Loader() {
        try {
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            JPanel toolsPanel = new JPanel();
            pane = new JTabbedPane();
            logItems = new DefaultListModel();
            JList logWindow = new JList(logItems);
            JScrollPane scrollPane = new JScrollPane();
            logItems.add(0, "Starting up...");
            scrollPane.setViewportView(logWindow);
            this.setTitle("Casper Client by Anonymous");
            parseParams();
            if (needsUpdating() || !client.exists()) {
                update();
            }
            addClient();
            pane.addTab("Tools", toolsPanel);
            scrollPane.setSize(50, this.getWidth());
            this.getContentPane().add(pane, BorderLayout.CENTER);
            if (debug) {
                this.setSize(770, 678);
                this.add(scrollPane, BorderLayout.SOUTH);
            } else {
                this.setSize(770, 590);
            }
            this.setLocationRelativeTo(null);
            loadMenu();
            this.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadMenu() {
        JMenuBar menu = new JMenuBar();
        JMenuItem jMenuItem1 = new JMenuItem();
        JMenuItem jMenuItem2 = new JMenuItem();
        JMenuItem jMenuItem3 = new JMenuItem();
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.exit(0);
            }
        });
        jMenuItem2.setText("Add Client Tab");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addClient();
            }
        });
        jMenuItem3.setText("Remove Selected Tab");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTab();
            }
        });
        JMenu fileMenu = new JMenu();
        JMenu clientMenu = new JMenu();
        fileMenu.setText("File");
        fileMenu.add(jMenuItem1);
        clientMenu.setText("Client");
        clientMenu.add(jMenuItem2);
        clientMenu.add(jMenuItem3);
        menu.add(fileMenu);
        menu.add(clientMenu);
        this.setJMenuBar(menu);
    }
    
    private void addClient() {
        try {
            Applet canvas = (Applet) new URLClassLoader(new URL[] {
                    client.toURL()}).loadClass(getMainClass()).newInstance();
                canvas.setStub(this);
                canvas.init();
                canvas.start();
                pane.addTab("Client", canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void removeTab() {
        try {
            if (pane.getSelectedIndex() != -1) {
                pane.removeTabAt(pane.getSelectedIndex());
            }
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
    
    private String getVersion() {
        try {
            URL url = new URL("https://raw.github.com/s0beit/Casper/master/version.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String version;
            if ((version = in.readLine()) != null) {
                return version;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private boolean needsUpdating() {
        if (!getVersion().equals(thisVersion)) {
            return true;
        }
        return false;
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
    
    private void downloadFile(final String url, final String name) {
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
            FileOutputStream fos = new FileOutputStream(name);
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
  
    private void update() {
        if (client.exists()) {
            if (needsUpdating()) {
                downloadFile("https://github.com/s0beit/Casper/blob/master/jar/Casper.jar?raw=true", "Casper_v" + getVersion() + ".jar");
                client.delete();
                downloadFile(URL, "runescape.jar");
                JOptionPane.showMessageDialog(this, "Casper Client has been updated to version " + getVersion() + ", please run the updated client.", "Successfully Updated", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        } else {
            downloadFile(URL, "runescape.jar");
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
