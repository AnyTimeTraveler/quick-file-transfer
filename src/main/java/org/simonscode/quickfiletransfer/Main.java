package org.simonscode.quickfiletransfer;

import javax.swing.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Main {

    public static StatusDialog statusDialog = new StatusDialog();

    public static void main(String[] args) {
        Object[] objects = {"Send", "Receive"};
        Object selection = JOptionPane.showInputDialog(null,
                "Send or Receive?",
                "QuickFileTransfer",
                JOptionPane.QUESTION_MESSAGE,
                null,
                objects,
                objects[0]);
        if (selection != null) {
            if (selection == objects[0]) {
                new Sender().run();
            } else if (selection == objects[1]) {
                new Receiver().run();
            } else {
                System.exit(-1);
            }
        } else {
            System.exit(-1);
        }
    }

    protected static Set<InetAddress> getBroadcastAddresses() throws SocketException {
        Set<InetAddress> listOfBroadcasts = new HashSet<>();
        Enumeration list = NetworkInterface.getNetworkInterfaces();

        while (list.hasMoreElements()) {
            NetworkInterface iface = (NetworkInterface) list.nextElement();

            if (iface == null) continue;

            if (!iface.isLoopback() && iface.isUp()) {
                System.out.println("Found non-loopback, up interface:" + iface);

                Iterator it = iface.getInterfaceAddresses().iterator();
                //noinspection WhileLoopReplaceableByForEach
                while (it.hasNext()) {
                    InterfaceAddress address = (InterfaceAddress) it.next();
                    System.out.println("Found address: " + address);
                    if (address == null) continue;
                    InetAddress broadcast = address.getBroadcast();
                    if (broadcast != null) {
                        System.out.println("Found broadcast: " + broadcast);
                        listOfBroadcasts.add(broadcast);
                    }
                }
            }
        }

        return listOfBroadcasts;
    }

    static void doCopy(InputStream is, OutputStream os, Double size) {
        try {
            Main.statusDialog.setProgress(0);
            long bytesWritten = 0L;
            int bytesRead;
            for (byte[] buffer = new byte[8192]; (bytesRead = is.read(buffer)) > 0; bytesWritten += (long) bytesRead) {
                os.write(buffer, 0, bytesRead);
                Main.statusDialog.setProgress(Double.valueOf((bytesWritten / size) * 10_000).intValue());
            }
            Main.statusDialog.setProgress(10_000);
            Main.statusDialog.setStatus("Done!");
        } catch (Exception e) {
            Main.statusDialog.setError(e);
            e.printStackTrace();
        }
    }
}
