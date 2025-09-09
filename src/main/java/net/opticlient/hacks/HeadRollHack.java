/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import net.minecraft.util.math.MathHelper;
import net.opticlient.Category;
import net.opticlient.SearchTags;
import net.opticlient.events.UpdateListener;
import net.opticlient.hack.Hack;
import net.opticlient.util.Rotation;

@SearchTags({"head roll", "nodding", "yes"})
public final class HeadRollHack extends Hack implements UpdateListener
{
	public HeadRollHack()
	{
		super("HeadRoll");
		setCategory(Category.FUN);
	}
	
	@Override
	protected void onEnable()
	{
		// disable incompatible derps
		OPTI.getHax().derpHack.setEnabled(false);
		OPTI.getHax().tiredHack.setEnabled(false);
		
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
		float timer = MC.player.age % 20 / 10F;
		float pitch = MathHelper.sin(timer * (float)Math.PI) * 90F;
		
		new Rotation(MC.player.getYaw(), pitch).sendPlayerLookPacket();
	}
}
