/*
 * Copyright (C) ExBin Project
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
package org.exbin.framework.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.JTextComponent;

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
import org.exbin.framework.utils.gui.WindowHeaderPanel;
import org.exbin.framework.utils.handler.OkCancelService;

/**
 * Utility static methods usable for windows and dialogs.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WindowUtils {

    public static final String ESC_CANCEL_KEY = "esc-cancel";
    public static final String ENTER_OK_KEY = "enter-ok";

    private static LookAndFeel lookAndFeel = null;

    /**
     * Ugly hack for frame handling.
     */
    public static Map<Frame, Shell> frameShells = new HashMap<>();

    private WindowUtils() {
    }

    public static void addHeaderPanel(Window window, Class<?> resourceClass, ResourceBundle resourceBundle) {
        addHeaderPanel(window, resourceClass, resourceBundle, null);
    }

    public static void addHeaderPanel(Window window, Class<?> resourceClass, ResourceBundle resourceBundle, @Nullable OkCancelService okCancelService) {
        URL iconUrl = resourceClass.getResource(resourceBundle.getString("header.icon"));
        Icon headerIcon = iconUrl != null ? new ImageIcon(iconUrl) : null;
        WindowHeaderPanel headerPanel = addHeaderPanel(window, resourceBundle.getString("header.title"), resourceBundle.getString("header.description"), headerIcon);
        if (okCancelService != null) {
            WindowUtils.assignGlobalKeyListener(headerPanel, okCancelService.getOkCancelListener());
        }
    }

    @Nonnull
    public static WindowHeaderPanel addHeaderPanel(Window window, String headerTitle, String headerDescription, @Nullable Icon headerIcon) {
        WindowHeaderPanel headerPanel = new WindowHeaderPanel();
        headerPanel.setTitle(headerTitle);
        headerPanel.setDescription(headerDescription);

        // Ignore
/*        if (headerIcon != null) {
            headerPanel.setIcon(headerIcon);
        }
        if (window instanceof WindowHeaderPanel.WindowHeaderDecorationProvider) {
            ((WindowHeaderPanel.WindowHeaderDecorationProvider) window).setHeaderDecoration(headerPanel);
        } else {
            Frame frame = UiUtils.getFrame(window);
            if (frame instanceof WindowHeaderPanel.WindowHeaderDecorationProvider) {
                ((WindowHeaderPanel.WindowHeaderDecorationProvider) frame).setHeaderDecoration(headerPanel);
            }
        }
        int height = window.getHeight() + headerPanel.getPreferredSize().height;
        ((JDialog) window).getContentPane().add(headerPanel, java.awt.BorderLayout.PAGE_START);
        window.setSize(window.getWidth(), height); */
        return headerPanel;
    }

    public static void invokeWindow(final Window window) {
        if (lookAndFeel != null) {
            try {
                javax.swing.UIManager.setLookAndFeel(lookAndFeel);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(WindowUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        java.awt.EventQueue.invokeLater(() -> {
            if (window instanceof JDialog) {
                ((JDialog) window).setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            }

            window.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            window.setVisible(true);
        });
    }

    @Nonnull
    public static DialogWrapper createDialog(final JComponent component, Object parentComponent, String dialogTitle, Dialog.ModalityType modalityType) {
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

//			parentDisplay = Display.getDefault();
//    		Window window = WindowUtils.getWindow((Component) parentComponent);
//    		if (window instanceof XEmbeddedFrame) {
//	    		Container parentContainer = window.getParent();
//	    		parentContainer.getComponent(0);
//    			parentDisplay = Display.getDefault();
//    		}

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

				/* try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} */
				final java.awt.Frame frame = SWT_AWT.new_Frame(wrapper);
				
				// component.

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

		holder.dialogWrapper = new DialogWrapper() {
			@Override
			public void show() {
				display.syncExec(new Runnable() {
					public void run() {
						shell.open();
					}
				});

				/*				while (!shell.isDisposed()) {
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

    public static void setWindowTitle(Window window, String title) {
		Shell windowShell = WindowUtils.identifyComponentShell(window);
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

    private static class DialogWrapperHolder {
		private DialogWrapper dialogWrapper;
	}

    @Nonnull
    public static JDialog createDialog(final JComponent component) {
		JDialog dialog = new JDialog();
		Dimension size = component.getPreferredSize();
		dialog.add(component);
		dialog.setSize(size.width + 8, size.height + 24);
		return dialog;
    }

    public static void invokeDialog(final JComponent component) {
        JDialog dialog = createDialog(component);
        invokeWindow(dialog);
    }

    @Nullable
    public static LookAndFeel getLookAndFeel() {
        return lookAndFeel;
    }

    public static void setLookAndFeel(LookAndFeel lookAndFeel) {
        WindowUtils.lookAndFeel = lookAndFeel;
    }

    public static void closeWindow(Window window) {
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }

    @Nonnull
    public static JDialog createBasicDialog() {
        JDialog dialog = new JDialog(new javax.swing.JFrame(), true);
        dialog.setSize(640, 480);
        dialog.setLocationByPlatform(true);
        return dialog;
    }
    
    @Nullable
    public static Window getWindow(Component component) {
    	return SwingUtilities.getWindowAncestor(component);
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component target component
     * @param closeButton button which will be used for closing operation
     */
    public static void assignGlobalKeyListener(Component component, final JButton closeButton) {
        assignGlobalKeyListener(component, closeButton, closeButton);
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component target component
     * @param okButton button which will be used for default ENTER
     * @param cancelButton button which will be used for closing operation
     */
    public static void assignGlobalKeyListener(Component component, final JButton okButton, final JButton cancelButton) {
        assignGlobalKeyListener(component, new OkCancelListener() {
            @Override
            public void okEvent() {
                UiUtils.doButtonClick(okButton);
            }

            @Override
            public void cancelEvent() {
            	UiUtils.doButtonClick(cancelButton);
            }
        });
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component target component
     * @param listener ok and cancel event listener
     */
    public static void assignGlobalKeyListener(Component component, @Nullable final OkCancelListener listener) {
        JRootPane rootPane = SwingUtilities.getRootPane(component);
        if (rootPane != null) {
	        final String ESC_CANCEL = "esc-cancel";
	        final String ENTER_OK = "enter-ok";
	        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESC_CANCEL);
	        rootPane.getActionMap().put(ESC_CANCEL, new AbstractAction() {
	            @Override
	            public void actionPerformed(ActionEvent event) {
	                if (listener == null) {
	                    return;
	                }

	                boolean performCancelAction = true;

	                Window window = SwingUtilities.getWindowAncestor(event.getSource() instanceof JRootPane ? (JRootPane) event.getSource() : rootPane);
	                if (window != null) {
	                    Component focusOwner = window.getFocusOwner();
	                    if (focusOwner instanceof JComboBox) {
	                        performCancelAction = !((JComboBox<?>) focusOwner).isPopupVisible();
	                    } else if (focusOwner instanceof JRootPane) {
	                        // Ignore in popup menus
	                        // performCancelAction = false;
	                    }
	                }

	                if (performCancelAction) {
	                    listener.cancelEvent();
	                }
	            }
	        });

	        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_OK);
	        rootPane.getActionMap().put(ENTER_OK, new AbstractAction() {
	            @Override
	            public void actionPerformed(ActionEvent event) {
	                if (listener == null) {
	                    return;
	                }

	                boolean performOkAction = true;

	                Window window = SwingUtilities.getWindowAncestor(event.getSource() instanceof JRootPane ? (JRootPane) event.getSource() : rootPane);
	                if (window != null) {
	                    Component focusOwner = window.getFocusOwner();
	                    if (focusOwner instanceof JTextArea || focusOwner instanceof JEditorPane) {
	                        performOkAction = !((JTextComponent) focusOwner).isEditable();
	                    }
	                }

	                if (performOkAction) {
	                    listener.okEvent();
	                }
	            }
	        });
        } else {
            KeyListener keyListener = new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent evt) {
                    if (listener == null) {
                        return;
                    }

                    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                        boolean performOkAction = true;

                        if (evt.getSource() instanceof JButton) {
                        	UiUtils.doButtonClick(((JButton) evt.getSource()));
                            performOkAction = false;
                        } else if (evt.getSource() instanceof JTextArea) {
                            performOkAction = !((JTextArea) evt.getSource()).isEditable();
                        } else if (evt.getSource() instanceof JTextPane) {
                            performOkAction = !((JTextPane) evt.getSource()).isEditable();
                        } else if (evt.getSource() instanceof JEditorPane) {
                            performOkAction = !((JEditorPane) evt.getSource()).isEditable();
                        }

                        if (performOkAction) {
                            listener.okEvent();
                        }
                    } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        boolean performCancelAction = true;
                        if (evt.getSource() instanceof JComboBox) {
                            performCancelAction = !((JComboBox<?>) evt.getSource()).isPopupVisible();
                        } else if (evt.getSource() instanceof JRootPane) {
                            // Ignore in popup menus
                            performCancelAction = false;
                        }

                        if (performCancelAction) {
                            listener.cancelEvent();
                        }
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            };

            RecursiveLazyComponentListener componentListener = new RecursiveLazyComponentListener(new LazyComponentListener() {
				@Override
				public void componentCreated(Component childComponent) {
	                if (childComponent.isFocusable()) {
	                    childComponent.addKeyListener(keyListener);
	                }
				}
			});
            componentListener.fireListener(component);
      	}
    }

    @Nonnull
    public static WindowPosition getWindowPosition(Window window) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices();
        int windowX = window.getX();
        int windowY = window.getY();
        int screenX = 0;
        int screenY = 0;
        int screenWidth = 0;
        int screenHeight = 0;
        int screenIndex = 0;
        for (GraphicsDevice screen : screenDevices) {
            Rectangle bounds = screen.getDefaultConfiguration().getBounds();
            if (bounds.contains(windowX, windowY)) {
                screenX = bounds.x;
                screenY = bounds.y;
                screenWidth = bounds.width;
                screenHeight = bounds.height;
                break;
            }
            screenIndex++;
        }
        WindowPosition position = new WindowPosition();
        position.setScreenIndex(screenIndex);
        position.setScreenWidth(screenWidth);
        position.setScreenHeight(screenHeight);
        position.setRelativeX(window.getX() - screenX);
        position.setRelativeY(window.getY() - screenY);
        position.setWidth(window.getWidth());
        position.setHeight(window.getHeight());
        position.setMaximized(window instanceof Frame ? (((Frame) window).getExtendedState() & Frame.MAXIMIZED_BOTH) > 0 : false);
        return position;
    }

    public static void setWindowPosition(Window window, WindowPosition position) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices();
        GraphicsDevice device;
        if (screenDevices.length > position.getScreenIndex()) {
            device = screenDevices[position.getScreenIndex()];
        } else {
            device = ge.getDefaultScreenDevice();
        }
        Rectangle screenBounds = device.getDefaultConfiguration().getBounds();
        double absoluteX = position.getScreenWidth() > 0
                ? screenBounds.x + position.getRelativeX() * screenBounds.width / position.getScreenWidth()
                : screenBounds.x + position.getRelativeX();
        double absoluteY = position.getScreenHeight() > 0
                ? screenBounds.y + position.getRelativeY() * screenBounds.height / position.getScreenHeight()
                : screenBounds.y + position.getRelativeY();
        double widthX = position.getScreenWidth() > 0
                ? position.getWidth() * screenBounds.width / position.getScreenWidth()
                : position.getWidth();
        double widthY = position.getScreenHeight() > 0
                ? position.getHeight() * screenBounds.height / position.getScreenHeight()
                : position.getHeight();
        if (position.isMaximized()) {
            window.setLocation((int) absoluteX, (int) absoluteY);
            if (window instanceof Frame) {
                ((Frame) window).setExtendedState(Frame.MAXIMIZED_BOTH);
            } else {
                // TODO if (window instanceof JDialog)
            }
        } else {
            window.setBounds((int) absoluteX, (int) absoluteY, (int) widthX, (int) widthY);
        }
    }

	public static void placeDialogInCenter(Shell parent, Shell shell) {
		org.eclipse.swt.graphics.Rectangle parentSize = parent.getBounds();
		org.eclipse.swt.graphics.Rectangle mySize = shell.getBounds();

		int locationX, locationY;
		locationX = (parentSize.width - mySize.width) / 2 + parentSize.x;
		locationY = (parentSize.height - mySize.height) / 2 + parentSize.y;

		shell.setLocation(new Point(locationX, locationY));
	}

    /**
     * Creates panel for given main and control panel.
     *
     * @param mainPanel main panel
     * @param controlPanel control panel
     * @return panel
     */
    @Nonnull
    public static JPanel createDialogPanel(final JPanel mainPanel, final JPanel controlPanel) {
        final JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.add(mainPanel, BorderLayout.CENTER);
        dialogPanel.add(controlPanel, BorderLayout.SOUTH);
        SwingUtilities.invokeLater(() -> {
            Dimension mainPreferredSize = mainPanel.getPreferredSize();
            Dimension controlPreferredSize = controlPanel.getPreferredSize();
            dialogPanel.setPreferredSize(new Dimension(mainPreferredSize.width, mainPreferredSize.height + controlPreferredSize.height));
        });
        WindowUtils.assignGlobalKeyListener(mainPanel, ((OkCancelService) controlPanel).getOkCancelListener());
        return dialogPanel;
    }

    @ParametersAreNonnullByDefault
    private static final class DialogPanel extends JPanel implements OkCancelService {

        private final OkCancelService okCancelService;

        public DialogPanel(OkCancelService okCancelService) {
            super(new BorderLayout());
            this.okCancelService = okCancelService;
        }

        @Nullable
        @Override
        public JButton getDefaultButton() {
            return null;
        }

        @Nonnull
        @Override
        public OkCancelListener getOkCancelListener() {
            return okCancelService.getOkCancelListener();
        }
    }

    @ParametersAreNonnullByDefault
    public interface DialogWrapper {

        void show();

        void showCentered(@Nullable Component window);

        void close();

        void dispose();

        @Nonnull
        Window getWindow();

        @Nonnull
        Container getParent();

        void center(@Nullable Component window);

        void center();
    }
}
