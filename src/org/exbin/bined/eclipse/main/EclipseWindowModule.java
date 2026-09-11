/*
 * Copyright (C) ExBin Project, https://exbin.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.bined.eclipse.main;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.exbin.jaguif.window.WindowModule;
import org.exbin.jaguif.window.api.WindowHandler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Modified window module to wrap windows as SWT. 
 */
@NullMarked
public class EclipseWindowModule extends WindowModule {
    
    /**
     * Mapping of SWT shells to AWT frames.
     */
    public static Map<Frame, Shell> frameShells = new HashMap<>();

    public static WindowHandler createDialog(final JComponent component, Object parentComponent, String dialogTitle, Dialog.ModalityType modalityType) {
        Display[] initParentDisplay = new Display[1];
        Shell[] initParentShell = new Shell[1];
        if (parentComponent instanceof Composite) {
            initParentDisplay[0] = ((Composite) parentComponent).getDisplay();
            initParentShell[0] = ((Composite) parentComponent).getShell();
        } else if (parentComponent instanceof Menu) {
            Shell shell = ((Menu) parentComponent).getShell();
            initParentShell[0] = shell;
            initParentDisplay[0] = shell.getDisplay();
        } else {
            Shell shell = identifyComponentShell((Component) parentComponent);
            initParentShell[0] = shell;
            initParentDisplay[0] = shell.getDisplay();
/*
            Display display = Display.getDefault();
            initParentDisplay[0] = display;
            display.syncExec(new Runnable() {
                public void run() {
                    initParentShell[0] = display.getActiveShell();
                }
            }); */
        }

        final Display parentDisplay = initParentDisplay[0];
        final Shell parentShell = initParentShell[0];

//          parentDisplay = Display.getDefault();
//          Window window = WindowUtils.getWindow((Component) parentComponent);
//          if (window instanceof XEmbeddedFrame) {
//              Container parentContainer = window.getParent();
//              parentContainer.getComponent(0);
//              parentDisplay = Display.getDefault();
//          }

        final DialogWrapperHolder holder = new DialogWrapperHolder();

        final Shell[] outputShell = new Shell[1];
        final java.awt.Frame[] outputFrame = new java.awt.Frame[1];
        final Display[] outputDisplay = new Display[1];
        parentDisplay.syncExec(new Runnable() {
            public void run() {
                Shell shell = new Shell(parentShell, SWT.SHELL_TRIM | SWT.CENTER | SWT.APPLICATION_MODAL);
                shell.addListener(SWT.Traverse, new Listener() {
                    public void handleEvent(Event e) {
                        if (e.detail == SWT.TRAVERSE_ESCAPE) {
                            e.doit = false;
                        }
                    }
                });

                // DialogDescriptor dialogDescriptor = new DialogDescriptor(component,
                // dialogTitle, modalityType != Dialog.ModalityType.MODELESS, new Object[0],
                // null, 0, null, null);
                // DialogDisplayer.getDefault().createDialog(dialogDescriptor);
                // final Dialog dialog = new Dialog(getFrame(component));
                // dialog.setModalityType(modalityType);
                shell.setText(dialogTitle);
                Dimension size = component.getPreferredSize();
                Point scaledSize = new Point(size.width, size.height); // DPIUtil.autoScaleDown
                final org.eclipse.swt.graphics.Rectangle clientArea = shell.getClientArea();
                final org.eclipse.swt.graphics.Rectangle bounds = shell.getBounds();
                int widthDiff = bounds.width - clientArea.width;
                int heightDiff = bounds.height - clientArea.height;

                Point targetShellSize = new Point(scaledSize.x + widthDiff, scaledSize.y + heightDiff);

                shell.setLayout(new FillLayout());
                Composite wrapper = new Composite(shell, SWT.EMBEDDED);
                wrapper.addListener(SWT.Traverse, new Listener() {
                    public void handleEvent(Event e) {
                        if (e.detail == SWT.TRAVERSE_ESCAPE) {
                            e.doit = false;
                        }
                    }
                });

                final java.awt.Frame frame = SWT_AWT.new_Frame(wrapper);
                
                shell.addDisposeListener((e) -> {
                    frameShells.remove(frame);
                });
                frameShells.put(frame, shell);
                frame.add(component);
                shell.setSize(targetShellSize.x, targetShellSize.y);
                SwingUtilities.invokeLater(() -> {
                    frame.invalidate();
                    frame.setSize(targetShellSize.x, targetShellSize.y);
                });

                outputShell[0] = shell;
                outputFrame[0] = frame;
                outputDisplay[0] = shell.getDisplay();
            }
        });
        final Shell shell = outputShell[0];
        final Display display = outputDisplay[0];
        final java.awt.Frame frame = outputFrame[0];

        holder.dialogWrapper = new WindowHandler() {
            @Override
            public void show() {
                display.syncExec(new Runnable() {
                    public void run() {
                        shell.open();
                    }
                });

                /*              while (!shell.isDisposed()) {
                    parentDisplay.syncExec(new Runnable() {
                        public void run() {
                            if (!parentDisplay.readAndDispatch())
                                parentDisplay.sleep();
                        }
                    });
                }
                System.out.println("END"); */
            }

            @Override
            public void showCentered(@Nullable Component component) {
                center(component);
                show();
            }

            @Override
            public void close() {
                display.syncExec(new Runnable() {
                    public void run() {
                        if (!shell.isDisposed()) {
                            shell.close();
                        }
                    }
                });
            }

            @Override
            public Window getWindow() {
                return frame;
            }

            @Override
            public void dispose() {
                display.syncExec(new Runnable() {
                    public void run() {
                        if (!shell.isDisposed()) {
                            shell.dispose();
                        }
                    }
                });
            }

            @Override
            public Container getParent() {
                return frame;
            }

            @Override
            public void center(@Nullable Component component) {
                if (component == null) {
                    center();
                } else {
                    // TODO holder.dialogWrapper.center(component);
                    center();
                }
            }

            @Override
            public void center() {
                display.syncExec(new Runnable() {
                    public void run() {
                        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                        if (activeWorkbenchWindow != null) {
                            Shell parentShell = activeWorkbenchWindow.getShell();
                            placeDialogInCenter(parentShell, shell);
                        }
                    }
                });
            }
        };
        display.syncExec(new Runnable() {
            public void run() {
                placeDialogInCenter(parentShell, shell);
            }
        });
        return holder.dialogWrapper;
    }
    
