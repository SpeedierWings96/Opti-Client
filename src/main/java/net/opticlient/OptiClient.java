/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.MinecraftClient;
import net.opticlient.altmanager.AltManager;
import net.opticlient.altmanager.Encryption;
import net.opticlient.analytics.PlausibleAnalytics;
import net.opticlient.clickgui.ClickGui;
import net.opticlient.command.CmdList;
import net.opticlient.command.CmdProcessor;
import net.opticlient.command.Command;
import net.opticlient.event.EventManager;
import net.opticlient.events.ChatOutputListener;
import net.opticlient.events.GUIRenderListener;
import net.opticlient.events.KeyPressListener;
import net.opticlient.events.PostMotionListener;
import net.opticlient.events.PreMotionListener;
import net.opticlient.events.UpdateListener;
import net.opticlient.hack.Hack;
import net.opticlient.hack.HackList;
import net.opticlient.hud.IngameHUD;
import net.opticlient.keybinds.KeybindList;
import net.opticlient.keybinds.KeybindProcessor;
import net.opticlient.mixinterface.IMinecraftClient;
import net.opticlient.navigator.Navigator;
import net.opticlient.other_feature.OtfList;
import net.opticlient.other_feature.OtherFeature;
import net.opticlient.settings.SettingsFile;
import net.opticlient.update.ProblematicResourcePackDetector;
import net.opticlient.update.OptiUpdater;
import net.opticlient.util.json.JsonException;

public enum OptiClient
{
	INSTANCE;
	
	public static MinecraftClient MC;
	public static IMinecraftClient IMC;
	
	public static final String VERSION = "7.50.1";
	public static final String MC_VERSION = "1.21.8";
	
	private PlausibleAnalytics plausible;
	private EventManager eventManager;
	private AltManager altManager;
	private HackList hax;
	private CmdList cmds;
	private OtfList otfs;
	private SettingsFile settingsFile;
	private Path settingsProfileFolder;
	private KeybindList keybinds;
	private ClickGui gui;
	private Navigator navigator;
	private CmdProcessor cmdProcessor;
	private IngameHUD hud;
	private RotationFaker rotationFaker;
	private FriendsList friends;
	private OptiTranslator translator;
	
	private boolean enabled = true;
	private static boolean guiInitialized;
	private OptiUpdater updater;
	private ProblematicResourcePackDetector problematicPackDetector;
	private Path OPTIFolder;
	
	public void initialize()
	{
		System.out.println("Starting Opti Client...");
		
		MC = MinecraftClient.getInstance();
		IMC = (IMinecraftClient)MC;
		OPTIFolder = createOPTIFolder();
		
		Path analyticsFile = OPTIFolder.resolve("analytics.json");
		plausible = new PlausibleAnalytics(analyticsFile);
		plausible.pageview("/");
		
		eventManager = new EventManager(this);
		
		Path enabledHacksFile = OPTIFolder.resolve("enabled-hacks.json");
		hax = new HackList(enabledHacksFile);
		
		cmds = new CmdList();
		
		otfs = new OtfList();
		
		Path settingsFile = OPTIFolder.resolve("settings.json");
		settingsProfileFolder = OPTIFolder.resolve("settings");
		this.settingsFile = new SettingsFile(settingsFile, hax, cmds, otfs);
		this.settingsFile.load();
		hax.tooManyHaxHack.loadBlockedHacksFile();
		
		Path keybindsFile = OPTIFolder.resolve("keybinds.json");
		keybinds = new KeybindList(keybindsFile);
		
		Path guiFile = OPTIFolder.resolve("windows.json");
		gui = new ClickGui(guiFile);
		
		Path preferencesFile = OPTIFolder.resolve("preferences.json");
		navigator = new Navigator(preferencesFile, hax, cmds, otfs);
		
		Path friendsFile = OPTIFolder.resolve("friends.json");
		friends = new FriendsList(friendsFile);
		friends.load();
		
		translator = new OptiTranslator();
		
		cmdProcessor = new CmdProcessor(cmds);
		eventManager.add(ChatOutputListener.class, cmdProcessor);
		
		KeybindProcessor keybindProcessor =
			new KeybindProcessor(hax, keybinds, cmdProcessor);
		eventManager.add(KeyPressListener.class, keybindProcessor);
		
		hud = new IngameHUD();
		eventManager.add(GUIRenderListener.class, hud);
		
		rotationFaker = new RotationFaker();
		eventManager.add(PreMotionListener.class, rotationFaker);
		eventManager.add(PostMotionListener.class, rotationFaker);
		
		updater = new OptiUpdater();
		eventManager.add(UpdateListener.class, updater);
		
		problematicPackDetector = new ProblematicResourcePackDetector();
		problematicPackDetector.start();
		
		Path altsFile = OPTIFolder.resolve("alts.encrypted_json");
		Path encFolder = Encryption.chooseEncryptionFolder();
		altManager = new AltManager(altsFile, encFolder);
	}
	
