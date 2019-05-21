package com.tatoulo.bootstrap;

import static fr.theshark34.swinger.Swinger.getTransparentWhite;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import static fr.theshark34.swinger.Swinger.*;

import fr.theshark34.openlauncherlib.bootstrap.Bootstrap;
import fr.theshark34.openlauncherlib.bootstrap.LauncherClasspath;
import fr.theshark34.openlauncherlib.bootstrap.LauncherInfos;
import fr.theshark34.openlauncherlib.util.ErrorUtil;
import fr.theshark34.openlauncherlib.util.GameDir;
import fr.theshark34.openlauncherlib.util.SplashScreen;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;

public class NexoriaBootstrap {
	
	private static SplashScreen splash;
	private static SColoredBar bar;
	private static Thread barThread;
	
	private static final LauncherInfos NX_B_INFOS = new LauncherInfos("Nexoria V1", "com.tatoulo.nexoria.LauncherFrame");
	private static final File NX_DIR = GameDir.createGameDir("Nexoria V1");
	private static final LauncherClasspath NX_B_CP = new LauncherClasspath(new File(NX_DIR, "Launcher/launcher.jar"), new File(NX_DIR, "Launcher/Libs/"));
	
	private static ErrorUtil errorUtil = new ErrorUtil(new File(NX_DIR, "Launcher/crashes/"));
	
	public static void main(String[] args) {
		Swinger.setResourcePath("/com/tatoulo/bootstrap/ressources/");
		
		displaySplash();
		try{
			doUpdate();
		} catch (Exception e) {
			errorUtil.catchError(e, "Impossible de mettre le launcher a jour.");
			barThread.interrupt();
		}
		try {
			launchLauncher();
		} catch (IOException e) {
			errorUtil.catchError(e, "Impossible de lancer le launcher.");
		}

	}
	
	private static void displaySplash() {
		splash = new SplashScreen("Nexoria V1", Swinger.getResource("splash.png"));
		
		bar = new SColoredBar(getTransparentWhite(100), getTransparentWhite(175));
		splash.setLayout(null);
		bar.setBounds(82, 328, 588, 10);
		splash.add(bar);
        splash.setBackground(Swinger.TRANSPARENT);
        bar.setForeground(Color.MAGENTA);
        bar.setVisible(false);
		
		splash.setVisible(true);
	}
	
	private static void doUpdate() throws Exception{
		SUpdate su = new SUpdate("http://nexoria.fr/app/webroot/bootstrap/", new File(NX_DIR, "Launcher"));
		
		barThread = new Thread() {
			@Override
			public void run() {
				while(!this.isInterrupted()) {
					bar.setValue((int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000));
					bar.setMaximum((int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000));
				}
			}
		};
		barThread.start();
		
		su.start();
		barThread.interrupt();
		
	}
	
	public static void launchLauncher() throws IOException {
		Bootstrap bootstrap = new Bootstrap(NX_B_CP, NX_B_INFOS);
		Process p = bootstrap.launch();
		splash.setVisible(false);
		try {
			p.waitFor();
		} catch (InterruptedException e){
		}
		System.exit(0);
	}

}
