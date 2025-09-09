/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import net.minecraft.util.math.Vec3d;
import net.opticlient.Category;
import net.opticlient.events.UpdateListener;
import net.opticlient.hack.Hack;

public final class NoWebHack extends Hack implements UpdateListener
{
	public NoWebHack()
	{
		super("NoWeb");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		MC.player.movementMultiplier = Vec3d.ZERO;
	}
}
