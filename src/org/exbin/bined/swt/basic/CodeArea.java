/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.bined.swt.basic;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.exbin.bined.BasicCodeAreaSection;
import org.exbin.bined.BasicCodeAreaZone;
import org.exbin.bined.CaretMovedListener;
import org.exbin.bined.CaretPosition;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeAreaViewMode;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.EditationMode;
import org.exbin.bined.EditationModeChangedListener;
import org.exbin.bined.PositionOverflowMode;
import org.exbin.bined.ScrollBarVisibility;
import org.exbin.bined.ScrollingListener;
import org.exbin.bined.SelectionChangedListener;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.basic.BasicBackgroundPaintMode;
import org.exbin.bined.basic.CodeAreaScrollPosition;
import org.exbin.bined.basic.HorizontalScrollUnit;
import org.exbin.bined.basic.MovementDirection;
import org.exbin.bined.basic.ScrollingDirection;
import org.exbin.bined.basic.VerticalScrollUnit;
import org.exbin.bined.swt.CodeAreaCommandHandler;
import org.exbin.bined.swt.CodeAreaCore;
import org.exbin.bined.swt.CodeAreaPainter;
import org.exbin.bined.swt.CodeAreaSwtControl;

/**
 * Code area component default code area.
 *
 * @version 0.2.0 2018/09/01
 * @author ExBin Project (https://exbin.org)
 */
public class CodeArea extends CodeAreaCore implements DefaultCodeArea, CodeAreaSwtControl {

    @Nonnull
    private CodeAreaPainter painter;

    @Nonnull
    private final DefaultCodeAreaCaret caret;
    @Nonnull
    private final SelectionRange selection = new SelectionRange();
    @Nonnull
    private final CodeAreaScrollPosition scrollPosition = new CodeAreaScrollPosition();

    @Nonnull
    private Charset charset = Charset.defaultCharset();
    private boolean handleClipboard = true;

    @Nonnull
    private EditationMode editationMode = EditationMode.OVERWRITE;
    @Nonnull
    private CodeAreaViewMode viewMode = CodeAreaViewMode.DUAL;
    @Nullable
    private Font font;
    @Nonnull
    private BasicBackgroundPaintMode borderPaintMode = BasicBackgroundPaintMode.STRIPED;
    @Nonnull
    private CodeType codeType = CodeType.HEXADECIMAL;
    private int rowPositionNumberLength = 0;
    @Nonnull
    private CodeCharactersCase codeCharactersCase = CodeCharactersCase.UPPER;
    private boolean showMirrorCursor = true;
    @Nonnull
    private RowWrappingMode lineWrapping = RowWrappingMode.NO_WRAPPING;
    private int wrappingBytesGroupSize = 0;
    private int maxBytesPerLine = 16;

    @Nonnull
    private ScrollBarVisibility verticalScrollBarVisibility = ScrollBarVisibility.IF_NEEDED;
    @Nonnull
    private VerticalScrollUnit verticalScrollUnit = VerticalScrollUnit.ROW;
    @Nonnull
    private ScrollBarVisibility horizontalScrollBarVisibility = ScrollBarVisibility.IF_NEEDED;
    @Nonnull
    private HorizontalScrollUnit horizontalScrollUnit = HorizontalScrollUnit.PIXEL;

    private final List<CaretMovedListener> caretMovedListeners = new ArrayList<>();
    private final List<ScrollingListener> scrollingListeners = new ArrayList<>();
    private final List<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();
    private final List<EditationModeChangedListener> editationModeChangedListeners = new ArrayList<>();

    /**
     * Creates new instance with default command handler and painter.
     *
     * @param parent parent component
     * @param style style
     */
    public CodeArea(@Nullable Composite parent, int style) {
        super(parent, style, DefaultCodeAreaCommandHandler.createDefaultCodeAreaCommandHandlerFactory());

        caret = new DefaultCodeAreaCaret(this);
        painter = new DefaultCodeAreaPainter(this);
        init();
    }

    /**
     * Creates new instance with provided command handler factory method.
     *
     * @param parent parent component
     * @param style style
     * @param commandHandlerFactory command handler or null for default handler
     */
    public CodeArea(@Nullable Composite parent, int style, @Nullable CodeAreaCommandHandler.CodeAreaCommandHandlerFactory commandHandlerFactory) {
        super(parent, style, commandHandlerFactory);

        caret = new DefaultCodeAreaCaret(this);
        painter = new DefaultCodeAreaPainter(this);
        init();
    }

    private void init() {
        addPaintListener((PaintEvent paintEvent) -> {
            GC g = paintEvent.gc;
            if (g == null) {
                return;
            }

            painter.paintComponent(g);
        });
    }

