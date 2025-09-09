/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.commands;

import net.opticlient.OptiClient;
import net.opticlient.command.CmdException;
import net.opticlient.command.CmdSyntaxError;
import net.opticlient.command.Command;
import net.opticlient.hack.Hack;
import net.opticlient.other_feature.OtherFeature;
import net.opticlient.util.ChatUtils;

public final class FeaturesCmd extends Command
{
	public FeaturesCmd()
	{
		super("features",
			"Shows the number of features and some other\n" + "statistics.",
			".features");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 0)
			throw new CmdSyntaxError();
		
		if(OptiClient.VERSION.startsWith("7.0pre"))
			ChatUtils.warning(
				"This is just a pre-release! It doesn't (yet) have all of the features of OPTI 7.0! See download page for details.");
		
		int hax = OPTI.getHax().countHax();
		int cmds = OPTI.getCmds().countCmds();
		int otfs = OPTI.getOtfs().countOtfs();
		int all = hax + cmds + otfs;
		
		ChatUtils.message("All features: " + all);
		ChatUtils.message("Hacks: " + hax);
		ChatUtils.message("Commands: " + cmds);
		ChatUtils.message("Other features: " + otfs);
		
		int settings = 0;
		for(Hack hack : OPTI.getHax().getAllHax())
			settings += hack.getSettings().size();
		for(Command cmd : OPTI.getCmds().getAllCmds())
			settings += cmd.getSettings().size();
		for(OtherFeature otf : OPTI.getOtfs().getAllOtfs())
			settings += otf.getSettings().size();
		
		ChatUtils.message("Settings: " + settings);
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Show Statistics";
	}
	
	@Override
	public void doPrimaryAction()
	{
		OPTI.getCmdProcessor().process("features");
	}
}
