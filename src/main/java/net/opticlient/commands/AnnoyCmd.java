/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.commands;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.network.ClientPlayerEntity;
import net.opticlient.command.CmdError;
import net.opticlient.command.CmdException;
import net.opticlient.command.CmdSyntaxError;
import net.opticlient.command.Command;
import net.opticlient.events.ChatInputListener;
import net.opticlient.settings.CheckboxSetting;
import net.opticlient.util.ChatUtils;

public final class AnnoyCmd extends Command implements ChatInputListener
{
	private final CheckboxSetting rcMode = new CheckboxSetting("RC mode",
		"Remote control mode. Re-enables a bug that allows .annoy to run OPTI"
			+ " commands. Not recommended for security reasons, but until we have a"
			+ " proper remote control feature, this is at least better than nothing.",
		false);
	
	private boolean enabled;
	private String target;
	
	public AnnoyCmd()
	{
		super("annoy", "Annoys a player by repeating everything they say.",
			".annoy <player>", "Turn off: .annoy");
		addSetting(rcMode);
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length > 0)
		{
			if(enabled)
				disable();
			
			enable(args);
			
		}else
		{
			if(!enabled)
				throw new CmdError(".annoy is already turned off.");
			
			disable();
		}
	}
	
	private void enable(String[] args) throws CmdException
	{
		if(args.length < 1)
			throw new CmdSyntaxError();
		
		target = String.join(" ", args);
		ChatUtils.message("Now annoying " + target + ".");
		
		ClientPlayerEntity player = MC.player;
		if(player != null && target.equals(player.getName().getString()))
			ChatUtils.warning("Annoying yourself is a bad idea!");
		
		EVENTS.add(ChatInputListener.class, this);
		enabled = true;
	}
	
	private void disable() throws CmdException
	{
		EVENTS.remove(ChatInputListener.class, this);
		
		if(target != null)
		{
			ChatUtils.message("No longer annoying " + target + ".");
			target = null;
		}
		
		enabled = false;
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		String message = event.getComponent().getString();
		if(message.startsWith(ChatUtils.opti_PREFIX))
			return;
		
		String prefix1 = target + ">";
		if(message.contains("<" + prefix1) || message.contains(prefix1))
		{
			repeat(message, prefix1);
			return;
		}
		
		String prefix2 = target + ":";
		if(message.contains("] " + prefix2) || message.contains("]" + prefix2))
			repeat(message, prefix2);
	}
	
	private void repeat(String message, String prefix)
	{
		int beginIndex = message.indexOf(prefix) + prefix.length();
		String repeated = message.substring(beginIndex).trim();
		repeated = StringUtils.normalizeSpace(repeated);
		
		if(rcMode.isChecked() && repeated.startsWith("."))
			OPTI.getCmdProcessor().process(repeated.substring(1));
		else
			MC.getNetworkHandler().sendChatMessage(repeated);
	}
}
