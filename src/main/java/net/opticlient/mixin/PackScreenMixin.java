/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.text.Text;
import net.opticlient.OptiClient;

@Mixin(PackScreen.class)
public class PackScreenMixin extends Screen
{
	private PackScreenMixin(OptiClient OPTI, Text title)
	{
		super(title);
	}
	
	/**
	 * Scans for problematic resource packs (currently just VanillaTweaks
	 * Twinkling Stars) whenever the resource pack screen is closed.
	 */
	@Inject(at = @At("HEAD"), method = "close()V")
	public void onClose(CallbackInfo ci)
	{
		OptiClient.INSTANCE.getProblematicPackDetector().start();
	}
}
