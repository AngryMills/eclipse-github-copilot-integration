/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.intellij.psi.PsiElement
 *  org.jetbrains.annotations.NotNull
 */
package com.github.copilot.lang;

import com.intellij.psi.PsiElement;

@FunctionalInterface
public interface IsValidBlockFunction {
	public boolean isValidBlock(PsiElement var1, boolean var2);
}
