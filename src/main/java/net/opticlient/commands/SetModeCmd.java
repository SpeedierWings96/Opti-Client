/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.commands;

import net.opticlient.DontBlock;
import net.opticlient.Feature;
import net.opticlient.command.CmdError;
import net.opticlient.command.CmdException;
import net.opticlient.command.CmdSyntaxError;
import net.opticlient.command.Command;
import net.opticlient.settings.EnumSetting;
import net.opticlient.settings.Setting;
import net.opticlient.util.CmdUtils;

@DontBlock
public final class SetModeCmd extends Command
{
	public SetModeCmd()
	{
		super("setmode",
			"Changes a mode setting of a feature. Allows you to\n"
				+ "switch modes through keybinds.",
			".setmode <feature> <setting> <mode>",
			".setmode <feature> <setting> (prev|next)");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 3)
			throw new CmdSyntaxError();
		
		Feature feature = CmdUtils.findFeature(args[0]);
		Setting setting = CmdUtils.findSetting(feature, args[1]);
		EnumSetting<?> enumSetting = getAsEnumSetting(feature, setting);
		setMode(feature, enumSetting, args[2]);
	}
	
	private EnumSetting<?> getAsEnumSetting(Feature feature, Setting setting)
		throws CmdError
	{
		if(!(setting instanceof EnumSetting<?>))
			throw new CmdError(feature.getName() + " " + setting.getName()
				+ " is not a mode setting.");
		
		return (EnumSetting<?>)setting;
	}
	
	private void setMode(Feature feature, EnumSetting<?> setting, String mode)
		throws CmdError
	{
		mode = mode.replace("_", " ").toLowerCase();
		
		switch(mode)
		{
			case "prev":
			setting.selectPrev();
			break;
			
			case "next":
			setting.selectNext();
			break;
			
			default:
			boolean successful = setting.setSelected(mode);
			if(!successful)
				throw new CmdError(
					"A mode named '" + mode + "' in " + feature.getName() + " "
						+ setting.getName() + " could not be found.");
			break;
		}
	}
}
