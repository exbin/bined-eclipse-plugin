BinEd - Binary/Hex Editor - Plugin for Eclipse Platform
=======================================================

Hex editor/viewer plugin module for Eclipse platform.

Homepage: https://bined.exbin.org/eclipse-plugin/  

Published as: https://marketplace.eclipse.org/content/bined-binaryhexadecimal-editor  

Screenshot
----------

![BinEd-Editor Screenshot](images/bined-eclipse-screenshot.png?raw=true)

Usage
-----

  * Use "Open With/BinEd Binary/Hex Editor" action in project explorer context menu
  * Use "View as Binary" in Variables window

Features
--------

  * Visualize data as numerical (hexadecimal) codes and text representation
  * Codes can be also binary, octal or decimal
  * Support for Unicode, UTF-8 and other charsets
  * Insert and overwrite edit modes
  * Searching for text / hexadecimal code with found matches highlighting
  * Support for undo/redo
  * Support for files with size up to exabytes
  * Show debug variables as binary data

Known issues
------------

Plugin uses Swing components wrapped in SWT dialogs - it doesn''t fit native look and feel and have various rendering issues depending on system / java version. 

Compiling
---------

Java Development Kit (JDK) version 8 or later is required to build this project.

To compile open this project in Eclipse. See. https://eclipse.org

Install Eclipse Project SDK via Help/Install New Software.

Use "Eclipse Application" run configuration to run/execute.

License
-------

Apache License, Version 2.0 - see LICENSE.txt