    @Nonnull
    public CodeAreaPainter getPainter() {
        return painter;
    }

    public void setPainter(@Nonnull CodeAreaPainter painter) {
        CodeAreaUtils.requireNonNull(painter);

        this.painter = painter;
        repaint();
    }

    @Override
    public void paintComponent(@Nonnull GC g) {
        painter.paintComponent(g);
    }

    @Nonnull
    @Override
    public DefaultCodeAreaCaret getCaret() {
        return caret;
    }

    @Override
    public boolean isShowMirrorCursor() {
        return showMirrorCursor;
    }

    @Override
    public void setShowMirrorCursor(boolean showMirrorCursor) {
        this.showMirrorCursor = showMirrorCursor;
        repaint();
    }

    @Override
    public int getRowPositionNumberLength() {
        return rowPositionNumberLength;
    }

    @Override
    public void setRowPositionNumberLength(int rowPositionNumberLength) {
        this.rowPositionNumberLength = rowPositionNumberLength;
        reset();
        repaint();
    }

    public boolean isInitialized() {
        return painter.isInitialized();
    }

    public long getDataPosition() {
        return caret.getDataPosition();
    }

    public int getCodeOffset() {
        return caret.getCodeOffset();
    }

    public int getActiveSection() {
        return caret.getSection();
    }

    @Nonnull
    public CaretPosition getCaretPosition() {
        return caret.getCaretPosition();
    }

    public void setCaretPosition(@Nonnull CaretPosition caretPosition) {
        caret.setCaretPosition(caretPosition);
        notifyCaretMoved();
    }

    public void setCaretPosition(long dataPosition) {
        caret.setCaretPosition(dataPosition);
        notifyCaretMoved();
    }

    public void setCaretPosition(long dataPosition, int codeOffset) {
        caret.setCaretPosition(dataPosition, codeOffset);
        notifyCaretMoved();
    }

    @Override
    public int getMouseCursorShape(int positionX, int positionY) {
        return painter.getMouseCursorShape(positionX, positionY);
    }

    @Override
    public BasicCodeAreaZone getPositionZone(int positionX, int positionY) {
        return painter.getPositionZone(positionX, positionY);
    }

    @Nonnull
    @Override
    public CodeCharactersCase getCodeCharactersCase() {
        return codeCharactersCase;
    }

    @Override
    public void setCodeCharactersCase(@Nonnull CodeCharactersCase codeCharactersCase) {
        this.codeCharactersCase = codeCharactersCase;
        repaint();
    }

    @Override
    public void resetColors() {
        painter.resetColors();
    }

    @Nonnull
    @Override
    public CodeAreaViewMode getViewMode() {
        return viewMode;
    }

    @Override
    public void setViewMode(@Nonnull CodeAreaViewMode viewMode) {
        if (viewMode != this.viewMode) {
            this.viewMode = viewMode;
            switch (viewMode) {
                case CODE_MATRIX:
                    getCaret().setSection(BasicCodeAreaSection.CODE_MATRIX.getSection());
                    reset();
                    notifyCaretMoved();
                    break;
                case TEXT_PREVIEW:
                    getCaret().setSection(BasicCodeAreaSection.TEXT_PREVIEW.getSection());
                    reset();
                    notifyCaretMoved();
                    break;
                default:
                    reset();
                    break;
            }
            repaint();
        }
    }

    @Override
    @Nonnull
    public CodeType getCodeType() {
        return codeType;
    }

    @Override
    public void setCodeType(@Nonnull CodeType codeType) {
        this.codeType = codeType;
        painter.reset();
        repaint();
    }

    @Override
    public void revealCursor() {
        revealPosition(caret.getCaretPosition());
    }

    @Override
    public void revealPosition(@Nonnull CaretPosition caretPosition) {
        if (!isInitialized()) {
            // Silently ignore if painter is not yet initialized
            return;
        }

        CodeAreaScrollPosition revealScrollPosition = painter.computeRevealScrollPosition(caretPosition);
        if (revealScrollPosition != null) {
            setScrollPosition(revealScrollPosition);
            resetPainter();
            updateScrollBars();
            notifyScrolled();
        }
    }

    public void revealPosition(long dataPosition, int dataOffset, int section) {
        revealPosition(new CodeAreaCaretPosition(dataPosition, dataOffset, section));
    }

    @Override
    public void centerOnCursor() {
        centerOnPosition(caret.getCaretPosition());
    }

    @Override
    public void centerOnPosition(@Nonnull CaretPosition caretPosition) {
        if (!isInitialized()) {
            // Silently ignore if painter is not yet initialized
            return;
        }

        CodeAreaScrollPosition centerOnScrollPosition = painter.computeCenterOnScrollPosition(caretPosition);
        if (centerOnScrollPosition != null) {
            setScrollPosition(centerOnScrollPosition);
            resetPainter();
            updateScrollBars();
            notifyScrolled();
        }
    }

