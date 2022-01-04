/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.masecla.copilot.extra.Logger
 *  com.intellij.openapi.util.Pair
 *  com.intellij.openapi.util.TextRange
 *  com.intellij.openapi.util.text.StringUtil
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.github.copilot.completions;

import com.github.copilot.completions.CopilotCompletion;
import com.github.copilot.completions.CopilotCompletionType;
import com.github.copilot.completions.CopilotEditorInlay;
import com.github.copilot.completions.CopilotInlayList;
import com.github.copilot.completions.DefaultCopilotEditorInlay;
import com.github.copilot.completions.DefaultInlayList;
import com.github.copilot.lang.CommonLanguageSupport;
import com.github.copilot.request.EditorRequest;
import com.github.copilot.request.LineInfo;
import com.github.copilot.util.CopilotStringUtil;
import me.masecla.copilot.extra.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompletionUtil {
	private static final Logger LOG = Logger.getInstance(CompletionUtil.class);

	static List<CopilotInlayList> createEditorCompletions(EditorRequest request, List<CopilotCompletion> items) {
		if (request == null) {
			throw new IllegalStateException("request cannot be null!");
		}
		if (items == null) {
			throw new IllegalStateException("items cannot be null!");
		}
		return items.stream().map(item -> CompletionUtil.createEditorCompletion(request, item, true))
				.collect(Collectors.toList());
	}

	public static CopilotInlayList createEditorCompletion(EditorRequest request, CopilotCompletion copilotCompletion,
			boolean dropLinePrefix) {
		boolean replaceLinePrefix;
		ArrayList<String> lines;
		if (request == null) {
			throw new IllegalStateException("request cannot be null!");
		}
		if (copilotCompletion == null) {
			throw new IllegalStateException("copilotCompletion cannot be null!");
		}
		if ((lines = new ArrayList<String>(copilotCompletion.getCompletion())).isEmpty()
				|| lines.size() == 1 && (lines.get(0).isEmpty() || lines.get(0).equals("\n"))) {
			LOG.debug("ignoring empty completion: " + request);
			return null;
		}
		CompletionUtil.dropOverlappingTrailingLines(lines, request.getDocumentContent(), request.getOffset());
		if (lines.isEmpty()) {
			return null;
		}
		String replacementText = CompletionUtil.createReplacementText(request.getLineInfo(), lines);
		boolean bl = replaceLinePrefix = dropLinePrefix
				&& CompletionUtil.adjustWhitespace(lines, request.getLineInfo());
		if (lines.isEmpty()) {
			return null;
		}
		return new DefaultInlayList(copilotCompletion,
				CompletionUtil.createReplacementRange(request, replaceLinePrefix), replacementText,
				CompletionUtil.createEditorInlays(request, lines));
	}

	private static String createReplacementText(LineInfo lineInfo, List<String> lines) {
		String ws;
		if (lineInfo == null) {
			throw new IllegalStateException("lineInfo cannot be null!");
		}
		String text = StringUtil.join(lines, (String) "\n");
		if (!lineInfo.isBlankLine() && text.startsWith(ws = lineInfo.getWhitespaceBeforeCursor())) {
			String string = text.substring(ws.length());
			if (string == null) {
				throw new IllegalStateException("string cannot be null!");
			}
			return string;
		}
		String string = text;
		if (string == null) {
			throw new IllegalStateException("string cannot be null!");
		}
		return string;
	}

	private static TextRange createReplacementRange(EditorRequest request, boolean replaceLinePrefix) {
		if (request == null) {
			throw new IllegalStateException("request cannot be null!");
		}
		LineInfo lineInfo = request.getLineInfo();
		int startOffset = replaceLinePrefix ? lineInfo.getLineStartOffset() : request.getOffset();
		int endOffset = CompletionUtil.isReplaceLineSuffix(request)
				? lineInfo.getLineEndOffset() - CopilotStringUtil.trailingWhitespaceLength(lineInfo.getLineSuffix())
				: request.getOffset();
		TextRange textRange = TextRange.create((int) startOffset, (int) endOffset);
		if (textRange == null) {
			throw new IllegalStateException("textRange cannot be null!");
		}
		return textRange;
	}

	private static boolean isReplaceLineSuffix(EditorRequest request) {
		String lineSuffix;
		if (request == null) {
			throw new IllegalStateException("request cannot be null!");
		}
		return CopilotStringUtil.isSpacesOrTabs(lineSuffix = request.getLineInfo().getLineSuffix(), false)
				|| CommonLanguageSupport.isValidMiddleOfTheLinePosition(lineSuffix);
	}

	private static List<CopilotEditorInlay> createEditorInlays(EditorRequest request, List<String> lines) {
		if (request == null) {
			throw new IllegalStateException("request cannot be null!");
		}
		if (lines == null) {
			throw new IllegalStateException("lines cannot be null!");
		}
		ArrayList<CopilotEditorInlay> inlays = new ArrayList<CopilotEditorInlay>();
		int offset = request.getOffset();
		if (lines.size() > 1 && request.getLineInfo().isBlankLine() && lines.get(0).isEmpty()) {
			inlays.add(
					new DefaultCopilotEditorInlay(CopilotCompletionType.Block, offset, lines.subList(1, lines.size())));
		} else {
			String completionLine;
			String editorLineSuffix = request.getLineInfo().getLineSuffix();
			List<Pair<Integer, String>> diffs = CopilotStringUtil.createDiffInlays(editorLineSuffix,
					completionLine = lines.get(0));
			if (diffs != null && !diffs.isEmpty()) {
				for (Pair<Integer, String> diff : diffs) {
					Integer delta = (Integer) diff.getFirst();
					inlays.add(new DefaultCopilotEditorInlay(CopilotCompletionType.Inline, offset + delta,
							List.of((String) diff.second)));
				}
			}
			if (lines.size() > 1) {
				inlays.add(new DefaultCopilotEditorInlay(CopilotCompletionType.Block, offset,
						lines.subList(1, lines.size())));
			}
		}
		ArrayList<CopilotEditorInlay> arrayList = inlays;
		if (arrayList == null) {
			throw new IllegalStateException("arrayList cannot be null!");
		}
		return arrayList;
	}

	private static void dropOverlappingTrailingLines(List<String> lines, String editorContent, int offset) {
		if (lines == null) {
			throw new IllegalStateException("lines cannot be null!");
		}
		if (editorContent == null) {
			throw new IllegalStateException("editorContent cannot be null!");
		}
		if (offset < editorContent.length() && editorContent.charAt(offset) == '\n') {
			++offset;
		}
		if (offset >= editorContent.length()) {
			return;
		}
		List<String> editorLines = CopilotStringUtil.getNextLines(editorContent, offset, lines.size());
		int overlap = CopilotStringUtil.findOverlappingLines(lines, editorLines);
		for (int i = 0; i < overlap; ++i) {
			lines.remove(lines.size() - 1);
		}
	}

	private static boolean adjustWhitespace(List<String> completionLines, LineInfo lineInfo) {
		String firstLine;
		if (completionLines == null) {
			throw new IllegalStateException("completionLines cannot be null!");
		}
		if (lineInfo == null) {
			throw new IllegalStateException("lineInfo cannot be null!");
		}
		String editorWhitespacePrefix = lineInfo.getWhitespaceBeforeCursor();
		if (completionLines.isEmpty() || editorWhitespacePrefix.isEmpty()) {
			return false;
		}
		boolean isEditorEmptyLine = lineInfo.isBlankLine();
		boolean replacePrefixInEditor = false;
		String firstLineFixed = firstLine = completionLines.get(0);
		if (firstLine.startsWith(editorWhitespacePrefix)) {
			firstLineFixed = firstLine.substring(editorWhitespacePrefix.length());
			replacePrefixInEditor = isEditorEmptyLine;
		} else if (isEditorEmptyLine) {
			String lineLeadingWhitespace = CopilotStringUtil.leadingWhitespace(firstLine);
			firstLineFixed = firstLine.substring(lineLeadingWhitespace.length());
			replacePrefixInEditor = !firstLine.isEmpty() && !lineLeadingWhitespace.startsWith(editorWhitespacePrefix);
		}
		completionLines.set(0, firstLineFixed);
		return replacePrefixInEditor;
	}

	public static CopilotCompletion apiChoiceWithoutPrefix(CopilotCompletion apiChoice, String prefix) {
		if (apiChoice == null) {
			throw new IllegalStateException("apiChoice cannot be null!");
		}
		if (prefix == null) {
			throw new IllegalStateException("prefix cannot be null!");
		}
		if (prefix.isEmpty()) {
			return apiChoice;
		}
		boolean ignoreFirstWhiteSpace = CopilotStringUtil.leadingWhitespace(prefix).isEmpty();
		List<String> completion = apiChoice.getCompletion();
		String remainingPrefix = prefix;
		int completionSize = completion.size();
		for (int i = 0; i < completionSize; ++i) {
			String line = completion.get(i);
			int prefixLineEnd = remainingPrefix.indexOf(10);
			String prefixLine = remainingPrefix.substring(0,
					prefixLineEnd == -1 ? remainingPrefix.length() : prefixLineEnd);
			if (ignoreFirstWhiteSpace && i == 0) {
				boolean ok;
				String trimmedLine = CopilotStringUtil.stripLeading(line);
				boolean bl = ok = prefixLineEnd == -1 ? trimmedLine.startsWith(prefixLine)
						: trimmedLine.equals(prefixLine);
				if (!ok) {
					return null;
				}
			} else {
				boolean ok;
				boolean bl = ok = prefixLineEnd == -1 ? line.startsWith(prefixLine) : line.equals(prefixLine);
				if (!ok) {
					return null;
				}
			}
			if (prefixLineEnd == -1) {
				ArrayList<String> newCompletions = new ArrayList<String>(completionSize - i);
				int droppedWhitespace = ignoreFirstWhiteSpace && i == 0
						? CopilotStringUtil.leadingWhitespace(line).length()
						: 0;
				newCompletions.add(line.substring(droppedWhitespace + prefixLine.length()));
				if (i + 1 < completionSize) {
					newCompletions.addAll(completion.subList(i + 1, completionSize));
				}
				return apiChoice.withCompletion(newCompletions);
			}
			remainingPrefix = remainingPrefix.substring(prefixLineEnd + 1);
		}
		return null;
	}

}
