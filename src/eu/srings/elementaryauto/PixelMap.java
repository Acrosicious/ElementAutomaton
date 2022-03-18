package eu.srings.elementaryauto;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Ein Panel, auf dem Punkte gezeichnet werden können. Der Frame lässt sich mit der rechten Maustaste
 * verschieben, mit dem Mausrad zoomen und mit linksklick können Punkte hinzugefügt werden.
 * Als Punkte wird die geschachtelte Klasse Punkt verwendet.
 * Alle funktionen können aktiviert/deaktiviert werden.
 * 
 * Zum erzeugen die static Methode newPixelMapFrame(int, int) verwenden.
 * 
 * @author Sebastian Rings 
 * @version 1.01
 */
public class PixelMap extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private boolean drag_enabled = true;
    private boolean markPoints_enabled = true;
    private boolean zoom_enabled = true;
   
    private Point drag_point = null;
    private double[] first_pos = {0, 0};
    
    private Set<Punkt> _punkte;
    
    private boolean border_enabled = true;
    
    private Color border_color = Color.GRAY;
    private Color pixel_color = Color.BLACK;
    private Color background_color = Color.WHITE;
    
    private int size = 15;
    
    private double[] position = {0, 0};

    public PixelMap()
    {
        //      set_pointSet(new HashSet<Punkt>());
        //      set_pointSet(Collections.synchronizedSet(new HashSet<Punkt>()));
        //      set_pointSet(new CopyOnWriteArraySet<Punkt>());
        set_pointSet(Collections.newSetFromMap(new ConcurrentHashMap<Punkt, Boolean>()));
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        
        setFocusable(true);
    }
    
    @Override
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(background_color);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(pixel_color);

        for(Punkt p : _punkte)
        {
            double W = (p.getX() + position[0]) * size;
            double H = (p.getY() + position[1]) * size;
            
            g2.setColor(p.getColor());
            if(W >= 0 && W <= getWidth() && H >= 0 && H <= getHeight())
                g2.fillRect((int) W, (int) H, size, size);
        }


        if(border_enabled && size > 1)
        {
            g2.setColor(border_color);

            int i = 0;

            while(i <= getWidth())
            {
                g2.drawLine(i, 0, i, getHeight());
                i += size;
            }

            i = 0;

            while(i <= getHeight())
            {
                g2.drawLine(0, i, getWidth(), i);
                i += size;
            }
        }

    }
    
    private Image dbImage;
    private Graphics dbg;
    public void update (Graphics g)
    {
        // initialize buffer
        if (dbImage == null)
        {
            dbImage = createImage (this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics ();
        }
        // clear screen in background
        dbg.setColor (getBackground ());
        dbg.fillRect (0, 0, this.getSize().width, this.getSize().height);

        // draw elements in background
        dbg.setColor (getForeground());
        paint (dbg);

        // draw image on the screen
        g.drawImage (dbImage, 0, 0, this);
    }
    
    public static BufferedImage createBufferedImage(JPanel panel) {
        int w = panel.getWidth();
        int h = panel.getHeight();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        panel.paint(g);
        return bi;
    }

    @Override
    public void mouseClicked(MouseEvent arg0)
    {
        
    }

    @Override
    public void mouseEntered(MouseEvent arg0)
    {
        
    }

    @Override
    public void mouseExited(MouseEvent arg0)
    {
        
    }

    @Override
    public void mousePressed(MouseEvent arg0)
    {
        Punkt p = getPointFromCoords(arg0.getX(), arg0.getY());
        
        if(markPoints_enabled && SwingUtilities.isLeftMouseButton(arg0))
        {
            
            if(!get_pointSet().contains(p))
            {
                get_pointSet().add(p);
            }
            else
            {
                get_pointSet().remove(p);
            }
            
            
        }
        
        if(drag_enabled && SwingUtilities.isRightMouseButton(arg0))
        {
            drag_point = arg0.getPoint();
            first_pos = position.clone();
        }
        
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent arg0)
    {
        if(drag_point != null && SwingUtilities.isRightMouseButton(arg0))
        {
            int DX =  (int) Math.round((arg0.getX() - drag_point.getX())/size);
            int DY =  (int) Math.round((arg0.getY() - drag_point.getY())/size);

            position[0] = first_pos[0] + DX;
            position[1] = first_pos[1] + DY;
            
            repaint();
        }
        drag_point = null;
    }

    @Override
    public void mouseDragged(MouseEvent arg0)
    {
        if(drag_point != null && SwingUtilities.isRightMouseButton(arg0))
        {
            int DX =  (int) Math.round((arg0.getX() - drag_point.getX())/size);
            int DY =  (int) Math.round((arg0.getY() - drag_point.getY())/size);

            position[0] = first_pos[0] + DX;
            position[1] = first_pos[1] + DY;
            
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent arg0)
    {
        
    }

    @Override
    public void keyPressed(KeyEvent arg0)
    {
        switch(arg0.getKeyCode())
        {
        case KeyEvent.VK_LEFT:
            position[0]--;
            break;
        case KeyEvent.VK_RIGHT:
            position[0]++;
            break;
        case KeyEvent.VK_UP:
            position[1]--;
            break;
        case KeyEvent.VK_DOWN:
            position[1]++;
            break;
        }
        
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent arg0)
    {
        
    }

    @Override
    public void keyTyped(KeyEvent arg0)
    {
        
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        
        if (zoom_enabled) 
        {
            int X = (int) ((e.getX() / size) - position[0]);
            int Y = (int) ((e.getY() / size) - position[1]);
            
            if (e.getWheelRotation() > 0 && size > 1) {
                size--;
            } else if (e.getWheelRotation() < 0) {
                size++;
            }
            
            int size2 = size;
            position[0] = (e.getX() / size2 - X);
            position[1] = (e.getY() / size2 - Y);
        }
        repaint();
    }
    
    /**
     * @return Gibt an ob man mit der Maus die Map verschieben kann
     */
    public boolean isDrag_enabled() {
        return drag_enabled;
    }

    /**
     * Zum ein oder ausschalten vom verschieben der Map mit der Maus
     * 
     * @param drag_enabled
     */
    public void setDrag_enabled(boolean drag_enabled) {
        this.drag_enabled = drag_enabled;
    }
    
    /**
     * @return Gibt an ob die Border an ist
     */
    public boolean isBorder_enabled() {
        return border_enabled;
    }

    /**
     * Zum ein oder ausschalten von der Border
     * 
     * @param drag_enabled
     */
    public void setBorder_enabled(boolean border_enabled) {
        this.border_enabled = border_enabled;
    }

    /**
     * @return Gibt an ob man mit der Maus Punkte erstellen Kann
     */
    public boolean isMarkPoints_enabled() {
        return markPoints_enabled;
    }

    /**
     * Zum ein oder ausschalten zum erstellen von Punkte mit der Maus
     * 
     * @param markPoints_enabled
     */
    public void setMarkPoints_enabled(boolean markPoints_enabled) {
        this.markPoints_enabled = markPoints_enabled;
    }

    /**
     * @return Ein Set mit allen Punkten
     */
    public Set<Punkt> get_pointSet() {
        return _punkte;
    }

    /**
     * Ersetzt das Set mit dem übergebenen
     * 
     * @param _punkte
     */
    public void set_pointSet(Set<Punkt> _punkte) {
        this._punkte = _punkte;
        repaint();
    }

    /**
     * Fügt einen Punkt in die Map ein wenn er noch nicht vorhanden ist.
     * 
     * @param X
     * @param Y
     */
    public void addPoint(int X, int Y)
    {
        Punkt p = new Punkt(X, Y, pixel_color);
        
        if(!get_pointSet().contains(p))
        {
            get_pointSet().add(p);
        }
        repaint();
    }
    
    /**
     * Fügt einen Punkt in die Map ein wenn er noch nicht vorhanden ist.
     * 
     * @param p Der Punkt
     */
    public void addPoint(Punkt p)
    {
        if(!get_pointSet().contains(p))
        {
            get_pointSet().add(p);
        }
        repaint();
    }
    
    /**
     * Entfernt einen Punkt von der Map wenn er vorhanden ist.
     * 
     * @param X
     * @param Y
     */
    public void removePoint(int X, int Y)
    {
        Punkt p = new Punkt(X, Y, pixel_color);
        
        if(get_pointSet().contains(p))
        {
            get_pointSet().remove(p);
        }
        repaint();
    }
    
    /**
     * Entfernt einen Punkt von der Map wenn er vorhanden ist.
     * 
     * @param p Der Punkt
     */
    public void removePoint(Punkt p)
    {
        if(get_pointSet().contains(p))
        {
            get_pointSet().remove(p);
        }
        repaint();
    }
    
    /**
     * @param x Die X Koordinate Relativ zur grafischen Oberfläche der Map.
     * @param y Die Y Koordinate Relativ zur grafischen Oberfläche der Map.
     * @return Gibt einen Punkt, der auf den grafischen Koordinaten der Map liegt.
     */
    public Punkt getPointFromCoords(double x, double y)
    {
        return new Punkt(
                (int) (x / size - position[0]),
                (int) (y / size - position[1]));
    }
    
    
    /**
     * @return Die laenge der Pixel
     */
    public int getPixelSize() {
        return size;
    }

    /**
     * @param size Setzt die laenge der Pixel
     */
    public void setPixelSize(int size) {
        this.size = size;
    }

    /**
     * @param borders Zum Ein/Ausschalten vom Raster
     */
    public void setBorders(boolean borders) {
        this.border_enabled = borders;
        repaint();
    }

    /**
     * @param border_color Die Farbe vom Raster
     */
    public void setBorder_color(Color border_color) {
        this.border_color = border_color;
        repaint();
    }


    /**
     * @param zoom_enabled Zum Ein/Ausschalten vom Zoom mit Mausrad
     */
    public void setZoom_enabled(boolean zoom_enabled) {
        this.zoom_enabled = zoom_enabled;
    }

    /**
     * @param pixel_color Die Farbe der Pixel
     */
    public void setPixel_color(Color pixel_color) {
        this.pixel_color = pixel_color;
        repaint();
    }

    /**
     *   @param background_color Die Farbe vom Hintergrund
     */
    public void setBackground_color(Color background_color) {
        this.background_color = background_color;
        repaint();
    }
    
    /**
     * Setzt den Punkt x,y ganz nach oben links in der Ansicht
     * 
     * @param x
     * @param y
     */
    public void setPosition(int x, int y) {
        position[0] = x;
        position[1] = y;
    }
    
    /**
     * Entfernt alle Punkte
     */
    public void clearMap()
    {
        _punkte.clear();
        repaint();
    }
    
    /**
     * @return Ein Set mit allen (max 8) angrenzenden Punkten
     */
    public Set<Punkt> getBorderingPoints(Punkt P)
    {
        Set<Punkt> set = getNewSet();
        
        for(int i = 0; i < 8; i++)
        {
            Punkt p = new Punkt(P.getX()-1+i%3, P.getY()-1+i/3);
            if(get_pointSet().contains(p))
            {
                set.add(p);
            }
        }
        
        return set;
    }
    
    /**
     * @return Ein Leeres Set fuer Punkte
     */
    public static Set<Punkt> getNewSet()
    {
        return new HashSet<Punkt>();
    }
    
    public static PixelMap newPixelMapFrame(int w, int h)
    {
        JFrame f = new JFrame("Map");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setAlwaysOnTop(true); 

        PixelMap map = new PixelMap();
        map.setPreferredSize(new Dimension(w, h));
        
        f.add(map);
        f.setResizable(false);
        f.pack();
        f.setVisible(true);
        
        return map;
    }


    /**
     * @author Sebastian Rings
     * Einfacher Punkt
     */
    public static class Punkt
    {
        int x, y;
        
        Color color;
        
        /**
         * Generiert einen Punkt bei [0:0]
         */
        public Punkt()
        {
            this(0, 0);
        }

        /**
         * Generiert einen Punkt an den Koordinaten
         * 
         * @param x Die x Koordinate
         * @param y Die y Koordinate
         */
        public Punkt(int x, int y)
        {
            this(x, y, Color.BLACK);
        }
        
        /**
         * Generiert einen Punkt an den Koordinaten
         * 
         * @param x Die x Koordinate
         * @param y Die y Koordinate
         */
        public Punkt(int x, int y, Color c)
        {
            this.x = x;
            this.y = y;
            color = c;
        }

        public Color getColor() {
            return color;
        }
        
        public void setColor(Color c) {
            color = c;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        @Override
        public int hashCode()
        {
            return x*1000000+y;
        }

        @Override
        public boolean equals(Object o)
        {
            if(o instanceof Punkt)
            {
                Punkt p = (Punkt) o;

                if(p.getX() == x && p.getY() == y)
                {
                    return true;
                }
            }
            
            return false;
        }

        //      public boolean isVisible(int[] pos, int size, int width, int height) {
        //
        //          boolean result = 
        //                  x >= pos[0] &&
        //                  y >= pos[1] &&
        //                  x <= width / size + pos[0] &&
        //                  y <= height / size + pos[1];
        //                  
        //          return result;
        //      }
        
        public String toString()
        {
            return "Punkt["+ x + " : " + y +"]";
        }
    }
    
    public static class DoublePunkt
    {
        private double x,y;
        
        public DoublePunkt(double x, double y)
        {
            this.x = x;
            this.y = y;
        }
        
        public double getX()
        {
            return x;
        }
        
        public double getY()
        {
            return y;
        }
        
        public Punkt getPunkt(double faktor)
        {
            return new Punkt((int)((x * faktor)+0.5), (int)((y * faktor)+0.5));
        }
    }
}
