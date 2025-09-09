/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.opticlient.OptiClient;
import net.opticlient.other_features.OptiLogoOtf;
import net.opticlient.util.RenderUtils;

public final class OptiLogo
{
	private static final OptiClient OPTI = OptiClient.INSTANCE;
	private static final Identifier LOGO_TEXTURE =
		Identifier.of("Opti", "Opti_128.png");
	
	public void render(DrawContext context)
	{
		OptiLogoOtf otf = OPTI.getOtfs().OptiLogoOtf;
		if(!otf.isVisible())
			return;
		
		String version = getVersionString();
		TextRenderer tr = OptiClient.MC.textRenderer;
		
		// background
		int bgColor;
		if(OPTI.getHax().rainbowUiHack.isEnabled())
			bgColor = RenderUtils.toIntColor(OPTI.getGui().getAcColor(), 0.5F);
		else
			bgColor = otf.getBackgroundColor();
		context.fill(0, 6, tr.getWidth(version) + 76, 17, bgColor);
		
		context.state.goUpLayer();
		
		// version string
		context.drawText(tr, version, 74, 8, otf.getTextColor(), false);
		
		// OPTI logo
		context.drawTexture(RenderPipelines.GUI_TEXTURED, LOGO_TEXTURE, 0, 3, 0,
			0, 72, 18, 72, 18);
		
		context.state.goDownLayer();
	}
	
	private String getVersionString()
	{
		String version = "v" + OptiClient.VERSION;
		version += " MC" + OptiClient.MC_VERSION;
		
		if(OPTI.getUpdater().isOutdated())
			version += " (outdated)";
		
		return version;
	}
}
