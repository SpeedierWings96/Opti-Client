/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.ai;

import net.minecraft.client.MinecraftClient;
import net.opticlient.OptiClient;
import net.opticlient.hack.HackList;

public record PlayerAbilities(boolean invulnerable, boolean creativeFlying,
	boolean flying, boolean immuneToFallDamage, boolean noWaterSlowdown,
	boolean jesus, boolean spider)
{
	
	private static final OptiClient OPTI = OptiClient.INSTANCE;
	private static final MinecraftClient MC = OptiClient.MC;
	
	public static PlayerAbilities get()
	{
		HackList hax = OPTI.getHax();
		net.minecraft.entity.player.PlayerAbilities mcAbilities =
			MC.player.getAbilities();
		
		boolean invulnerable =
			mcAbilities.invulnerable || mcAbilities.creativeMode;
		boolean creativeFlying = mcAbilities.flying;
		boolean flying = creativeFlying || hax.flightHack.isEnabled();
		boolean immuneToFallDamage = invulnerable || hax.noFallHack.isEnabled();
		boolean noWaterSlowdown = hax.antiWaterPushHack.isPreventingSlowdown();
		boolean jesus = hax.jesusHack.isEnabled();
		boolean spider = hax.spiderHack.isEnabled();
		
		return new PlayerAbilities(invulnerable, creativeFlying, flying,
			immuneToFallDamage, noWaterSlowdown, jesus, spider);
	}
}
