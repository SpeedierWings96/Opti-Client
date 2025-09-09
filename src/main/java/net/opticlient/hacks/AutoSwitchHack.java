/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import net.minecraft.entity.player.PlayerInventory;
import net.opticlient.Category;
import net.opticlient.SearchTags;
import net.opticlient.events.UpdateListener;
import net.opticlient.hack.Hack;

@SearchTags({"auto switch"})
public final class AutoSwitchHack extends Hack implements UpdateListener
{
	public AutoSwitchHack()
	{
		super("AutoSwitch");
		setCategory(Category.ITEMS);
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
		PlayerInventory inventory = MC.player.getInventory();
		int prevSlot = inventory.getSelectedSlot();
		
		if(prevSlot == 8)
			inventory.setSelectedSlot(0);
		else
			inventory.setSelectedSlot(prevSlot + 1);
	}
}