    private static class DialogWrapperHolder {
        private WindowHandler dialogWrapper;
    }

    public static void setWindowTitle(Window window, String title) {
        Shell windowShell = EclipseWindowModule.identifyComponentShell(window);
        if (windowShell != null) {
            windowShell.getDisplay().syncExec(new Runnable() {
                public void run() {
                    windowShell.setText(title);
                }
            });
        }
    }

    private static Shell identifyComponentShell(Component component) {
        Component parent = null;
        do {
            parent = component.getParent();
            if (parent != null) {
                component = parent;
            }
        } while (parent != null);

        if (component instanceof JPopupMenu) {
            Frame frame = (Frame) SwingUtilities.getRoot(((JPopupMenu) component).getInvoker());
            return frameShells.get(frame);
        } else if (component instanceof Frame) {
            return frameShells.get((Frame) component);
        }

        return null;
    }

    public static void placeDialogInCenter(Shell parent, Shell shell) {
        org.eclipse.swt.graphics.Rectangle parentSize = parent.getBounds();
        org.eclipse.swt.graphics.Rectangle mySize = shell.getBounds();

        int locationX, locationY;
        locationX = (parentSize.width - mySize.width) / 2 + parentSize.x;
        locationY = (parentSize.height - mySize.height) / 2 + parentSize.y;

        shell.setLocation(new Point(locationX, locationY));
    }
}
