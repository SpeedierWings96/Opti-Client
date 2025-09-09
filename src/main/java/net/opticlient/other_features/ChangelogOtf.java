/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.other_features;

import net.minecraft.util.Util;
import net.opticlient.DontBlock;
import net.opticlient.SearchTags;
import net.opticlient.OptiClient;
import net.opticlient.other_feature.OtherFeature;
import net.opticlient.update.Version;

@SearchTags({"change log", "Opti update", "release notes", "what's new",
	"what is new", "new features", "recently added features"})
@DontBlock
public final class ChangelogOtf extends OtherFeature
{
	public ChangelogOtf()
	{
		super("Changelog", "Opens the changelog in your browser.");
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "View Changelog";
	}
	
	@Override
	public void doPrimaryAction()
	{
		String link = new Version(OptiClient.VERSION).getChangelogLink()
			+ "?utm_source=OPTI+Client&utm_medium=ChangelogOtf&utm_content=View+Changelog";
		Util.getOperatingSystem().open(link);
	}
}
