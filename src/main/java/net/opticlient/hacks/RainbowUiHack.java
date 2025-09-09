/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import net.opticlient.Category;
import net.opticlient.SearchTags;
import net.opticlient.hack.Hack;

@SearchTags({"RainbowGUI", "rainbow ui", "rainbow gui", "rgb"})
public final class RainbowUiHack extends Hack
{
	public RainbowUiHack()
	{
		super("RainbowUI");
		setCategory(Category.FUN);
	}
	
	// See ClickGui.updateColors()
}
