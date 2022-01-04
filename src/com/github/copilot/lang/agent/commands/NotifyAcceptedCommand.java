/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  org.jetbrains.annotations.NotNull
 */
package com.github.copilot.lang.agent.commands;

import com.github.copilot.lang.agent.rpc.JsonRpcCommand;
import com.google.gson.annotations.SerializedName;

public final class NotifyAcceptedCommand implements JsonRpcCommand<String> {
	@SerializedName(value = "uuid")
	private final String uuid;

	@Override
	public String getCommandName() {
		return "notifyAccepted";
	}

	@Override
	public Class<String> getResponseType() {
		return String.class;
	}

	public String getUuid() {
		String string = this.uuid;
		if (string == null) {
			throw new IllegalStateException("string cannot be null!");
		}
		return string;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof NotifyAcceptedCommand)) {
			return false;
		}
		NotifyAcceptedCommand other = (NotifyAcceptedCommand) o;
		String this$uuid = this.getUuid();
		String other$uuid = other.getUuid();
		return !(this$uuid == null ? other$uuid != null : !this$uuid.equals(other$uuid));
	}

	public int hashCode() {
		int result = 1;
		String $uuid = this.getUuid();
		result = result * 59 + ($uuid == null ? 43 : $uuid.hashCode());
		return result;
	}

	public String toString() {
		return "NotifyAcceptedCommand(uuid=" + this.getUuid() + ")";
	}

	public NotifyAcceptedCommand(String uuid) {
		if (uuid == null) {
			throw new IllegalStateException("uuid cannot be null!");
		}
		this.uuid = uuid;
	}

}
