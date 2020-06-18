/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.gui.utils;

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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.exbin.framework.gui.utils.gui.WindowHeaderPanel;
import org.exbin.framework.gui.utils.handler.OkCancelService;

/**
 * Utility static methods usable for windows and dialogs.
 *
 * @version 0.2.0 2019/08/09
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WindowUtils {

    private static final int BUTTON_CLICK_TIME = 150;
    private static LookAndFeel lookAndFeel = null;

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
        if (headerIcon != null) {
            headerPanel.setIcon(headerIcon);
        }
        if (window instanceof WindowHeaderPanel.WindowHeaderDecorationProvider) {
            ((WindowHeaderPanel.WindowHeaderDecorationProvider) window).setHeaderDecoration(headerPanel);
        } else {
            Frame frame = getFrame(window);
            if (frame instanceof WindowHeaderPanel.WindowHeaderDecorationProvider) {
                ((WindowHeaderPanel.WindowHeaderDecorationProvider) frame).setHeaderDecoration(headerPanel);
            }
        }
        int height = window.getHeight() + headerPanel.getPreferredSize().height;
        ((JDialog) window).getContentPane().add(headerPanel, java.awt.BorderLayout.PAGE_START);
        window.setSize(window.getWidth(), height);
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
    public static DialogWrapper createDialog(final JComponent component, @Nullable final Object parentComponent, String dialogTitle, Dialog.ModalityType modalityType) {
    	Display parentDisplay = null;
    	if (parentComponent instanceof Composite) {
    		parentDisplay = ((Composite)parentComponent).getDisplay();
    	} else if (parentComponent instanceof Component) {
//			parentDisplay = Display.getDefault();
//    		Window window = WindowUtils.getWindow((Component) parentComponent);
//    		if (window instanceof XEmbeddedFrame) {
//	    		Container parentContainer = window.getParent();
//	    		parentContainer.getComponent(0);
//    			parentDisplay = Display.getDefault();
//    		}
    	}
		final Display finalParentDisplay = parentDisplay; 
		final DialogWrapperHolder holder = new DialogWrapperHolder();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
//				Window window = WindowUtils.getWindow(parent);
				Display currentDisplay = Display.getCurrent();
				if (currentDisplay == null)
					currentDisplay = Display.getDefault();
				final Display display = currentDisplay;
				final Shell shell = new Shell(display, SWT.SHELL_TRIM | SWT.CENTER | SWT.APPLICATION_MODAL);
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

				shell.setSize(size.width + 8, size.height + 24);
				// dialog.setSize(size.width + 8, size.height + 24);
				// JDialog dialog = new JDialog(parent, modalityType);
				// dialog.add(component);

				shell.setLayout(new FillLayout());
				Composite wrapper = new Composite(shell, SWT.EMBEDDED | SWT.FILL);
				wrapper.addListener(SWT.Traverse, new Listener() {
					public void handleEvent(Event e) {
						if (e.detail == SWT.TRAVERSE_ESCAPE) {
							e.doit = false;
						}
					}
				});

				java.awt.Frame frame = SWT_AWT.new_Frame(wrapper);
				frame.setLayout(new BorderLayout());
				frame.add(component, BorderLayout.CENTER);
//				frame.setSize(size.width + 8, size.height + 24);

//				Label label = new Label(shell, SWT.NO_FOCUS);
//				label.setText("TEST");
				
				holder.dialogWrapper = new DialogWrapper() {

					@Override
					public void show() {
						display.syncExec(new Runnable() {
							public void run() {
								shell.open();
								shell.layout();

								if (finalParentDisplay != null) {
									while (!shell.isDisposed()) {
				                	if (!finalParentDisplay.readAndDispatch())
				                		finalParentDisplay.sleep();
									}
								}
							}
						});

//						display.syncExec(new Runnable() {
//							public void run() {
//								while (!shell.isDisposed()) {
//				                	if (!display.readAndDispatch())
//				                		display.sleep();
//									}
//							}
//						});

						// TODO: Wait for dialog to be closed
//						try {
//							Composite parentShell = shell.getParent();
//							Display parentDisplay = parentShell.getDisplay();
//							while (!shell.isDisposed()) {
//			                	if (!parentDisplay.readAndDispatch())
//			                		parentDisplay.sleep();
//							}
//						} catch (SWTException ex) {
//							// Not sure how to work around this as we currently don't have parent shell
//						}
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
								shell.close();
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
								shell.dispose();
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
						IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						if (activeWorkbenchWindow != null) {
							Shell parentShell = activeWorkbenchWindow.getShell();
							placeDialogInCenter(parentShell, shell);
						}
					}
				};
				holder.dialogWrapper.center();
			}
		});
		return holder.dialogWrapper;
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

    /**
     * Find frame component for given component.
     *
     * @param component instantiated component
     * @return frame instance if found
     */
    @Nullable
    public static Frame getFrame(Component component) {
        Window parentComponent = SwingUtilities.getWindowAncestor(component);
        while (!(parentComponent == null || parentComponent instanceof Frame)) {
            parentComponent = SwingUtilities.getWindowAncestor(parentComponent);
        }
        if (parentComponent == null) {
            parentComponent = JOptionPane.getRootFrame();
        }
        return (Frame) parentComponent;
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
                doButtonClick(okButton);
            }

            @Override
            public void cancelEvent() {
                doButtonClick(cancelButton);
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
	                        performCancelAction = !((JComboBox) focusOwner).isPopupVisible();
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
                            ((JButton) evt.getSource()).doClick(BUTTON_CLICK_TIME);
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
                            performCancelAction = !((JComboBox) evt.getSource()).isPopupVisible();
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

            RecursiveLazyComponentListener componentListener = new RecursiveLazyComponentListener((Component childComponent) -> {
                if (childComponent.isFocusable()) {
                    childComponent.addKeyListener(keyListener);
                }
            });
            componentListener.fireListener(component);
      	}
    }

    /**
     * Performs visually visible click on the button component.
     *
     * @param button button component
     */
    public static void doButtonClick(JButton button) {
        button.doClick(BUTTON_CLICK_TIME);
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
        position.setMaximized(window instanceof Frame ? (((Frame) window).getExtendedState() & JFrame.MAXIMIZED_BOTH) > 0 : false);
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
                ((Frame) window).setExtendedState(JFrame.MAXIMIZED_BOTH);
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
    public static JPanel createDialogPanel(JPanel mainPanel, JPanel controlPanel) {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.add(mainPanel, BorderLayout.CENTER);
        dialogPanel.add(controlPanel, BorderLayout.SOUTH);
        Dimension mainPreferredSize = mainPanel.getPreferredSize();
        Dimension controlPreferredSize = controlPanel.getPreferredSize();
        dialogPanel.setPreferredSize(new Dimension(mainPreferredSize.width, mainPreferredSize.height + controlPreferredSize.height));
        WindowUtils.assignGlobalKeyListener(mainPanel, ((OkCancelService) controlPanel).getOkCancelListener());
        return dialogPanel;
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
