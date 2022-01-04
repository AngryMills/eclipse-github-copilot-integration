/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.intellij.lang.Language
 *  com.intellij.util.xmlb.annotations.OptionTag
 *  com.intellij.util.xmlb.annotations.Transient
 *  com.intellij.util.xmlb.annotations.XCollection
 *  com.intellij.util.xmlb.annotations.XCollection$Style
 *  javax.annotation.Nullable
 *  org.jetbrains.annotations.NotNull
 */
package com.github.copilot.settings;

import com.github.copilot.openai.OpenAI;
import com.github.copilot.settings.ColorConverter;
import com.github.copilot.settings.ZonedDateTimeConverter;
import com.github.copilot.telemetry.TelemetryData;
import com.github.copilot.telemetry.TelemetryService;
import com.intellij.lang.Language;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Transient;
import com.intellij.util.xmlb.annotations.XCollection;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class CopilotApplicationState {
	@OptionTag(value = "maxTokens")
	public int maxTokens = 500;
	@OptionTag(value = "topP")
	public int topP = 1;
	@OptionTag(value = "multilineCompletionCount")
	public int multilineCompletionCount = 3;
	@OptionTag(value = "temperature")
	public Float temperature = null;
	@OptionTag(value = "copilotTokenExpiry")
	public long copilotTokenExpiresAtSeconds = 0L;
	@OptionTag(value = "signinNotificationShown")
	public boolean signinNotificationShown = false;
	@OptionTag(value = "enableCompletions")
	public boolean enableCompletions = true;
	@XCollection(style = XCollection.Style.v2, propertyElementName = "disabledLanguages", elementTypes = {
			String.class })
	private final Set<String> disabledLanguageIds = new HashSet<String>();
	@OptionTag(value = "inlayTextColor", converter = ColorConverter.class)
	public Color inlayTextColor = null;
	@OptionTag(value = "showIdeCompletions")
	private boolean showIdeCompletions = false;
	@OptionTag(value = "disableHttpCache")
	public transient boolean internalDisableHttpCache = false;
	@OptionTag(value = "checkForUpdate")
	public boolean checkForUpdate = true;
	@OptionTag(value = "telemetryTermsAcceptedDate", converter = ZonedDateTimeConverter.class)
	public ZonedDateTime telemetryAcceptedDate = null;

	@Transient
	public boolean isTelemetryTermsAccepted() {
		ZonedDateTime acceptedDate = this.telemetryAcceptedDate;
		return acceptedDate != null && !acceptedDate.isBefore(OpenAI.LAST_TELEMETRY_TERMS_UPDATE);
	}

	@Transient
	public void setTelemetryTermsAccepted(boolean accepted) {
		this.telemetryAcceptedDate = accepted ? ZonedDateTime.now() : null;
	}

	public boolean isShowIdeCompletions() {
		return this.showIdeCompletions;
	}

	public void setShowIdeCompletions(boolean showIdeCompletions) {
		if (this.showIdeCompletions != showIdeCompletions) {
			this.showIdeCompletions = showIdeCompletions;
			TelemetryData properties = TelemetryData
					.create(Map.of("showIdeCompletions", String.valueOf(showIdeCompletions)));
			TelemetryService.getInstance().track("editor.intellij.showIdeCompletionsChanged", properties);
		}
	}

	public Set<String> getDisabledLanguageIds() {
		Set<String> set = Collections.unmodifiableSet(this.disabledLanguageIds);
		if (set == null) {
			throw new IllegalStateException("set cannot be null!");
		}
		return set;
	}

	public boolean isEnabled(Language language) {
		if (language == null) {
			throw new IllegalStateException("language cannot be null!");
		}
		return !this.disabledLanguageIds.contains(language.getID());
	}

	public void setDisabledLanguageIds(Set<String> languageIds) {
		if (languageIds == null) {
			throw new IllegalStateException("languageIds cannot be null!");
		}
		this.disabledLanguageIds.clear();
		this.disabledLanguageIds.addAll(languageIds);
	}

	public void enableLanguage(Language language) {
		if (language == null) {
			throw new IllegalStateException("language cannot be null!");
		}
		this.disabledLanguageIds.remove(language.getID());
	}

	public void disableLanguage(Language language) {
		if (language == null) {
			throw new IllegalStateException("language cannot be null!");
		}
		this.disabledLanguageIds.add(language.getID());
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof CopilotApplicationState)) {
			return false;
		}
		CopilotApplicationState other = (CopilotApplicationState) o;
		if (!other.canEqual(this)) {
			return false;
		}
		if (this.maxTokens != other.maxTokens) {
			return false;
		}
		if (this.topP != other.topP) {
			return false;
		}
		if (this.multilineCompletionCount != other.multilineCompletionCount) {
			return false;
		}
		if (this.copilotTokenExpiresAtSeconds != other.copilotTokenExpiresAtSeconds) {
			return false;
		}
		if (this.signinNotificationShown != other.signinNotificationShown) {
			return false;
		}
		if (this.enableCompletions != other.enableCompletions) {
			return false;
		}
		if (this.isShowIdeCompletions() != other.isShowIdeCompletions()) {
			return false;
		}
		if (this.checkForUpdate != other.checkForUpdate) {
			return false;
		}
		Float this$temperature = this.temperature;
		Float other$temperature = other.temperature;
		if (this$temperature == null ? other$temperature != null
				: !((Object) this$temperature).equals(other$temperature)) {
			return false;
		}
		Set<String> this$disabledLanguageIds = this.getDisabledLanguageIds();
		Set<String> other$disabledLanguageIds = other.getDisabledLanguageIds();
		if (this$disabledLanguageIds == null ? other$disabledLanguageIds != null
				: !((Object) this$disabledLanguageIds).equals(other$disabledLanguageIds)) {
			return false;
		}
		Color this$inlayTextColor = this.inlayTextColor;
		Color other$inlayTextColor = other.inlayTextColor;
		if (this$inlayTextColor == null ? other$inlayTextColor != null
				: !((Object) this$inlayTextColor).equals(other$inlayTextColor)) {
			return false;
		}
		ZonedDateTime this$telemetryAcceptedDate = this.telemetryAcceptedDate;
		ZonedDateTime other$telemetryAcceptedDate = other.telemetryAcceptedDate;
		return !(this$telemetryAcceptedDate == null ? other$telemetryAcceptedDate != null
				: !((Object) this$telemetryAcceptedDate).equals(other$telemetryAcceptedDate));
	}

	protected boolean canEqual(Object other) {
		return other instanceof CopilotApplicationState;
	}

	public int hashCode() {
		int PRIME = 59;
		int result = 1;
		result = result * 59 + this.maxTokens;
		result = result * 59 + this.topP;
		result = result * 59 + this.multilineCompletionCount;
		long $copilotTokenExpiresAtSeconds = this.copilotTokenExpiresAtSeconds;
		result = result * 59 + (int) ($copilotTokenExpiresAtSeconds >>> 32 ^ $copilotTokenExpiresAtSeconds);
		result = result * 59 + (this.signinNotificationShown ? 79 : 97);
		result = result * 59 + (this.enableCompletions ? 79 : 97);
		result = result * 59 + (this.isShowIdeCompletions() ? 79 : 97);
		result = result * 59 + (this.checkForUpdate ? 79 : 97);
		Float $temperature = this.temperature;
		result = result * 59 + ($temperature == null ? 43 : ((Object) $temperature).hashCode());
		Set<String> $disabledLanguageIds = this.getDisabledLanguageIds();
		result = result * 59 + ($disabledLanguageIds == null ? 43 : ((Object) $disabledLanguageIds).hashCode());
		Color $inlayTextColor = this.inlayTextColor;
		result = result * 59 + ($inlayTextColor == null ? 43 : ((Object) $inlayTextColor).hashCode());
		ZonedDateTime $telemetryAcceptedDate = this.telemetryAcceptedDate;
		result = result * 59 + ($telemetryAcceptedDate == null ? 43 : ((Object) $telemetryAcceptedDate).hashCode());
		return result;
	}

}
