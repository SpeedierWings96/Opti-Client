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

import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.opticlient.OptiClient;
import net.opticlient.hacks.NoWeatherHack;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess, AutoCloseable
{
	@Inject(at = @At("HEAD"),
		method = "getRainGradient(F)F",
		cancellable = true)
	private void onGetRainGradient(float delta,
		CallbackInfoReturnable<Float> cir)
	{
		if(OptiClient.INSTANCE.getHax().noWeatherHack.isRainDisabled())
			cir.setReturnValue(0F);
	}
	
	@Override
	public float getSkyAngle(float tickDelta)
	{
		NoWeatherHack noWeather = OptiClient.INSTANCE.getHax().noWeatherHack;
		
		long timeOfDay = noWeather.isTimeChanged() ? noWeather.getChangedTime()
			: getLevelProperties().getTimeOfDay();
		
		return getDimension().getSkyAngle(timeOfDay);
	}
	
	@Override
	public int getMoonPhase()
	{
		NoWeatherHack noWeather = OptiClient.INSTANCE.getHax().noWeatherHack;
		
		if(noWeather.isMoonPhaseChanged())
			return noWeather.getChangedMoonPhase();
		
		return getDimension().getMoonPhase(getLunarTime());
	}
}
