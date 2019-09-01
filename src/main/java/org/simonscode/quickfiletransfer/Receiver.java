package org.simonscode.quickfiletransfer;

import javax.swing.*;
import java.io.*;
import java.net.*;

@SuppressWarnings("Duplicates")
public class Receiver implements Runnable {
    @Override
    public void run() {
        try {
            Main.statusDialog.setStatus("Waiting for sender...");
            JFileChooser chooser = new JFileChooser();

            byte[] data = new byte[1024];
            DatagramSocket socket = new DatagramSocket(1337);
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.setSoTimeout(60_000);
            socket.setBroadcast(true);
            try {
                socket.receive(packet);
            } catch (SocketTimeoutException e) {
                Main.statusDialog.setStatus("ERROR: Could not find sender!");

                // Wait for user to close status dialog
                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException ignored) {
                }
                return;
            }
            socket.close();
            data = packet.getData();
            if (data != null && data.length > 0) {
                String s = new String(data).trim();
                chooser.setSelectedFile(new File(s.substring(s.indexOf(":") + 1)));
                if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                    System.exit(0);
                }
                File f = chooser.getSelectedFile();
                if (f.exists()) {
                    if (JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "File exists!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        //noinspection ResultOfMethodCallIgnored
                        f.delete();
                    } else {
                        System.exit(0);
                    }
                }
                InetAddress targetAddress = packet.getAddress();

                Socket transferSocket = new Socket();
                transferSocket.connect(new InetSocketAddress(targetAddress, 1338));
                Main.statusDialog.setStatus("Receiving...");
                InputStream is = transferSocket.getInputStream();
                OutputStream os = new FileOutputStream(f);
                Double size = Long.valueOf(Long.parseLong(s.substring(0, s.indexOf(":")))).doubleValue();

                Main.doCopy(is, os, size);
                os.flush();
                is.close();
                os.close();
                transferSocket.close();
            } else {
                Main.statusDialog.setStatus("ERROR: Received a broadcast but have idea what that meant.\nAborting!");
            }
        } catch (IOException e) {
            Main.statusDialog.setError(e);
            e.printStackTrace();
        }
    }
}
