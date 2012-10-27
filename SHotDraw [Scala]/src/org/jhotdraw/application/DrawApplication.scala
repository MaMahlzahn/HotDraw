/*
 * @(#)DrawApplication.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.application

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Component._
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.GraphicsEnvironment
import java.awt.PrintJob
import java.awt.event.InputEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.IOException
import java.util.ArrayList
import java.util.ListIterator
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.JToolBar
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.WindowConstants
import org.jhotdraw.contrib.Desktop
import org.jhotdraw.contrib.DesktopEvent
import org.jhotdraw.contrib.DesktopListener
import org.jhotdraw.contrib.JPanelDesktop
import org.jhotdraw.figures.GroupCommand
import org.jhotdraw.figures.PolyLineFigure
import org.jhotdraw.figures.UngroupCommand
import org.jhotdraw.framework.Drawing
import org.jhotdraw.framework.DrawingEditor
import org.jhotdraw.framework.DrawingView
import org.jhotdraw.framework.FigureAttributeConstant
import org.jhotdraw.framework.Tool
import org.jhotdraw.framework.ViewChangeListener
import org.jhotdraw.standard.AbstractCommand
import org.jhotdraw.standard.BringToFrontCommand
import org.jhotdraw.standard.ChangeAttributeCommand
import org.jhotdraw.standard.CopyCommand
import org.jhotdraw.standard.CutCommand
import org.jhotdraw.standard.DeleteCommand
import org.jhotdraw.standard.DuplicateCommand
import org.jhotdraw.standard.NullTool
import org.jhotdraw.standard.PasteCommand
import org.jhotdraw.standard.SelectAllCommand
import org.jhotdraw.standard.SelectionTool
import org.jhotdraw.standard.SendToBackCommand
import org.jhotdraw.standard.StandardDrawing
import org.jhotdraw.standard.StandardDrawingView
import org.jhotdraw.standard.ToolButton
import org.jhotdraw.util.ColorMap
import org.jhotdraw.util.Command
import org.jhotdraw.util.CommandMenu
import org.jhotdraw.util.Iconkit
import org.jhotdraw.util.PaletteButton
import org.jhotdraw.util.PaletteListener
import org.jhotdraw.util.RedoCommand
import org.jhotdraw.util.SerializationStorageFormat
import org.jhotdraw.util.StandardStorageFormat
import org.jhotdraw.util.StandardVersionControlStrategy
import org.jhotdraw.util.StorageFormat
import org.jhotdraw.util.StorageFormatManager
import org.jhotdraw.util.UndoCommand
import org.jhotdraw.util.UndoManager
import org.jhotdraw.util.UndoableCommand
import org.jhotdraw.util.VersionControlStrategy
import org.jhotdraw.util.VersionManagement
import org.jhotdraw.util.VersionRequester
import java.lang.reflect.InvocationTargetException
import scala.collection.JavaConversions._


/**
 * DrawApplication defines a standard presentation for
 * standalone drawing editors. The presentation is
 * customized in subclasses.
 * The application is started as follows:
 * <pre>
 * public static void main(String[] args) {
 * MayDrawApp window = new MyDrawApp();
 * window.open();
 * }
 * </pre>
 *
 * @version <$CURRENT_VERSION$>
 */
object DrawApplication {
  private final val serialVersionUID: Long = 4741862790087996224L
  val TITLE: String = "JHotDraw"
  private final val fgDrawPath: String = "/org/jhotdraw/"
  val IMAGES: String = fgDrawPath + "images/"
  
  /**
   * The index of the file menu in the menu bar.
   */
  val FILE_MENU: Int = 0
  /**
   * The index of the edit menu in the menu bar.
   */
  val EDIT_MENU: Int = 1
  /**
   * The index of the alignment menu in the menu bar.
   */
  val ALIGNMENT_MENU: Int = 2
  /**
   * The index of the attributes menu in the menu bar.
   */
  val ATTRIBUTES_MENU: Int = 3
}

class DrawApplication extends JFrame(DrawApplication.TITLE) with DrawingEditor with PaletteListener with VersionRequester {

