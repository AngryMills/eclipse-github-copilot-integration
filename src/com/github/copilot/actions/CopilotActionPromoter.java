/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.intellij.openapi.actionSystem.ActionPromoter
 *  com.intellij.openapi.actionSystem.ActionWithDelegate
 *  com.intellij.openapi.actionSystem.AnAction
 *  com.intellij.openapi.actionSystem.CommonDataKeys
 *  com.intellij.openapi.actionSystem.DataContext
 *  com.intellij.openapi.editor.Editor
 *  com.intellij.openapi.editor.actionSystem.EditorAction
 *  org.jetbrains.annotations.NotNull
 */
package com.github.copilot.actions;

import com.github.copilot.actions.CopilotAction;
import com.github.copilot.actions.CopilotApplyInlaysAction;
import com.github.copilot.actions.CopilotDisposeInlaysAction;
import com.intellij.openapi.actionSystem.ActionPromoter;
import com.intellij.openapi.actionSystem.ActionWithDelegate;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import java.util.ArrayList;
import java.util.List;

public class CopilotActionPromoter implements ActionPromoter {
	public List<AnAction> promote(List<? extends AnAction> actions, DataContext context) {
		Editor editor;
		if (actions == null) {
			throw new IllegalStateException("actions cannot be null!");
		}
		if (context == null) {
			throw new IllegalStateException("context cannot be null!");
		}
		if ((editor = (Editor) CommonDataKeys.EDITOR.getData(context)) == null
				|| !CopilotApplyInlaysAction.isSupported(editor) && !CopilotDisposeInlaysAction.isSupported(editor)) {
			return null;
		}
		if (actions.stream().noneMatch(action -> action instanceof CopilotAction && action instanceof EditorAction)) {
			return null;
		}
		ArrayList<AnAction> result = new ArrayList<AnAction>(actions);
		result.sort((a, b) -> {
			boolean bIsCopilot;
			boolean aIsCopilot = a instanceof CopilotAction && a instanceof EditorAction;
			boolean bl = bIsCopilot = b instanceof CopilotAction && b instanceof EditorAction;
			if (aIsCopilot && bIsCopilot) {
				if (a instanceof CopilotApplyInlaysAction) {
					return -1;
				}
				if (b instanceof CopilotApplyInlaysAction) {
					return -1;
				}
			}
			if (CopilotActionPromoter.isIdeaVimAction(a) || CopilotActionPromoter.isIdeaVimAction(b)) {
				return 0;
			}
			if (aIsCopilot) {
				return -1;
			}
			if (bIsCopilot) {
				return 1;
			}
			return 0;
		});
		return result;
	}

	private static boolean isIdeaVimAction(AnAction action) {
		if (action == null) {
			throw new IllegalStateException("action cannot be null!");
		}
		String packagePrefix = "com.maddyhome.idea.vim";
		if (action.getClass().getName().startsWith(packagePrefix)) {
			return true;
		}
		if (action instanceof ActionWithDelegate) {
			Object delegate = ((ActionWithDelegate) action).getDelegate();
			return delegate.getClass().getName().startsWith(packagePrefix);
		}
		return false;
	}
}
