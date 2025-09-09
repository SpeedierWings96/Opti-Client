/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import net.minecraft.util.shape.VoxelShapes;
import net.opticlient.Category;
import net.opticlient.SearchTags;
import net.opticlient.events.CactusCollisionShapeListener;
import net.opticlient.hack.Hack;

@SearchTags({"NoCactus", "anti cactus", "no cactus"})
public final class AntiCactusHack extends Hack
	implements CactusCollisionShapeListener
{
	public AntiCactusHack()
	{
		super("AntiCactus");
		setCategory(Category.BLOCKS);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(CactusCollisionShapeListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(CactusCollisionShapeListener.class, this);
	}
	
	@Override
	public void onCactusCollisionShape(CactusCollisionShapeEvent event)
	{
		event.setCollisionShape(VoxelShapes.fullCube());
	}
}