  protected var winCount: Int = 0
  protected var fgUntitled: String = "untitled"
  
  /**
   * Open a new window for this application containing the passed in drawing,
   * or a new drawing if the passed in drawing is null.
   */
  def newWindow(initialDrawing: Drawing) {
    val window: DrawApplication = new DrawApplication
    if (initialDrawing == null) {
      window.open
    }
    else {
      window.open(window.createDrawingView(initialDrawing))
    }
  }

  final def newWindow {
    newWindow(createDrawing)
  }

  /**
   * Opens a new window
   */
  def open {
    open(createInitialDrawingView)
  }

  /**
   * Opens a new window with a drawing view.
   */
  protected def open(newDrawingView: DrawingView) {
    getVersionControlStrategy.assertCompatibleVersion
    setUndoManager(new UndoManager)
    setIconkit(createIconkit)
    getContentPane.setLayout(new BorderLayout)
    setStatusLine(createStatusLine)
    getContentPane.add(getStatusLine, BorderLayout.SOUTH)
//    setTool(new NullTool(this), "")
    setView(newDrawingView)
    val tools: JToolBar = createToolPalette
    createTools(tools)
    val activePanel: JPanel = new JPanel
    activePanel.setAlignmentX(LEFT_ALIGNMENT)
    activePanel.setAlignmentY(TOP_ALIGNMENT)
    activePanel.setLayout(new BorderLayout)
    activePanel.add(tools, BorderLayout.NORTH)
    setDesktopListener(createDesktopListener)
    setDesktop(createDesktop)
    activePanel.add(getDesktop.asInstanceOf[Component], BorderLayout.CENTER)
    getContentPane.add(activePanel, BorderLayout.CENTER)
    val mb: JMenuBar = new JMenuBar
    createMenus(mb)
    setJMenuBar(mb)
    val d: Dimension = defaultSize
    if (d.width > mb.getPreferredSize.width) {
      setSize(d.width, d.height)
    }
    else {
      setSize(mb.getPreferredSize.width, d.height)
    }
    addListeners
    setStorageFormatManager(createStorageFormatManager)
    setVisible(true)
    val r: Runnable = new Runnable {
      def run {
        if (newDrawingView.isInteractive) {
          getDesktop.addToDesktop(newDrawingView, Desktop.PRIMARY)
        }
        toolDone
      }
    }
    if (java.awt.EventQueue.isDispatchThread == false) {
      try {
        java.awt.EventQueue.invokeAndWait(r)
      } catch {
        case ie: InterruptedException => {
          System.err.println(ie.getMessage)
          exit
        }
        case ite: InvocationTargetException => {
          System.err.println(ite.getMessage)
          exit
        }
      }
    } else {
      r.run
    }
    toolDone
  }

  /**
   * Registers the listeners for this window
   */
  protected def addListeners {
    addWindowListener(new WindowAdapter {
      override def windowClosing(event: WindowEvent) {
        endApp
      }

      override def windowOpened(event: WindowEvent) {
        winCount += 1
      }

      override def windowClosed(event: WindowEvent) {
        if (winCount == 0) System.exit(0)
        winCount -= 1
      }
    })
  }

  /**
   * Creates the standard menus. Clients override this
   * method to add additional menus.
   */
  protected def createMenus(mb: JMenuBar) {
    addMenuIfPossible(mb, createFileMenu)
    addMenuIfPossible(mb, createEditMenu)
    addMenuIfPossible(mb, createAttributesMenu)
  }

  protected def addMenuIfPossible(mb: JMenuBar, newMenu: JMenu) {
    if (newMenu != null) {
      mb.add(newMenu)
    }
  }

