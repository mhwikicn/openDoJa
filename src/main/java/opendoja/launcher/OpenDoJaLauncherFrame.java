package opendoja.launcher;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.nio.file.Path;

final class OpenDoJaLauncherFrame extends JFrame {
    private final JamLaunchService jamLaunchService;
    private final LauncherToolsController toolsController;
    private final JLabel statusLabel;
    private final Action loadJamAction;

    OpenDoJaLauncherFrame(JamLaunchService jamLaunchService, LauncherToolsController toolsController) {
        super(OpenDoJaLauncher.APP_NAME);
        this.jamLaunchService = jamLaunchService;
        this.toolsController = toolsController;
        this.statusLabel = new JLabel("Choose a JAM file to launch.", SwingConstants.CENTER);
        this.loadJamAction = new AbstractAction("Load JAM") {
            @Override
            public void actionPerformed(ActionEvent event) {
                chooseAndLaunchJam();
            }
        };
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());
        setContentPane(buildContent());
        setMinimumSize(new Dimension(360, 360));
        setSize(420, 420);
        setLocationByPlatform(true);
    }

    private JMenuBar buildMenuBar() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem(loadJamAction));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        }));

        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.add(new JMenuItem(new AbstractAction("Keybind Settings") {
            @Override
            public void actionPerformed(ActionEvent event) {
                toolsController.showKeybindSettings(OpenDoJaLauncherFrame.this);
            }
        }));

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent event) {
                showAboutDialog();
            }
        }));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JPanel buildContent() {
        JButton loadButton = new JButton(loadJamAction);
        loadButton.setFont(loadButton.getFont().deriveFont(Font.BOLD, 26f));
        loadButton.setPreferredSize(new Dimension(220, 120));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(loadButton);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.add(centerPanel, BorderLayout.CENTER);
        root.add(statusLabel, BorderLayout.SOUTH);
        return root;
    }

    private void chooseAndLaunchJam() {
        Path jamPath = jamLaunchService.chooseJamFile(this);
        if (jamPath == null) {
            return;
        }
        setLaunchControlsEnabled(false);
        statusLabel.setText("Launching " + jamPath.getFileName() + "...");
        Thread.ofVirtual().start(() -> {
            try {
                GameLaunchResult result = jamLaunchService.launchInSeparateProcess(jamPath);
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Launched " + result.selection().jamPath().getFileName()
                            + " in a separate process.");
                    setLaunchControlsEnabled(true);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Launch failed.");
                    setLaunchControlsEnabled(true);
                    JOptionPane.showMessageDialog(
                            this,
                            e.getMessage(),
                            OpenDoJaLauncher.APP_NAME,
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }

    private void setLaunchControlsEnabled(boolean enabled) {
        loadJamAction.setEnabled(enabled);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(
                this,
                OpenDoJaLauncher.APP_NAME + "\nVersion " + OpenDoJaLauncher.VERSION
                        + "\nLightweight desktop launcher for DoJa games packaged as .jam + .jar.",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