	private Path createOPTIFolder()
	{
		Path dotMinecraftFolder = MC.runDirectory.toPath().normalize();
		Path OPTIFolder = dotMinecraftFolder.resolve("Opti");
		
		try
		{
			Files.createDirectories(OPTIFolder);
			
		}catch(IOException e)
		{
			throw new RuntimeException(
				"Couldn't create .minecraft/opti folder.", e);
		}
		
		return OPTIFolder;
	}
	
	public String translate(String key, Object... args)
	{
		return translator.translate(key, args);
	}
	
	public PlausibleAnalytics getPlausible()
	{
		return plausible;
	}
	
	public EventManager getEventManager()
	{
		return eventManager;
	}
	
	public void saveSettings()
	{
		settingsFile.save();
	}
	
	public ArrayList<Path> listSettingsProfiles()
	{
		if(!Files.isDirectory(settingsProfileFolder))
			return new ArrayList<>();
		
		try(Stream<Path> files = Files.list(settingsProfileFolder))
		{
			return files.filter(Files::isRegularFile)
				.collect(Collectors.toCollection(ArrayList::new));
			
		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void loadSettingsProfile(String fileName)
		throws IOException, JsonException
	{
		settingsFile.loadProfile(settingsProfileFolder.resolve(fileName));
	}
	
	public void saveSettingsProfile(String fileName)
		throws IOException, JsonException
	{
		settingsFile.saveProfile(settingsProfileFolder.resolve(fileName));
	}
	
	public HackList getHax()
	{
		return hax;
	}
	
	public CmdList getCmds()
	{
		return cmds;
	}
	
	public OtfList getOtfs()
	{
		return otfs;
	}
	
	public Feature getFeatureByName(String name)
	{
		Hack hack = getHax().getHackByName(name);
		if(hack != null)
			return hack;
		
		Command cmd = getCmds().getCmdByName(name.substring(1));
		if(cmd != null)
			return cmd;
		
		OtherFeature otf = getOtfs().getOtfByName(name);
		return otf;
	}
	
	public KeybindList getKeybinds()
	{
		return keybinds;
	}
	
	public ClickGui getGui()
	{
		if(!guiInitialized)
		{
			guiInitialized = true;
			gui.init();
		}
		
		return gui;
	}
	
	public Navigator getNavigator()
	{
		return navigator;
	}
	
	public CmdProcessor getCmdProcessor()
	{
		return cmdProcessor;
	}
	
	public IngameHUD getHud()
	{
		return hud;
	}
	
	public RotationFaker getRotationFaker()
	{
		return rotationFaker;
	}
	
	public FriendsList getFriends()
	{
		return friends;
	}
	
	public OptiTranslator getTranslator()
	{
		return translator;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		
		if(!enabled)
		{
			hax.panicHack.setEnabled(true);
			hax.panicHack.onUpdate();
		}
	}
	
	public OptiUpdater getUpdater()
	{
		return updater;
	}
	
	public ProblematicResourcePackDetector getProblematicPackDetector()
	{
		return problematicPackDetector;
	}
	
	public Path getOPTIFolder()
	{
		return OPTIFolder;
	}
	
	public AltManager getAltManager()
	{
		return altManager;
	}
}
