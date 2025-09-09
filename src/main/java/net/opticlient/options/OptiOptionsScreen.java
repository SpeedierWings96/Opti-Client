/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.options;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OperatingSystem;
import net.opticlient.OptiClient;
import net.opticlient.analytics.PlausibleAnalytics;
import net.opticlient.commands.FriendsCmd;
import net.opticlient.hacks.XRayHack;
import net.opticlient.other_features.VanillaSpoofOtf;
import net.opticlient.settings.CheckboxSetting;
import net.opticlient.util.ChatUtils;
import net.opticlient.util.OptiColors;

public class OptiOptionsScreen extends Screen
{
	private Screen prevScreen;
	
	public OptiOptionsScreen(Screen prevScreen)
	{
		super(Text.literal(""));
		this.prevScreen = prevScreen;
	}
	
	@Override
	public void init()
	{
		addDrawableChild(ButtonWidget
			.builder(Text.literal("Back"), b -> client.setScreen(prevScreen))
			.dimensions(width / 2 - 100, height / 4 + 144 - 16, 200, 20)
			.build());
		
		addSettingButtons();
		addManagerButtons();
		addLinkButtons();
	}
	
	private void addSettingButtons()
	{
		OptiClient OPTI = OptiClient.INSTANCE;
		FriendsCmd friendsCmd = OPTI.getCmds().friendsCmd;
		CheckboxSetting middleClickFriends = friendsCmd.getMiddleClickFriends();
		PlausibleAnalytics plausible = OPTI.getPlausible();
		VanillaSpoofOtf vanillaSpoofOtf = OPTI.getOtfs().vanillaSpoofOtf;
		CheckboxSetting forceEnglish =
			OPTI.getOtfs().translationsOtf.getForceEnglish();
		
		new OPTIOptionsButton(-154, 24,
			() -> "Click Friends: "
				+ (middleClickFriends.isChecked() ? "ON" : "OFF"),
			middleClickFriends.getWrappedDescription(200),
			b -> middleClickFriends
				.setChecked(!middleClickFriends.isChecked()));
		
		new OPTIOptionsButton(-154, 48,
			() -> "Count Users: " + (plausible.isEnabled() ? "ON" : "OFF"),
			"Counts how many people are using OPTI and which versions are the"
				+ " most popular. This data helps me to decide when I can stop"
				+ " supporting old versions.\n\n"
				+ "These statistics are completely anonymous, never sold, and"
				+ " stay in the EU (I'm self-hosting Plausible in Germany)."
				+ " There are no cookies or persistent identifiers"
				+ " (see plausible.io).",
			b -> plausible.setEnabled(!plausible.isEnabled()));
		
		new OPTIOptionsButton(-154, 72,
			() -> "Spoof Vanilla: "
				+ (vanillaSpoofOtf.isEnabled() ? "ON" : "OFF"),
			vanillaSpoofOtf.getDescription(),
			b -> vanillaSpoofOtf.doPrimaryAction());
		
		new OPTIOptionsButton(-154, 96,
			() -> "Translations: " + (!forceEnglish.isChecked() ? "ON" : "OFF"),
			"Allows text in OPTI to be displayed in other languages than"
				+ " English. It will use the same language that Minecraft is"
				+ " set to.\n\n" + "This is an experimental feature!",
			b -> forceEnglish.setChecked(!forceEnglish.isChecked()));
	}
	
	private void addManagerButtons()
	{
		XRayHack xRayHack = OptiClient.INSTANCE.getHax().xRayHack;
		
		new OPTIOptionsButton(-50, 24, () -> "Keybinds",
			"Keybinds allow you to toggle any hack or command by simply"
				+ " pressing a button.",
			b -> client.setScreen(new KeybindManagerScreen(this)));
		
		new OPTIOptionsButton(-50, 48, () -> "X-Ray Blocks",
			"Manager for the blocks that X-Ray will show.",
			b -> xRayHack.openBlockListEditor(this));
		
		new OPTIOptionsButton(-50, 72, () -> "Zoom",
			"The Zoom Manager allows you to change the zoom key and how far it"
				+ " will zoom in.",
			b -> client.setScreen(new ZoomManagerScreen(this)));
	}
	
