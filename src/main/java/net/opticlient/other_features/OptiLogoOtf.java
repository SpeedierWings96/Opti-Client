/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.other_features;

import java.awt.Color;
import java.util.function.BooleanSupplier;

import net.opticlient.DontBlock;
import net.opticlient.SearchTags;
import net.opticlient.other_feature.OtherFeature;
import net.opticlient.settings.ColorSetting;
import net.opticlient.settings.EnumSetting;

@SearchTags({"Opti logo", "top left corner"})
@DontBlock
public final class OptiLogoOtf extends OtherFeature
{
	private final ColorSetting bgColor = new ColorSetting("Background",
		"Background color.\n"
			+ "Only visible when \u00a76RainbowUI\u00a7r is disabled.",
		Color.WHITE);
	
	private final ColorSetting txtColor =
		new ColorSetting("Text", "Text color.", Color.BLACK);
	
	private final EnumSetting<Visibility> visibility =
		new EnumSetting<>("Visibility", Visibility.values(), Visibility.ALWAYS);
	
	public OptiLogoOtf()
	{
		super("OptiLogo", "Shows the OPTI logo and version on the screen.");
		addSetting(bgColor);
		addSetting(txtColor);
		addSetting(visibility);
	}
	
	public boolean isVisible()
	{
		return visibility.getSelected().isVisible();
	}
	
	public int getBackgroundColor()
	{
		return bgColor.getColorI(128);
	}
	
	public int getTextColor()
	{
		return txtColor.getColorI();
	}
	
	public static enum Visibility
	{
		ALWAYS("Always", () -> true),
		
		ONLY_OUTDATED("Only when outdated",
			() -> OPTI.getUpdater().isOutdated());
		
		private final String name;
		private final BooleanSupplier visible;
		
		private Visibility(String name, BooleanSupplier visible)
		{
			this.name = name;
			this.visible = visible;
		}
		
		public boolean isVisible()
		{
			return visible.getAsBoolean();
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