    public void centerOnPosition(long dataPosition, int dataOffset, int section) {
        centerOnPosition(new CodeAreaCaretPosition(dataPosition, dataOffset, section));
    }

    @Nullable
    @Override
    public CaretPosition mousePositionToClosestCaretPosition(int positionX, int positionY, @Nonnull PositionOverflowMode overlowMode) {
        return painter.mousePositionToClosestCaretPosition(positionX, positionY, overlowMode);
    }

    @Nonnull
    @Override
    public CaretPosition computeMovePosition(@Nonnull CaretPosition position, @Nonnull MovementDirection direction) {
        return painter.computeMovePosition(position, direction);
    }

    @Nonnull
    @Override
    public CodeAreaScrollPosition computeScrolling(@Nonnull CodeAreaScrollPosition startPosition, @Nonnull ScrollingDirection scrollingShift) {
        return painter.computeScrolling(startPosition, scrollingShift);
    }

    @Override
    public void updateScrollBars() {
        painter.updateScrollBars();
        repaint();
    }

    @Nonnull
    @Override
    public CodeAreaScrollPosition getScrollPosition() {
        return scrollPosition;
    }

    @Override
    public void setScrollPosition(@Nonnull CodeAreaScrollPosition scrollPosition) {
        this.scrollPosition.setScrollPosition(scrollPosition);
    }

    @Nonnull
    @Override
    public ScrollBarVisibility getVerticalScrollBarVisibility() {
        return verticalScrollBarVisibility;
    }

    @Override
    public void setVerticalScrollBarVisibility(ScrollBarVisibility verticalScrollBarVisibility) {
        this.verticalScrollBarVisibility = verticalScrollBarVisibility;
        resetPainter();
        updateScrollBars();
    }

    @Nonnull
    @Override
    public VerticalScrollUnit getVerticalScrollUnit() {
        return verticalScrollUnit;
    }

    @Override
    public void setVerticalScrollUnit(@Nonnull VerticalScrollUnit verticalScrollUnit) {
        this.verticalScrollUnit = verticalScrollUnit;
        long linePosition = scrollPosition.getRowPosition();
        if (verticalScrollUnit == VerticalScrollUnit.ROW) {
            scrollPosition.setRowOffset(0);
        }
        resetPainter();
        scrollPosition.setRowPosition(linePosition);
        updateScrollBars();
        notifyScrolled();
    }

    @Nonnull
    @Override
    public ScrollBarVisibility getHorizontalScrollBarVisibility() {
        return horizontalScrollBarVisibility;
    }

    @Override
    public void setHorizontalScrollBarVisibility(@Nonnull ScrollBarVisibility horizontalScrollBarVisibility) {
        this.horizontalScrollBarVisibility = horizontalScrollBarVisibility;
        resetPainter();
        updateScrollBars();
    }

    @Nonnull
    @Override
    public HorizontalScrollUnit getHorizontalScrollUnit() {
        return horizontalScrollUnit;
    }

    @Override
    public void setHorizontalScrollUnit(@Nonnull HorizontalScrollUnit horizontalScrollUnit) {
        this.horizontalScrollUnit = horizontalScrollUnit;
        int bytePosition = scrollPosition.getCharPosition();
        if (horizontalScrollUnit == HorizontalScrollUnit.CHARACTER) {
            scrollPosition.setCharOffset(0);
        }
        resetPainter();
        scrollPosition.setCharPosition(bytePosition);
        updateScrollBars();
        notifyScrolled();
    }

    @Override
    public void reset() {
        Display.getDefault().asyncExec(() -> {
            painter.reset();
        });
    }

    @Override
    public void updateLayout() {
        Display.getDefault().asyncExec(() -> {
            layout(true, true);
            painter.updateLayout();
        });
    }

    public void repaint() {
        Display.getDefault().asyncExec(() -> {
            layout(true, true);
            resetPainter();
            painter.repaint();
        });
    }

    @Override
    public void resetPainter() {
        painter.reset();
    }

    @Override
    public void notifyCaretChanged() {
        repaint();
    }

    @Nonnull
    @Override
    public SelectionRange getSelection() {
        return selection;
    }

    @Override
    public void setSelection(@Nonnull SelectionRange selection) {
        CodeAreaUtils.requireNonNull(selection);

        this.selection.setSelection(selection);
        notifySelectionChanged();
        repaint();
    }

    @Override
    public void setSelection(long start, long end) {
        this.selection.setSelection(start, end);
        notifySelectionChanged();
        repaint();
    }

