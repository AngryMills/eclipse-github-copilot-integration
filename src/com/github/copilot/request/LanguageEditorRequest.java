/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.intellij.psi.PsiFile
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.github.copilot.request;

import com.github.copilot.lang.LanguageSupport;
import com.github.copilot.request.EditorRequest;
import com.intellij.psi.PsiFile;

public interface LanguageEditorRequest extends EditorRequest {
	public LanguageSupport getLanguage();

	public PsiFile createFile();
}
