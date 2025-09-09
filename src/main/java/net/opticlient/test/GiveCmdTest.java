/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.test;

import static net.opticlient.test.OptiClientTestHelper.*;

import net.minecraft.item.Items;

public enum GiveCmdTest
{
	;
	
	public static void testGiveCmd()
	{
		System.out.println("Testing .give command");
		runOPTICommand("give diamond");
		waitForWorldTicks(1);
		assertOneItemInSlot(0, Items.DIAMOND);
		
		// Clean up
		runChatCommand("clear");
		clearChat();
	}
}
