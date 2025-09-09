/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.other_features;

import net.opticlient.DontBlock;
import net.opticlient.SearchTags;
import net.opticlient.other_feature.OtherFeature;
import net.opticlient.settings.CheckboxSetting;

@SearchTags({"turn off", "hide OPTI logo", "ghost mode", "stealth mode",
	"vanilla Minecraft"})
@DontBlock
public final class DisableOtf extends OtherFeature
{
	private final CheckboxSetting hideEnableButton = new CheckboxSetting(
		"Hide enable button",
		"Removes the \"Enable OPTI\" button as soon as you close the Statistics screen."
			+ " You will have to restart the game to re-enable OPTI.",
		false);
	
	public DisableOtf()
	{
		super("Disable OPTI",
			"To disable OPTI, go to the Statistics screen and press the \"Disable OPTI\" button.\n"
				+ "It will turn into an \"Enable OPTI\" button once pressed.");
		addSetting(hideEnableButton);
	}
	
	public boolean shouldHideEnableButton()
	{
		return !OPTI.isEnabled() && hideEnableButton.isChecked();
	}
}
