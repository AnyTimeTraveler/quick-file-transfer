package org.simonscode.quickfiletransfer;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Set;

public class Broadcaster extends Thread {
    private final File f;
    private boolean doBroadcast = true;

    Broadcaster(final File f) {
        this.f = f;
    }

    @Override
    public void run() {
        try {
            Set<InetAddress> broadcastAddresses = Main.getBroadcastAddresses();
            byte[] data = (f.length()+":"+f.getName()).getBytes();
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            while (doBroadcast) {
                System.out.println("Sending...");
                for (InetAddress address : broadcastAddresses)
                    socket.send(new DatagramPacket(data, data.length, address, 1337));
                Thread.sleep(1_000);
            }
            socket.close();
        } catch (Exception e) {
            Main.statusDialog.setError(e);
            e.printStackTrace();
        }
    }

    public void stopBroadcasting() {
        doBroadcast = false;
    }
}
