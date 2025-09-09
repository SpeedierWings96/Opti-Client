/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.opticlient.Category;
import net.opticlient.SearchTags;
import net.opticlient.events.UpdateListener;
import net.opticlient.hack.Hack;
import net.opticlient.mixinterface.IClientPlayerInteractionManager;
import net.opticlient.settings.CheckboxSetting;
import net.opticlient.settings.SliderSetting;
import net.opticlient.settings.SliderSetting.ValueDisplay;
import net.opticlient.util.InventoryUtils;

@SearchTags({"auto totem", "offhand", "off-hand"})
public final class AutoTotemHack extends Hack implements UpdateListener
{
	private final CheckboxSetting showCounter = new CheckboxSetting(
		"Show totem counter", "Displays the number of totems you have.", true);
	
	private final SliderSetting delay = new SliderSetting("Delay",
		"Amount of ticks to wait before equipping the next totem.", 0, 0, 20, 1,
		ValueDisplay.INTEGER);
	
	private final SliderSetting health = new SliderSetting("Health",
		"Won't equip a totem until your health reaches this value or falls"
			+ " below it.\n" + "0 = always active",
		0, 0, 10, 0.5, ValueDisplay.DECIMAL.withSuffix(" hearts")
			.withLabel(1, "1 heart").withLabel(0, "ignore"));
	
	private int nextTickSlot;
	private int totems;
	private int timer;
	private boolean wasTotemInOffhand;
	
	public AutoTotemHack()
	{
		super("AutoTotem");
		setCategory(Category.COMBAT);
		addSetting(showCounter);
		addSetting(delay);
		addSetting(health);
	}
	
	@Override
	public String getRenderName()
	{
		if(!showCounter.isChecked())
			return getName();
		
		if(totems == 1)
			return getName() + " [1 totem]";
		
		return getName() + " [" + totems + " totems]";
	}
	
	@Override
	protected void onEnable()
	{
		nextTickSlot = -1;
		totems = 0;
		timer = 0;
		wasTotemInOffhand = false;
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
		finishMovingTotem();
		
		int nextTotemSlot = searchForTotems();
		
		if(isTotem(MC.player.getOffHandStack()))
		{
			wasTotemInOffhand = true;
			return;
		}
		
		if(wasTotemInOffhand)
		{
			timer = delay.getValueI();
			wasTotemInOffhand = false;
		}
		
		if(nextTotemSlot == -1)
			return;
		
		float healthF = health.getValueF();
		if(healthF > 0 && MC.player.getHealth() > healthF * 2F)
			return;
		
		// don't move items while a container is open
		if(MC.currentScreen instanceof HandledScreen
			&& !(MC.currentScreen instanceof InventoryScreen
				|| MC.currentScreen instanceof CreativeInventoryScreen))
			return;
		
		if(timer > 0)
		{
			timer--;
			return;
		}
		
		moveToOffhand(nextTotemSlot);
	}
	
	private void moveToOffhand(int itemSlot)
	{
		boolean offhandEmpty = MC.player.getOffHandStack().isEmpty();
		
		IClientPlayerInteractionManager im = IMC.getInteractionManager();
		im.windowClick_PICKUP(itemSlot);
		im.windowClick_PICKUP(45);
		
		if(!offhandEmpty)
			nextTickSlot = itemSlot;
	}
	
	private void finishMovingTotem()
	{
		if(nextTickSlot == -1)
			return;
		
		IClientPlayerInteractionManager im = IMC.getInteractionManager();
		im.windowClick_PICKUP(nextTickSlot);
		nextTickSlot = -1;
	}
	
	private int searchForTotems()
	{
		totems = InventoryUtils.count(this::isTotem, 40, true);
		if(totems <= 0)
			return -1;
		
		int totemSlot = InventoryUtils.indexOf(this::isTotem, 40);
		return InventoryUtils.toNetworkSlot(totemSlot);
	}
	
	private boolean isTotem(ItemStack stack)
	{
		return stack.isOf(Items.TOTEM_OF_UNDYING);
	}
}
