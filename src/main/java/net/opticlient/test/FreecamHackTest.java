/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.test;

import static net.opticlient.test.OptiClientTestHelper.*;

import java.time.Duration;

import net.opticlient.mixinterface.IKeyBinding;

public enum FreecamHackTest
{
	;
	
	public static void testFreecamHack()
	{
		System.out.println("Testing Freecam hack");
		
		// Enable Freecam with default settings
		runOPTICommand("setcheckbox Freecam tracer off");
		runOPTICommand("t Freecam on");
		takeScreenshot("freecam_default", Duration.ofMillis(100));
		clearChat();
		
		// Press shift to fly down a bit
		submitAndWait(
			mc -> IKeyBinding.get(mc.options.sneakKey).simulatePress(true));
		waitForWorldTicks(5);
		submitAndWait(
			mc -> IKeyBinding.get(mc.options.sneakKey).simulatePress(false));
		takeScreenshot("freecam_down", Duration.ofMillis(300));
		clearChat();
		
		// Tracer
		runOPTICommand("setcheckbox Freecam tracer on");
		takeScreenshot("freecam_tracer", Duration.ofMillis(100));
		clearChat();
		
		// Clean up
		runOPTICommand("setcheckbox Freecam tracer off");
		runOPTICommand("t Freecam off");
		waitForWorldTicks(5);
		clearChat();
	}
}