  /**
   * Creates the file menu. Clients override this
   * method to add additional menu items.
   */
  protected def createFileMenu: JMenu = {
    val menu: CommandMenu = new CommandMenu("File")
    var cmd: Command = new AbstractCommand("New", this, false) {
      override def execute {
        promptNew
      }
    }
    menu.add(cmd, KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK))
    cmd = new AbstractCommand("Open...", this, false) {
      override def execute {
        promptOpen
      }
    }
    menu.add(cmd, KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK))
    cmd = new AbstractCommand("Save As...", this, true) {
      override def execute {
        promptSaveAs
      }
    }
    menu.add(cmd, KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK))
    menu.addSeparator
    cmd = new AbstractCommand("Print...", this, true) {
      override def execute {
        print
      }
    }
    menu.add(cmd, KeyStroke.getKeyStroke('P', InputEvent.CTRL_DOWN_MASK))
    menu.addSeparator
    cmd = new AbstractCommand("Quit", this, true) {
      override def execute {
        endApp
      }
    }
    menu.add(cmd, KeyStroke.getKeyStroke('Q', InputEvent.CTRL_DOWN_MASK))
    menu
  }

  /**
   * Creates the edit menu. Clients override this
   * method to add additional menu items.
   */
  protected def createEditMenu: JMenu = {
    val menu: CommandMenu = new CommandMenu("Edit")
    menu.add(new UndoableCommand(new SelectAllCommand("Select All", this)), KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK))
    menu.addSeparator
    menu.add(new UndoableCommand(new CutCommand("Cut", this)), KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK))
    menu.add(new CopyCommand("Copy", this), KeyStroke.getKeyStroke('C', InputEvent.CTRL_DOWN_MASK))
    menu.add(new UndoableCommand(new PasteCommand("Paste", this)), KeyStroke.getKeyStroke('V', InputEvent.CTRL_DOWN_MASK))
    menu.addSeparator
    menu.add(new UndoableCommand(new DuplicateCommand("Duplicate", this)), KeyStroke.getKeyStroke('D', InputEvent.CTRL_DOWN_MASK))
    menu.add(new UndoableCommand(new DeleteCommand("Delete", this)))
    menu.addSeparator
    menu.add(new UndoableCommand(new GroupCommand("Group", this)), KeyStroke.getKeyStroke('G', InputEvent.CTRL_DOWN_MASK))
    menu.add(new UndoableCommand(new UngroupCommand("Ungroup", this)), KeyStroke.getKeyStroke('U', InputEvent.CTRL_DOWN_MASK))
    menu.addSeparator
    menu.add(new UndoableCommand(new SendToBackCommand("Send to Back", this)), KeyStroke.getKeyStroke('B', InputEvent.CTRL_DOWN_MASK))
    menu.add(new UndoableCommand(new BringToFrontCommand("Bring to Front", this)), KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK))
    menu.addSeparator
    menu.add(new UndoCommand("Undo Command", this), KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK))
    menu.add(new RedoCommand("Redo Command", this), KeyStroke.getKeyStroke('Y', InputEvent.CTRL_DOWN_MASK))
    menu
  }

  /**
   * Creates the attributes menu and its submenus. Clients override this
   * method to add additional menu items.
   */
  protected def createAttributesMenu: JMenu = {
    val menu: JMenu = new JMenu("Attributes")
    menu.add(createColorMenu("Fill Color", FigureAttributeConstant.FILL_COLOR))
    menu.add(createColorMenu("Pen Color", FigureAttributeConstant.FRAME_COLOR))
    menu.add(createArrowMenu)
    menu.addSeparator
    menu.add(createFontMenu)
    menu.add(createFontSizeMenu)
    menu.add(createFontStyleMenu)
    menu.add(createColorMenu("Text Color", FigureAttributeConstant.TEXT_COLOR))
    menu
  }

  /**
   * Creates the color menu.
   */
  protected def createColorMenu(title: String, attribute: FigureAttributeConstant): JMenu = {
    val menu: CommandMenu = new CommandMenu(title)
    ColorMap.fMap foreach { case (name, color) =>
      menu.add(new UndoableCommand(new ChangeAttributeCommand(name, attribute, color, this)))
    }
    menu
  }

  /**
   * Creates the arrows menu.
   */
  protected def createArrowMenu: JMenu = {
    val arrowMode: FigureAttributeConstant = FigureAttributeConstant.ARROW_MODE
    val menu: CommandMenu = new CommandMenu("Arrow")
    menu.add(new UndoableCommand(new ChangeAttributeCommand("none", arrowMode, new Integer(PolyLineFigure.ARROW_TIP_NONE), this)))
    menu.add(new UndoableCommand(new ChangeAttributeCommand("at Start", arrowMode, new Integer(PolyLineFigure.ARROW_TIP_START), this)))
    menu.add(new UndoableCommand(new ChangeAttributeCommand("at End", arrowMode, new Integer(PolyLineFigure.ARROW_TIP_END), this)))
    menu.add(new UndoableCommand(new ChangeAttributeCommand("at Both", arrowMode, new Integer(PolyLineFigure.ARROW_TIP_BOTH), this)))
    menu
  }

  /**
   * Creates the fonts menus. It installs all available fonts
   * supported by the toolkit implementation.
   */
  protected def createFontMenu: JMenu = {
    val menu: CommandMenu = new CommandMenu("Font")
    val fonts: Array[String] = GraphicsEnvironment.getLocalGraphicsEnvironment.getAvailableFontFamilyNames
    fonts foreach { f =>
      menu.add(new UndoableCommand(new ChangeAttributeCommand(f, FigureAttributeConstant.FONT_NAME, f, this)))
    }
    menu
  }

  /**
   * Creates the font style menu with entries (Plain, Italic, Bold).
   */
  protected def createFontStyleMenu: JMenu = {
    val fontStyle: FigureAttributeConstant = FigureAttributeConstant.FONT_STYLE
    val menu: CommandMenu = new CommandMenu("Font Style")
    menu.add(new UndoableCommand(new ChangeAttributeCommand("Plain", fontStyle, new Integer(Font.PLAIN), this)))
    menu.add(new UndoableCommand(new ChangeAttributeCommand("Italic", fontStyle, new Integer(Font.ITALIC), this)))
    menu.add(new UndoableCommand(new ChangeAttributeCommand("Bold", fontStyle, new Integer(Font.BOLD), this)))
    menu
  }

  /**
   * Creates the font size menu.
   */
  protected def createFontSizeMenu: JMenu = {
    val menu: CommandMenu = new CommandMenu("Font Size")
    val sizes: List[Int] = List(9, 10, 12, 14, 16, 18, 20, 24, 36, 48, 72)
    sizes foreach { e =>
      menu.add(new UndoableCommand(new ChangeAttributeCommand(e.toString, FigureAttributeConstant.FONT_SIZE, e, this)))
    }
    menu
  }


  /**
   * Creates the tool palette.
   */
  protected def createToolPalette: JToolBar = {
    val palette: JToolBar = new JToolBar
    palette.setBackground(Color.lightGray)
    palette
  }

  /**
   * Creates the tools. By default only the selection tool is added.
   * Override this method to add additional tools.
   * Call the inherited method to include the selection tool.
   * @param palette the palette where the tools are added.
   */
  protected def createTools(palette: JToolBar) {
    setDefaultTool(createDefaultTool)
    palette.add(fDefaultToolButton)
  }

  /**
   * Creates the selection tool used in this editor. Override to use
   * a custom selection tool.
   */
  protected def createSelectionTool: Tool = {
    return new SelectionTool(this)
  }

  protected def createDefaultTool: Tool = {
    return createSelectionTool
  }

  protected def setDefaultTool(newDefaultTool: Tool) {
    if (newDefaultTool != null) fDefaultToolButton = createToolButton(DrawApplication.IMAGES + "SEL", "Selection Tool", newDefaultTool)
    else fDefaultToolButton = null
  }

  def getDefaultTool: Tool = {
    if (fDefaultToolButton != null) fDefaultToolButton.tool
    else null
  }

  /**
   * Creates a tool button with the given image, tool, and text
   */
  protected def createToolButton(iconName: String, toolName: String, tool: Tool): ToolButton = new ToolButton(this, iconName, toolName, tool)

  /**
   * Creates the drawing view used in this application.
   * You need to override this method to use a DrawingView
   * subclass in your application. By default a standard
   * DrawingView is returned.
   */
  protected def createDrawingView: DrawingView = {
    val createdDrawingView: DrawingView = createDrawingView(createDrawing)
    createdDrawingView.drawing.setTitle(getDefaultDrawingTitle)
    createdDrawingView
  }

  protected def createDrawingView(newDrawing: Drawing): DrawingView = {
    val d: Dimension = getDrawingViewSize
    val newDrawingView: DrawingView = new StandardDrawingView(this, d.width, d.height)
    newDrawingView.setDrawing(newDrawing)
    newDrawingView
  }

  /**
   * Create the DrawingView that is active when the application is started.
   * This initial DrawingView might be different from DrawingView created
   * by the application, so subclasses can override this method to provide
   * a special drawing view for application startup time, e.g. a NullDrawingView
   * which does not display an internal frame in a multiple document interface
   * (MDI) application.
   *
   * @return drawing view that is active at application startup time
   */
  protected def createInitialDrawingView: DrawingView = createDrawingView

  /**
   * Override to define the dimensions of the drawing view.
   */
  protected def getDrawingViewSize: Dimension = new Dimension(800, 800)

  /**
   * Creates the drawing used in this application.
   * You need to override this method to use a Drawing
   * subclass in your application. By default a standard
   * Drawing is returned.
   */
  protected def createDrawing: Drawing = new StandardDrawing

  protected def createDesktop: Desktop = new JPanelDesktop(this)

  protected def setDesktop(newDesktop: Desktop) {
    newDesktop.addDesktopListener(getDesktopListener)
    fDesktop = newDesktop
  }

  /**
   * Get the component, in which the content is embedded. This component
   * acts as a desktop for the content.
   */
  def getDesktop: Desktop = fDesktop

  /**
   * Factory method to create a StorageFormatManager for supported storage formats.
   * Different applications might want to use different storage formats and can return
   * their own format manager by overriding this method.
   */
  def createStorageFormatManager: StorageFormatManager = {
    val storageFormatManager: StorageFormatManager = new StorageFormatManager
    storageFormatManager.setDefaultStorageFormat(new StandardStorageFormat)
    storageFormatManager.addStorageFormat(storageFormatManager.getDefaultStorageFormat)
    storageFormatManager.addStorageFormat(new SerializationStorageFormat)
    storageFormatManager
  }

  /**
   * Set the StorageFormatManager. The StorageFormatManager is used when storing and
   * restoring Drawing from the file system.
   */
  protected final def setStorageFormatManager(newStorageFormatManager: StorageFormatManager) {
    fStorageFormatManager = newStorageFormatManager
  }

  /**
   * Return the StorageFormatManager for this application.The StorageFormatManager is
   * used when storing and restoring Drawing from the file system.
   */
  def getStorageFormatManager: StorageFormatManager = fStorageFormatManager

  /**
   * Gets the default size of the window.
   */
  protected def defaultSize: Dimension = {
    return new Dimension(600, 450)
  }

  /**
   * Creates the status line.
   */
  protected def createStatusLine: JTextField = {
    val field: JTextField = new JTextField("No Tool", 40)
    field.setBackground(Color.white)
    field.setEditable(false)
    field
  }

  private def setStatusLine(newStatusLine: JTextField) {
    fStatusLine = newStatusLine
  }

  protected def getStatusLine: JTextField = fStatusLine

  /**
   * Handles a user selection in the palette.
   * @see PaletteListener
   */
  def paletteUserSelected(paletteButton: PaletteButton) {
    val toolButton: ToolButton = paletteButton.asInstanceOf[ToolButton]
    setTool(toolButton.tool, toolButton.name)
    setSelected(toolButton)
  }

  /**
   * Handles when the mouse enters or leaves a palette button.
   * @see PaletteListener
   */
  def paletteUserOver(paletteButton: PaletteButton, inside: Boolean) {
    val toolButton: ToolButton = paletteButton.asInstanceOf[ToolButton]
    if (inside) showStatus(toolButton.name)
    else if (fSelectedToolButton != null) showStatus(fSelectedToolButton.name)
  }

  /**
   * Gets the current tool.
   * @see DrawingEditor
   */
  def tool: Tool = fTool

  /**
   * Retrieve the active view from the window
   * Gets the current drawing view.
   * @see DrawingEditor
   */
  def view: DrawingView = fView

  protected def setView(newView: DrawingView) {
    val oldView: DrawingView = fView
    fView = newView
    fireViewSelectionChangedEvent(oldView, view)
  }

  def views: Array[DrawingView] = Array[DrawingView](view)

  /**
   * Sets the default tool of the editor.
   * @see DrawingEditor
   */
  def toolDone {
    if (fDefaultToolButton != null) {
      setTool(fDefaultToolButton.tool, fDefaultToolButton.name)
      setSelected(fDefaultToolButton)
    }
  }

  /**
   * Fired by a view when the figure selection changes.  Since Commands and
   * Tools may depend on the figure selection they are registered to be notified
   * about these events.
   * Any selection sensitive GUI component should update its
   * own state if the selection has changed, e.g. selection sensitive menuitems
   * will update their own states.
   * @see DrawingEditor
   */
  def figureSelectionChanged(view: DrawingView) {
    checkCommandMenus
  }

  protected def checkCommandMenus {
    val mb: JMenuBar = getJMenuBar
    
    for(i <- 0 to mb.getMenuCount-1) {
      mb.getMenu(i) match {
        case jm: CommandMenu => checkCommandMenu(jm)
        case _ =>
      }
    }
  }

  protected def checkCommandMenu(cm: CommandMenu) {
    cm.checkEnabled
    for (i <- 0 to cm.getItemCount-1) { 
      cm.getItem(i) match {
        case jmi: CommandMenu => checkCommandMenu(jmi)
        case _ =>
      }
    }
  }

  /**
   * Register to hear when the active view is changed.  For Single document
   * interface, this will happen when a new drawing is created.
   */
  def addViewChangeListener(vsl: ViewChangeListener) {
    listeners ::= vsl
  }

  /**
   * Remove listener
   */
  def removeViewChangeListener(vsl: ViewChangeListener) {
    listeners -= vsl
  }

  /**
   * An appropriate event is triggered and all registered observers
   * are notified if the drawing view has been changed, e.g. by
   * switching between several internal frames.  This method is
   * usually not needed in SDI environments.
   */
  protected def fireViewSelectionChangedEvent(oldView: DrawingView, newView: DrawingView) {
    val li: ListIterator[ViewChangeListener] = listeners.listIterator(listeners.size)
    while (li.hasPrevious) {
      val vsl: ViewChangeListener = li.previous.asInstanceOf[ViewChangeListener]
      vsl.viewSelectionChanged(oldView, newView)
    }
  }

  protected def fireViewCreatedEvent(view: DrawingView) {
    val li: ListIterator[ViewChangeListener] = listeners.listIterator(listeners.size)
    while (li.hasPrevious) {
      val vsl: ViewChangeListener = li.previous.asInstanceOf[ViewChangeListener]
      vsl.viewCreated(view)
    }
  }

  protected def fireViewDestroyingEvent(view: DrawingView) {
    val li: ListIterator[ViewChangeListener] = listeners.listIterator(listeners.size)
    while (li.hasPrevious) {
      val vsl: ViewChangeListener = li.previous.asInstanceOf[ViewChangeListener]
      vsl.viewDestroying(view)
    }
  }

  /**
   * Shows a status message.
   * @see DrawingEditor
   */
  def showStatus(string: String) {
    getStatusLine.setText(string)
  }

  /**
   * Note: it is inconsistent to directly assign a variable but when using it
   * use it from a method.  (assignment:  fTool = t, usage: tool()) dnoyeB-4/8/02
   * Note:  should we check that the tool is inactive before we activate it?
   * this would be consistent with how we do deactivate.  I think we should do
   * this now and not wait till a bug pops up. even if their is no bug, its
   * consistent and adds understandability to the code.  dnoyeB-4/8/02
   */
  def setTool(t: Tool, name: String) {
    if ((tool != null) && (tool.isActive)) {
      tool.deactivate
    }
    fTool = t
    if (tool != null) {
      showStatus(name)
      tool.activate
    }
  }

  private def setSelected(button: ToolButton) {
    if (fSelectedToolButton != null) {
      fSelectedToolButton.reset
    }
    fSelectedToolButton = button
    if (fSelectedToolButton != null) {
      fSelectedToolButton.select
    }
  }

  /**
   * Exits the application. You should never override this method
   */
  def exit {
    destroy
    dispose
  }

  protected def closeQuery: Boolean = true

  protected def endApp {
    if (closeQuery == true) {
      exit
    }
  }

  /**
   * Handles additional clean up operations. Override to destroy
   * or release drawing editor resources.
   */
  protected def destroy {}

  /**
   * Resets the drawing to a new empty drawing.
   */
  def promptNew {
    newWindow(createDrawing)
  }

  /**
   * Shows a file dialog and opens a drawing.
   */
  def promptOpen {
    toolDone
    val openDialog: JFileChooser = createOpenFileChooser
    getStorageFormatManager.registerFileFilters(openDialog)
    if (openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      var foundFormat: StorageFormat = getStorageFormatManager.findStorageFormat(openDialog.getFileFilter)
      if (foundFormat == null) {
        foundFormat = getStorageFormatManager.findStorageFormat(openDialog.getSelectedFile)
      }
      if (foundFormat != null) {
        loadDrawing(foundFormat, openDialog.getSelectedFile.getAbsolutePath)
      }
      else {
        showStatus("Not a valid file format: " + openDialog.getFileFilter.getDescription)
      }
    }
  }

  /**
   * Shows a file dialog and saves drawing.
   */
  def promptSaveAs {
    if (view != null) {
      toolDone
      val saveDialog: JFileChooser = createSaveFileChooser
      getStorageFormatManager.registerFileFilters(saveDialog)
      if (saveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        var foundFormat: StorageFormat = getStorageFormatManager.findStorageFormat(saveDialog.getFileFilter)
        if (foundFormat == null) {
          foundFormat = getStorageFormatManager.findStorageFormat(saveDialog.getSelectedFile)
        }
        if (foundFormat != null) {
          saveDrawing(foundFormat, saveDialog.getSelectedFile.getAbsolutePath)
        }
        else {
          showStatus("Not a valid file format: " + saveDialog.getFileFilter.getDescription)
        }
      }
    }
  }

  /**
   * Create a file chooser for the open file dialog. Subclasses may override this
   * method in order to customize the open file dialog.
   */
  protected def createOpenFileChooser: JFileChooser = {
    val openDialog: JFileChooser = new JFileChooser
    openDialog.setDialogType(JFileChooser.OPEN_DIALOG)
    openDialog.setDialogTitle("Open File...")
    openDialog
  }

  /**
   * Create a file chooser for the save file dialog. Subclasses may override this
   * method in order to customize the save file dialog.
   */
  protected def createSaveFileChooser: JFileChooser = {
    val saveDialog: JFileChooser = new JFileChooser
    saveDialog.setDialogType(JFileChooser.SAVE_DIALOG)
    saveDialog.setDialogTitle("Save File...")
    saveDialog
  }

  /**
   * Prints the drawing.
   */
  def print {
    tool.deactivate
    val printJob: PrintJob = getToolkit.getPrintJob(this, "Print Drawing", null)
    if (printJob != null) {
      val pg: Graphics = printJob.getGraphics
      if (pg != null) {
        (view.asInstanceOf[StandardDrawingView]).printAll(pg)
        pg.dispose
      }
      printJob.end
    }
    tool.activate
  }

  /**
   * Save a Drawing in a file
   */
  protected def saveDrawing(storeFormat: StorageFormat, file: String) {
    if (view != null) {
      try {
        val name: String = storeFormat.store(file, view.drawing)
        view.drawing.setTitle(name)
        setDrawingTitle(name)
      }
      catch {
        case e: IOException => {
          showStatus(e.toString)
        }
      }
    }
  }

  /**
   * Load a Drawing from a file
   */
  protected def loadDrawing(restoreFormat: StorageFormat, file: String) {
    try {
      val restoredDrawing: Drawing = restoreFormat.restore(file)
      if (restoredDrawing != null) {
        restoredDrawing.setTitle(file)
        newWindow(restoredDrawing)
      }
      else {
        showStatus("Unknown file type: could not open file '" + file + "'")
      }
    }
    catch {
      case e: IOException => {
        showStatus("Error: " + e)
      }
    }
  }

  /**
   * Switch to a new Look&Feel
   */
  private def newLookAndFeel(landf: String) {
    try {
      UIManager.setLookAndFeel(landf)
      SwingUtilities.updateComponentTreeUI(this)
    }
    catch {
      case e: Exception => {
        System.err.println(e)
      }
    }
  }

  /**
   * Set the title of the currently selected drawing
   */
  protected def setDrawingTitle(drawingTitle: String) {
    if (getDefaultDrawingTitle == drawingTitle) {
      setTitle(getApplicationName)
    }
    else {
      setTitle(getApplicationName + " - " + drawingTitle)
    }
  }

  /**
   * Return the title of the currently selected drawing
   */
  protected def getDrawingTitle: String = {
    view.drawing.getTitle
  }

  /**
   * Set the name of the application build from this skeleton application
   */
  def setApplicationName(applicationName: String) {
    fApplicationName = applicationName
  }

  /**
   * Return the name of the application build from this skeleton application
   */
  def getApplicationName: String = {
    fApplicationName
  }

  protected def setUndoManager(newUndoManager: UndoManager) {
    myUndoManager = newUndoManager
  }

  def getUndoManager: UndoManager = {
    myUndoManager
  }

  protected def getVersionControlStrategy: VersionControlStrategy = {
    new StandardVersionControlStrategy(this)
  }

  /**
   * Subclasses should override this method to specify to which versions of
   * JHotDraw they are compatible. A string array is returned so it is possible
   * to specify several version numbers of JHotDraw to which the application
   * is compatible with.
   *
   * @return all versions number of JHotDraw the application is compatible with.
   */
  def getRequiredVersions: List[String] = List(VersionManagement.getPackageVersion(classOf[DrawApplication].getPackage))

  def getDefaultDrawingTitle: String = {
    fgUntitled
  }

  protected def getDesktopListener: DesktopListener = {
    fDesktopListener
  }

  protected def setDesktopListener(desktopPaneListener: DesktopListener) {
    fDesktopListener = desktopPaneListener
  }

  protected def createDesktopListener: DesktopListener = {
    new DesktopListener {
      def drawingViewAdded(dpe: DesktopEvent) {
        val dv: DrawingView = dpe.getDrawingView
        fireViewCreatedEvent(dv)
      }

      def drawingViewRemoved(dpe: DesktopEvent) {
        val dv: DrawingView = dpe.getDrawingView
        getUndoManager.clearUndos(dv)
        getUndoManager.clearRedos(dv)
        fireViewDestroyingEvent(dv)
        checkCommandMenus
      }

      def drawingViewSelected(dpe: DesktopEvent) {
        val dv: DrawingView = dpe.getDrawingView
        if (dv != null) {
          if (dv.drawing != null) dv.unfreezeView
        }
        setView(dv)
      }
    }
  }

  protected def createIconkit: Iconkit = {
    new Iconkit(this)
  }

  protected def setIconkit(newIconkit: Iconkit) {
    fIconkit = newIconkit
  }

  protected def getIconkit: Iconkit = {
    fIconkit
  }

  private var fTool: Tool = null
  private var fIconkit: Iconkit = null
  private var fStatusLine: JTextField = null
  private var fView: DrawingView = null
  private var fDefaultToolButton: ToolButton = null
  private var fSelectedToolButton: ToolButton = null
  private var fApplicationName: String = null
  private var fStorageFormatManager: StorageFormatManager = null
  private var myUndoManager: UndoManager = null
  /**
   * List is not thread safe, but should not need to be.  If it does we can
   * safely synchronize the few methods that use this by synchronizing on
   * the List object itself.
   */
  private var listeners: List[ViewChangeListener] = List()
  private var fDesktopListener: DesktopListener = null
  /**
   * This component acts as a desktop for the content.
   */
  private var fDesktop: Desktop = null
}

