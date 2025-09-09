/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;
import net.opticlient.OptiClient;
import net.opticlient.hacks.AutoSignHack;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen
{
	@Shadow
	@Final
	private String[] messages;
	
	private AbstractSignEditScreenMixin(OptiClient OPTI, Text title)
	{
		super(title);
	}
	
	@Inject(at = @At("HEAD"), method = "init()V")
	private void onInit(CallbackInfo ci)
	{
		AutoSignHack autoSignHack = OptiClient.INSTANCE.getHax().autoSignHack;
		
		String[] autoSignText = autoSignHack.getSignText();
		if(autoSignText == null)
			return;
		
		for(int i = 0; i < 4; i++)
			messages[i] = autoSignText[i];
		
		finishEditing();
	}
	
	@Inject(at = @At("HEAD"), method = "finishEditing()V")
	private void onFinishEditing(CallbackInfo ci)
	{
		OptiClient.INSTANCE.getHax().autoSignHack.setSignText(messages);
	}
	
	@Shadow
	private void finishEditing()
	{
		
	}
}
