/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.opticlient.OptiClient;

@Mixin(StatsScreen.class)
public abstract class StatsScreenMixin extends Screen
{
	@Unique
	private ButtonWidget toggleOPTIButton;
	
	private StatsScreenMixin(OptiClient OPTI, Text title)
	{
		super(title);
	}
	
	/**
	 * Adds the hidden "Enable/Disable OPTI" button on the Statistics screen.
	 */
	@Inject(at = @At("TAIL"), method = "createButtons()V")
	private void onCreateButtons(CallbackInfo ci)
	{
		if(OptiClient.INSTANCE.getOtfs().disableOtf.shouldHideEnableButton())
			return;
		
		toggleOPTIButton = ButtonWidget
			.builder(Text.literal(""), this::toggleOPTI).width(150).build();
		
		ClickableWidget doneButton = getDoneButton();
		doneButton.setX(width / 2 + 2);
		doneButton.setWidth(150);
		
		toggleOPTIButton.setPosition(width / 2 - 152, doneButton.getY());
		
		updateOPTIButtonText(toggleOPTIButton);
		addDrawableChild(toggleOPTIButton);
	}
	
	@Unique
	private ClickableWidget getDoneButton()
	{
		for(ClickableWidget button : Screens.getButtons(this))
			if(button.getMessage().getString()
				.equals(I18n.translate("gui.done")))
				return button;
			
		throw new IllegalStateException(
			"Can't find the done button on the statistics screen.");
	}
	
	@Unique
	private void toggleOPTI(ButtonWidget button)
	{
		OptiClient OPTI = OptiClient.INSTANCE;
		OPTI.setEnabled(!OPTI.isEnabled());
		
		updateOPTIButtonText(button);
	}
	
	@Unique
	private void updateOPTIButtonText(ButtonWidget button)
	{
		OptiClient OPTI = OptiClient.INSTANCE;
		String text = (OPTI.isEnabled() ? "Disable" : "Enable") + " OPTI";
		button.setMessage(Text.literal(text));
	}
}
