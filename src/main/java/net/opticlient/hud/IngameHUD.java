/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hud;

import net.minecraft.client.gui.DrawContext;
import net.opticlient.OptiClient;
import net.opticlient.clickgui.ClickGui;
import net.opticlient.clickgui.screens.ClickGuiScreen;
import net.opticlient.events.GUIRenderListener;

public final class IngameHUD implements GUIRenderListener
{
	private final OptiLogo OptiLogo = new OptiLogo();
	private final HackListHUD hackList = new HackListHUD();
	private TabGui tabGui;
	
	@Override
	public void onRenderGUI(DrawContext context, float partialTicks)
	{
		if(!OptiClient.INSTANCE.isEnabled())
			return;
		
		if(tabGui == null)
			tabGui = new TabGui();
		
		ClickGui clickGui = OptiClient.INSTANCE.getGui();
		
		clickGui.updateColors();
		
		OptiLogo.render(context);
		hackList.render(context, partialTicks);
		tabGui.render(context, partialTicks);
		
		// pinned windows
		if(!(OptiClient.MC.currentScreen instanceof ClickGuiScreen))
			clickGui.renderPinnedWindows(context, partialTicks);
	}
	
	public HackListHUD getHackList()
	{
		return hackList;
	}
}
