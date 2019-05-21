package com.tatoulo.nexoria;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import com.sun.awt.AWTUtilities;

import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.util.WindowMover;

@SuppressWarnings("serial")
public class LauncherFrame extends JFrame{
	
    private static LauncherFrame instance;
    private LauncherPanel launcherPanel;
    private static CrashReporter crashReporter;
    public static File ramFile = new File(Launcher.NX_DIR, "ram.txt");

	
	public LauncherFrame() {
        this.setTitle("Nexoria - Launcher");
        this.setSize(641, 834);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.setIconImage(Swinger.getResource("nexoria.png"));
        this.setContentPane(launcherPanel = new LauncherPanel());
        AWTUtilities.setWindowOpacity(this, 0.0F);
        
        WindowMover mover = new WindowMover(this);
        this.addMouseListener(mover);
        this.addMouseMotionListener(mover);
        
        this.setVisible(true);
        
        Animator.fadeInFrame(this, 2);

	}

    public static void main(String[] args) {
        Swinger.setSystemLookNFeel();
        Swinger.setResourcePath("/com/tatoulo/nexoria/ressources/");
        Launcher.NX_CRASHES_DIR.mkdirs();
        crashReporter = new CrashReporter("Nexoria Launcher", Launcher.NX_CRASHES_DIR);
        instance = new LauncherFrame();
        if(!ramFile.exists()) {
            try {
                ramFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
	
    public static LauncherFrame getInstance() {
        return instance;
    }

    public static CrashReporter getCrashReporter() {
        return crashReporter;
    }

    public LauncherPanel getLauncherPanel() {
        return this.launcherPanel;
    }

}
