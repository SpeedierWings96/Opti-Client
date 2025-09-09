/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.opticlient.Category;
import net.opticlient.DontBlock;
import net.opticlient.Feature;
import net.opticlient.SearchTags;
import net.opticlient.TooManyHaxFile;
import net.opticlient.hack.Hack;
import net.opticlient.util.json.JsonException;

@SearchTags({"too many hax", "TooManyHacks", "too many hacks", "YesCheat+",
	"YesCheatPlus", "yes cheat plus"})
@DontBlock
public final class TooManyHaxHack extends Hack
{
	private final ArrayList<Feature> blockedFeatures = new ArrayList<>();
	private final Path profilesFolder;
	private final TooManyHaxFile file;
	
	public TooManyHaxHack()
	{
		super("TooManyHax");
		setCategory(Category.OTHER);
		
		Path OPTIFolder = OPTI.getOPTIFolder();
		profilesFolder = OPTIFolder.resolve("toomanyhax");
		
		Path filePath = OPTIFolder.resolve("toomanyhax.json");
		file = new TooManyHaxFile(filePath, blockedFeatures);
	}
	
	public void loadBlockedHacksFile()
	{
		file.load();
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + " [" + blockedFeatures.size() + " blocked]";
	}
	
	@Override
	protected void onEnable()
	{
		disableBlockedHacks();
	}
	
	private void disableBlockedHacks()
	{
		for(Feature feature : blockedFeatures)
		{
			if(!(feature instanceof Hack))
				continue;
			
			((Hack)feature).setEnabled(false);
		}
	}
	
	public ArrayList<Path> listProfiles()
	{
		if(!Files.isDirectory(profilesFolder))
			return new ArrayList<>();
		
		try(Stream<Path> files = Files.list(profilesFolder))
		{
			return files.filter(Files::isRegularFile)
				.collect(Collectors.toCollection(ArrayList::new));
			
		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void loadProfile(String fileName) throws IOException, JsonException
	{
		file.loadProfile(profilesFolder.resolve(fileName));
		disableBlockedHacks();
	}
	
	public void saveProfile(String fileName) throws IOException, JsonException
	{
		file.saveProfile(profilesFolder.resolve(fileName));
	}
	
	public boolean isBlocked(Feature feature)
	{
		return blockedFeatures.contains(feature);
	}
	
	public void setBlocked(Feature feature, boolean blocked)
	{
		if(blocked)
		{
			if(!feature.isSafeToBlock())
				throw new IllegalArgumentException();
			
			blockedFeatures.add(feature);
			blockedFeatures
				.sort(Comparator.comparing(f -> f.getName().toLowerCase()));
			
		}else
			blockedFeatures.remove(feature);
		
		file.save();
	}
	
	public void blockAll()
	{
		blockedFeatures.clear();
		
		ArrayList<Feature> features = new ArrayList<>();
		features.addAll(OPTI.getHax().getAllHax());
		features.addAll(OPTI.getCmds().getAllCmds());
		features.addAll(OPTI.getOtfs().getAllOtfs());
		
		for(Feature feature : features)
		{
			if(!feature.isSafeToBlock())
				continue;
			
			blockedFeatures.add(feature);
		}
		
		blockedFeatures
			.sort(Comparator.comparing(f -> f.getName().toLowerCase()));
		
		file.save();
	}
	
	public void unblockAll()
	{
		blockedFeatures.clear();
		file.save();
	}
	
	public List<Feature> getBlockedFeatures()
	{
		return Collections.unmodifiableList(blockedFeatures);
	}
}
