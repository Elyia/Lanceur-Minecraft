package com.tatoulo.nexoria;

import static fr.theshark34.swinger.Swinger.drawFullsizedImage;
import static fr.theshark34.swinger.Swinger.getResource;
import static fr.theshark34.swinger.Swinger.getTransparentWhite;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;
import fr.theshark34.swinger.textured.STexturedProgressBar;

@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener {

    private Image background = getResource("background.png");
    private static Saver saver = new Saver(new File(Launcher.NX_DIR, "launcher.properties"));
    private static JTextField usernameField = new JTextField(saver.get("username"));
    private static JTextField passwordField = new JPasswordField();
    static RamSelector ramSelector = new RamSelector(new File(Launcher.NX_DIR, "ram.txt"));
    private static STexturedButton playButton = new STexturedButton(getResource("play.png"));
    private STexturedButton quitButton = new STexturedButton(getResource("quit.png"));
    private STexturedButton hideButton = new STexturedButton(getResource("hide.png"));
    private STexturedButton optionsButton = new STexturedButton(getResource("options.png"));
    private SColoredBar progressBar = new SColoredBar(getTransparentWhite(100), getTransparentWhite(175));
    private JLabel infoLabel = new JLabel("Clique sur Jouer !", SwingConstants.CENTER);

    public LauncherPanel() {

        this.setLayout(null);
        usernameField.setForeground(Color.lightGray);
        usernameField.setFont(usernameField.getFont().deriveFont(20F));
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setHorizontalAlignment(SwingConstants.LEFT);
        usernameField.setOpaque(false);
        usernameField.setBorder(null);
        usernameField.setBounds(0, 0, 0, 0);
        this.add(usernameField);

        passwordField.setForeground(Color.lightGray);
        passwordField.setFont(passwordField.getFont().deriveFont(20F));
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setOpaque(false);
        passwordField.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        passwordField.setBorder(null);
        passwordField.setBounds(0, 0, 0, 0);
        this.add(passwordField);

        playButton.setBounds(0, 0);
        playButton.addEventListener(this);
        this.add(playButton);

        hideButton.setBounds(0, 0);
        hideButton.addEventListener(this);
        this.add(hideButton);

        optionsButton.setBounds(0, 0);
        optionsButton.addEventListener(this);
        this.add(optionsButton);

        quitButton.setBounds(0, 0);
        quitButton.addEventListener(this);
        this.add(quitButton);

        this.progressBar.setBounds(0, 0, 0, 0);
        progressBar.setForeground(Color.MAGENTA);
        add(this.progressBar);

        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(usernameField.getFont().deriveFont(17F));
        infoLabel.setBounds(0, 0, 0, 0);
        this.add(infoLabel);
    }

    //* Evenement des boutons *//

    	@Override
	public void onEvent(SwingerEvent e) {
		if(e.getSource() == playButton) {
			setFieldsEnabled(false);
			
			if(usernameField.getText().replaceAll(" ", "").length() == 0 || passwordField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Erreur, vous avez un pseudo ou mot de passe invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
				setFieldsEnabled(true);
				return;
			}
			
            Thread t = new Thread() {
                @Override
    			public void run() {
	                try {
	                	Launcher.auth(usernameField.getText(), passwordField.getText());
	                } catch (AuthenticationException e) {
	    				JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, il est impossible de se connecter | " + e.getErrorModel().getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
	    				setFieldsEnabled(true);
	    				return;
	                }
	                
	                saver.set("username", usernameField.getText());
	                
	                try {
	                	Launcher.update();
	                } catch (Exception e) {
	                	Launcher.interruptThread();
	                	LauncherFrame.getCrashReporter().catchError(e, "Erreur, Impossible de mettre Nexoria ‡ jour");
	    				setFieldsEnabled(true);
	    				return;
	                }
	                try {
	                	try {
							Launcher.launch();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                } catch (LaunchException e) {
	                	LauncherFrame.getCrashReporter().catchError(e, "Erreur, Impossible de lancer Nexoria.");
	    				setFieldsEnabled(true);
	                }
	                
                }
    	};
            t.start();
        }	else if(e.getSource() == quitButton) {
            System.exit(0);
        }	else if(e.getSource() == hideButton) {
            LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
        }

        //* Ev√®nement RAM *//

        else if (e.getSource() == optionsButton) {
            ramSelector.display().setTitle("Choix de la ram | Nexoria");
            ramSelector.display().setIconImage(LauncherFrame.getInstance().getIconImage());
            ramSelector.display().setDefaultCloseOperation(ramSelector.display().DO_NOTHING_ON_CLOSE);
            ramSelector.display().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    ramSelector.save();
                    ramSelector.display().setVisible(false);
                }
            });
            ramSelector.display();
        }
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        drawFullsizedImage(graphics, this, background);
        graphics.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    public static void setFieldsEnabled(boolean enabled) {
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        playButton.setEnabled(enabled);
    }

    public SColoredBar getProgressBar()
    {
        return progressBar;
    }

    public void setInfoText(String text) {
        infoLabel.setText(text);
    }
    public static RamSelector getRamSelector()
    {
        return ramSelector;
    }
}