    @Override
    public void clearSelection() {
        this.selection.clearSelection();
        notifySelectionChanged();
        repaint();
    }

    @Override
    public boolean hasSelection() {
        return !selection.isEmpty();
    }

    @Nonnull
    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public void setCharset(@Nonnull Charset charset) {
        CodeAreaUtils.requireNonNull(charset);

        this.charset = charset;
        painter.reset();
        repaint();
    }

    @Nonnull
    @Override
    public EditationMode getEditationMode() {
        return editationMode;
    }

    @Override
    public boolean isEditable() {
        return editationMode != EditationMode.READ_ONLY;
    }

    @Override
    public void setEditationMode(@Nonnull EditationMode editationMode) {
        boolean changed = editationMode != this.editationMode;
        this.editationMode = editationMode;
        if (changed) {
            editationModeChangedListeners.forEach((listener) -> {
                listener.editationModeChanged(editationMode);
            });
            caret.resetBlink();
            repaint();
        }
    }

    @Override
    public boolean isHandleClipboard() {
        return handleClipboard;
    }

    @Override
    public void setHandleClipboard(boolean handleClipboard) {
        this.handleClipboard = handleClipboard;
    }

    @Nonnull
    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(@Nonnull Font font) {
        this.font = font;
        painter.reset();
        repaint();
    }

    @Nonnull
    @Override
    public BasicBackgroundPaintMode getBackgroundPaintMode() {
        return borderPaintMode;
    }

    @Override
    public void setBackgroundPaintMode(@Nonnull BasicBackgroundPaintMode borderPaintMode) {
        this.borderPaintMode = borderPaintMode;
        repaint();
    }

    @Nonnull
    @Override
    public RowWrappingMode getRowWrapping() {
        return lineWrapping;
    }

    @Override
    public void setRowWrapping(@Nonnull RowWrappingMode lineWrapping) {
        this.lineWrapping = lineWrapping;
        updateLayout();
    }

    @Override
    public int getWrappingBytesGroupSize() {
        return wrappingBytesGroupSize;
    }

    @Override
    public void setWrappingBytesGroupSize(int groupSize) {
        wrappingBytesGroupSize = groupSize;
        updateLayout();
    }

    @Override
    public int getMaxBytesPerRow() {
        return maxBytesPerLine;
    }

    @Nonnull
    @Override
    public BasicCodeAreaColors getBasicColors() {
        return painter.getBasicColors();
    }

    @Override
    public void setBasicColors(@Nonnull BasicCodeAreaColors colors) {
        painter.setBasicColors(colors);
    }

    @Override
    public void setMaxBytesPerLine(int maxBytesPerLine) {
        this.maxBytesPerLine = maxBytesPerLine;
    }

    public void notifySelectionChanged() {
        selectionChangedListeners.forEach((selectionChangedListener) -> {
            selectionChangedListener.selectionChanged(selection);
        });
    }

    @Override
    public void notifyCaretMoved() {
        caretMovedListeners.forEach((caretMovedListener) -> {
            caretMovedListener.caretMoved(caret.getCaretPosition());
        });
    }

    @Override
    public void notifyScrolled() {
        scrollingListeners.forEach((scrollingListener) -> {
            scrollingListener.scrolled();
        });
    }

    @Override
    public void addSelectionChangedListener(@Nullable SelectionChangedListener selectionChangedListener) {
        selectionChangedListeners.add(selectionChangedListener);
    }

    @Override
    public void removeSelectionChangedListener(@Nullable SelectionChangedListener selectionChangedListener) {
        selectionChangedListeners.remove(selectionChangedListener);
    }

    @Override
    public void addCaretMovedListener(@Nullable CaretMovedListener caretMovedListener) {
        caretMovedListeners.add(caretMovedListener);
    }

    @Override
    public void removeCaretMovedListener(@Nullable CaretMovedListener caretMovedListener) {
        caretMovedListeners.remove(caretMovedListener);
    }

    @Override
    public void addScrollingListener(@Nullable ScrollingListener scrollingListener) {
        scrollingListeners.add(scrollingListener);
    }

    @Override
    public void removeScrollingListener(@Nullable ScrollingListener scrollingListener) {
        scrollingListeners.remove(scrollingListener);
    }

    @Override
    public void addEditationModeChangedListener(@Nullable EditationModeChangedListener editationModeChangedListener) {
        editationModeChangedListeners.add(editationModeChangedListener);
    }

    @Override
    public void removeEditationModeChangedListener(@Nullable EditationModeChangedListener editationModeChangedListener) {
        editationModeChangedListeners.remove(editationModeChangedListener);
    }

    @Override
    public void dispose() {
        painter.dispose();
    }
}
