package org.simonscode.quickfiletransfer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.stream.Collectors;

public class StatusDialog extends JDialog {

    private JTextArea status = new JTextArea("Waiting...");
    private JProgressBar progressBar = new JProgressBar();

    public StatusDialog() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ignored) {
        }

        progressBar.setMinimum(0);
        progressBar.setMaximum(10_000);
        status.setLineWrap(true);
        status.setEditable(false);
        status.setWrapStyleWord(true);
        setLayout(new BorderLayout());
        add(status, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);
        setLocationRelativeTo(null);

        setSize(400, 150);
        validate();
        repaint();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    public void setError(Throwable e) {
        setStatus("ERROR: " + e.getMessage() + "\n" + Arrays.stream(e.getStackTrace()).
                map(StackTraceElement::toString).
                collect(Collectors.joining("\n")));
    }

    public void setStatus(String status) {
        setVisible(true);
        this.status.setText(status);
        revalidate();
    }

    void setProgress(int progress) {
        progressBar.setValue(progress);

    }
}
