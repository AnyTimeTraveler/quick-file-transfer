package org.simonscode.quickfiletransfer;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Sender implements Runnable {

    @Override
    public void run() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            System.exit(0);
        }
        File f = chooser.getSelectedFile();

        Main.statusDialog.setStatus("Waiting for receiver...");
        Broadcaster broadcaster = new Broadcaster(f);
        broadcaster.setName("Broadcaster");
        broadcaster.setDaemon(true);
        broadcaster.start();
        try {
            ServerSocket serverSocket = new ServerSocket(1338);
            Socket socket = serverSocket.accept();
            broadcaster.stopBroadcasting();
            Main.statusDialog.setStatus("Sending...");
            InputStream is = new FileInputStream(f);
            OutputStream os = socket.getOutputStream();
            Double size = Long.valueOf(f.length()).doubleValue();

            Main.doCopy(is, os, size);
            os.flush();
            os.close();
            socket.close();
        } catch (IOException e) {
            Main.statusDialog.setError(e);
            e.printStackTrace();
        }
    }

}