	private void addLinkButtons()
	{
		OperatingSystem os = Util.getOperatingSystem();
		
		new OPTIOptionsButton(54, 24, () -> "Official Website",
			"§n§lOptiClient.net",
			b -> os.open("https://www.OptiClient.net/options-website/"));
		
		new OPTIOptionsButton(54, 48, () -> "Opti Wiki", "§n§lOPTI.Wiki",
			b -> os.open("https://www.OptiClient.net/options-wiki/"));
		
		new OPTIOptionsButton(54, 72, () -> "OptiForum", "§n§lOPTIForum.net",
			b -> os.open("https://www.OptiClient.net/options-forum/"));
		
		new OPTIOptionsButton(54, 96, () -> "Twitter", "@opti_Imperium",
			b -> os.open("https://www.OptiClient.net/options-twitter/"));
		
		new OPTIOptionsButton(54, 120, () -> "Donate",
			"§n§lOptiClient.net/donate\n"
				+ "Donate now to help me keep the Opti Client alive and free"
				+ " to use for everyone.\n\n"
				+ "Every bit helps and is much appreciated! You can also get a"
				+ " few cool perks in return.",
			b -> os.open("https://www.OptiClient.net/options-donate/"));
	}
	
	@Override
	public void close()
	{
		client.setScreen(prevScreen);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY,
		float partialTicks)
	{
		renderTitles(context);
		
		for(Drawable drawable : drawables)
			drawable.render(context, mouseX, mouseY, partialTicks);
		
		renderButtonTooltip(context, mouseX, mouseY);
	}
	
	private void renderTitles(DrawContext context)
	{
		TextRenderer tr = client.textRenderer;
		int middleX = width / 2;
		int y1 = 40;
		int y2 = height / 4 + 24 - 28;
		
		context.drawCenteredTextWithShadow(tr, "Opti Options", middleX, y1,
			Colors.WHITE);
		
		context.drawCenteredTextWithShadow(tr, "Settings", middleX - 104, y2,
			OptiColors.VERY_LIGHT_GRAY);
		context.drawCenteredTextWithShadow(tr, "Managers", middleX, y2,
			OptiColors.VERY_LIGHT_GRAY);
		context.drawCenteredTextWithShadow(tr, "Links", middleX + 104, y2,
			OptiColors.VERY_LIGHT_GRAY);
	}
	
	private void renderButtonTooltip(DrawContext context, int mouseX,
		int mouseY)
	{
		for(ClickableWidget button : Screens.getButtons(this))
		{
			if(!button.isSelected() || !(button instanceof OPTIOptionsButton))
				continue;
			
			OPTIOptionsButton woButton = (OPTIOptionsButton)button;
			
			if(woButton.tooltip.isEmpty())
				continue;
			
			context.drawTooltip(textRenderer, woButton.tooltip, mouseX, mouseY);
			break;
		}
	}
	
	private final class OPTIOptionsButton extends ButtonWidget
	{
		private final Supplier<String> messageSupplier;
		private final List<Text> tooltip;
		
		public OPTIOptionsButton(int xOffset, int yOffset,
			Supplier<String> messageSupplier, String tooltip,
			PressAction pressAction)
		{
			super(OptiOptionsScreen.this.width / 2 + xOffset,
				OptiOptionsScreen.this.height / 4 - 16 + yOffset, 100, 20,
				Text.literal(messageSupplier.get()), pressAction,
				ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
			
			this.messageSupplier = messageSupplier;
			
			if(tooltip.isEmpty())
				this.tooltip = Arrays.asList();
			else
			{
				String[] lines = ChatUtils.wrapText(tooltip, 200).split("\n");
				
				Text[] lines2 = new Text[lines.length];
				for(int i = 0; i < lines.length; i++)
					lines2[i] = Text.literal(lines[i]);
				
				this.tooltip = Arrays.asList(lines2);
			}
			
			addDrawableChild(this);
		}
		
		@Override
		public void onPress()
		{
			super.onPress();
			setMessage(Text.literal(messageSupplier.get()));
		}
	}
}
