/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.opticlient.Category;
import net.opticlient.SearchTags;
import net.opticlient.events.UpdateListener;
import net.opticlient.hack.Hack;
import net.opticlient.settings.SliderSetting;
import net.opticlient.settings.SliderSetting.ValueDisplay;
import net.opticlient.util.ItemUtils;
import net.opticlient.util.Rotation;

@SearchTags({"AutoPotion", "auto potion", "AutoSplashPotion",
	"auto splash potion"})
public final class AutoPotionHack extends Hack implements UpdateListener
{
	private final SliderSetting health = new SliderSetting("Health",
		"Throws a potion when your health reaches this value or falls below it.",
		6, 0.5, 9.5, 0.5, ValueDisplay.DECIMAL.withSuffix(" hearts"));
	
	private int timer;
	
	public AutoPotionHack()
	{
		super("AutoPotion");
		
		setCategory(Category.COMBAT);
		addSetting(health);
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
		timer = 0;
	}
	
	@Override
	public void onUpdate()
	{
		// search potion in hotbar
		int potionInHotbar = findPotion(0, 9);
		
		// check if any potion was found
		if(potionInHotbar != -1)
		{
			// check timer
			if(timer > 0)
			{
				timer--;
				return;
			}
			
			// check health
			if(MC.player.getHealth() > health.getValueF() * 2F)
				return;
			
			// save old slot
			int oldSlot = MC.player.getInventory().getSelectedSlot();
			
			// throw potion in hotbar
			MC.player.getInventory().setSelectedSlot(potionInHotbar);
			new Rotation(MC.player.getYaw(), 90).sendPlayerLookPacket();
			IMC.getInteractionManager().rightClickItem();
			
			// reset slot and rotation
			MC.player.getInventory().setSelectedSlot(oldSlot);
			new Rotation(MC.player.getYaw(), MC.player.getPitch())
				.sendPlayerLookPacket();
			
			// reset timer
			timer = 10;
			
			return;
		}
		
		// search potion in inventory
		int potionInInventory = findPotion(9, 36);
		
		// move potion in inventory to hotbar
		if(potionInInventory != -1)
			IMC.getInteractionManager()
				.windowClick_QUICK_MOVE(potionInInventory);
	}
	
	private int findPotion(int startSlot, int endSlot)
	{
		for(int i = startSlot; i < endSlot; i++)
		{
			ItemStack stack = MC.player.getInventory().getStack(i);
			
			// filter out non-splash potion items
			if(stack.getItem() != Items.SPLASH_POTION)
				continue;
			
			// search for instant health effects
			if(ItemUtils.hasEffect(stack, StatusEffects.INSTANT_HEALTH))
				return i;
		}
		
		return -1;
	}
}
