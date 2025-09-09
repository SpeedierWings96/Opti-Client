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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.Entity;
import net.opticlient.OptiClient;

@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowBlockMixin extends Block
	implements FluidDrainable
{
	private PowderSnowBlockMixin(OptiClient OPTI, Settings settings)
	{
		super(settings);
	}
	
	@Inject(at = @At("HEAD"),
		method = "canWalkOnPowderSnow(Lnet/minecraft/entity/Entity;)Z",
		cancellable = true)
	private static void onCanWalkOnPowderSnow(Entity entity,
		CallbackInfoReturnable<Boolean> cir)
	{
		if(!OptiClient.INSTANCE.getHax().snowShoeHack.isEnabled())
			return;
		
		if(entity == OptiClient.MC.player)
			cir.setReturnValue(true);
	}
}
