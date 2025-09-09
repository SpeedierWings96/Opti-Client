/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.opticlient.hacks;

import net.opticlient.Category;
import net.opticlient.SearchTags;
import net.opticlient.events.ChatInputListener;
import net.opticlient.events.ChatOutputListener;
import net.opticlient.hack.Hack;
import net.opticlient.hacks.chattranslator.FilterOwnMessagesSetting;
import net.opticlient.hacks.chattranslator.GoogleTranslate;
import net.opticlient.hacks.chattranslator.LanguageSetting;
import net.opticlient.hacks.chattranslator.LanguageSetting.Language;
import net.opticlient.hacks.chattranslator.WhatToTranslateSetting;
import net.opticlient.settings.CheckboxSetting;
import net.opticlient.util.ChatUtils;

@SearchTags({"chat translator", "ChatTranslate", "chat translate",
	"ChatTranslation", "chat translation", "AutoTranslate", "auto translate",
	"AutoTranslator", "auto translator", "AutoTranslation", "auto translation",
	"GoogleTranslate", "google translate", "GoogleTranslator",
	"google translator", "GoogleTranslation", "google translation"})
public final class ChatTranslatorHack extends Hack
	implements ChatInputListener, ChatOutputListener
{
	private final WhatToTranslateSetting whatToTranslate =
		new WhatToTranslateSetting();
	
	private final LanguageSetting playerLanguage =
		LanguageSetting.withoutAutoDetect("Your language",
			"description.OPTI.setting.chattranslator.your_language",
			Language.ENGLISH);
	
	private final LanguageSetting otherLanguage =
		LanguageSetting.withoutAutoDetect("Other language",
			"description.OPTI.setting.chattranslator.other_language",
			Language.CHINESE_SIMPLIFIED);
	
	private final CheckboxSetting autoDetectReceived =
		new CheckboxSetting("Detect received language",
			"description.OPTI.setting.chattranslator.detect_received_language",
			true);
	
	private final CheckboxSetting autoDetectSent = new CheckboxSetting(
		"Detect sent language",
		"description.OPTI.setting.chattranslator.detect_sent_language", true);
	
	private final FilterOwnMessagesSetting filterOwnMessages =
		new FilterOwnMessagesSetting();
	
	public ChatTranslatorHack()
	{
		super("ChatTranslator");
		setCategory(Category.CHAT);
		addSetting(whatToTranslate);
		addSetting(playerLanguage);
		addSetting(otherLanguage);
		addSetting(autoDetectReceived);
		addSetting(autoDetectSent);
		addSetting(filterOwnMessages);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(ChatInputListener.class, this);
		EVENTS.add(ChatOutputListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(ChatInputListener.class, this);
		EVENTS.remove(ChatOutputListener.class, this);
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		if(!whatToTranslate.includesReceived())
			return;
		
		String message = event.getComponent().getString();
		Language fromLang = autoDetectReceived.isChecked()
			? Language.AUTO_DETECT : otherLanguage.getSelected();
		Language toLang = playerLanguage.getSelected();
		
		if(message.startsWith(ChatUtils.opti_PREFIX)
			|| message.startsWith(toLang.getPrefix()))
			return;
		
		if(filterOwnMessages.isChecked()
			&& filterOwnMessages.isOwnMessage(message))
			return;
		
		Thread.ofVirtual().name("ChatTranslator")
			.uncaughtExceptionHandler((t, e) -> e.printStackTrace())
			.start(() -> showTranslated(message, fromLang, toLang));
	}
	
	private void showTranslated(String message, Language fromLang,
		Language toLang)
	{
		String translated = GoogleTranslate.translate(message,
			fromLang.getValue(), toLang.getValue());
		
		if(translated != null)
			MC.inGameHud.getChatHud().addMessage(toLang.prefixText(translated));
	}
	
	@Override
	public void onSentMessage(ChatOutputEvent event)
	{
		if(!whatToTranslate.includesSent())
			return;
		
		String message = event.getMessage();
		Language fromLang = autoDetectSent.isChecked() ? Language.AUTO_DETECT
			: playerLanguage.getSelected();
		Language toLang = otherLanguage.getSelected();
		
		if(message.startsWith("/") || message.startsWith("."))
			return;
		
		event.cancel();
		
		Thread.ofVirtual().name("ChatTranslator")
			.uncaughtExceptionHandler((t, e) -> e.printStackTrace())
			.start(() -> sendTranslated(message, fromLang, toLang));
	}
	
	private void sendTranslated(String message, Language fromLang,
		Language toLang)
	{
		String translated = GoogleTranslate.translate(message,
			fromLang.getValue(), toLang.getValue());
		
		if(translated == null)
			translated = message;
		
		MC.getNetworkHandler().sendChatMessage(translated);
	}
}
