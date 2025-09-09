/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import net.opticlient.DontBlock;
import net.opticlient.SearchTags;
import net.opticlient.hack.DontSaveState;
import net.opticlient.hack.Hack;
import net.opticlient.navigator.NavigatorMainScreen;

@DontSaveState
@DontBlock
@SearchTags({"ClickGUI", "click gui", "SearchGUI", "search gui", "HackMenu",
	"hack menu"})
public final class NavigatorHack extends Hack
{
	public NavigatorHack()
	{
		super("Navigator");
	}
	
	@Override
	protected void onEnable()
	{
		if(!(MC.currentScreen instanceof NavigatorMainScreen))
			MC.setScreen(new NavigatorMainScreen());
		
		setEnabled(false);
	}
}
