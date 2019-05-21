package com.tatoulo.nexoria;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.internal.InternalLaunchProfile;
import fr.theshark34.openlauncherlib.internal.InternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.minecraft.MinecraftLauncher;
import fr.theshark34.openlauncherlib.util.ProcessLogManager;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.Swinger;

public class Launcher {
	
	public static final GameVersion NX_VERSION = new GameVersion("1.7.10", GameType.V1_7_10);
	public static final GameInfos NX_INFOS = new GameInfos("Nexoria V1", NX_VERSION, new GameTweak[] {GameTweak.FORGE});
	public static final File NX_DIR = NX_INFOS.getGameDir();
	public static final File NX_CRASHES_DIR = new File(NX_DIR, "crashes");
	
	private static AuthInfos authInfos;
	private static Thread updateThread;
	
	static void auth(String username, String password) throws AuthenticationException{
		Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
		AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
		authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(), response.getSelectedProfile().getId());
	}
	
	public static void update() throws Exception {
        SUpdate su = new SUpdate("http://nexoria.fr/app/webroot/mcp/", NX_DIR);
        su.addApplication(new FileDeleter());

        updateThread = new Thread() {
            private int val;
            private int max;

            @Override
            public void run() {
                if (BarAPI.getNumberOfFileToDownload() == 0)
                    while(!this.isInterrupted()) {
                        val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
                        max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);

                        LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(max);
                        LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(val);

                        LauncherFrame.getInstance().getLauncherPanel().setInfoText("Telechargement en cours " +
                                BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " +
                                Swinger.percentage(val, max) + "%");
                    }
            }
        };
        updateThread.start();
        su.start();
        updateThread.interrupt();
    }

    public static void launch() throws IOException, LaunchException {
        try
        {
            AuthInfos authInfos = new AuthInfos("", "", "");
            ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(Launcher.NX_INFOS, GameFolder.BASIC, authInfos);
            ExternalLauncher launcher = new ExternalLauncher(profile);

            Process p = launcher.launch();
            ProcessLogManager manager = new ProcessLogManager(p.getInputStream(), new File(NX_DIR, "logs.txt"));
            manager.start();

            Thread.sleep(5000L);
            LauncherFrame.getInstance().setVisible(false);
            p.waitFor();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void interruptThread() {
        {
            updateThread.interrupt();
        }
    }
}
