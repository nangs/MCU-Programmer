package com.jhe.hexed;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: laullon
 * Date: 08-abr-2003
 * Time: 13:21:09
 * https://code.google.com/p/hxve/
 */
public class JHexEditor extends JPanel implements FocusListener,AdjustmentListener,MouseWheelListener
{
	private static final long serialVersionUID = -8900185044934125191L;
    protected static Font font=new Font("Monospaced",0,12);
	protected final byte[] buffer;
	protected final int bufferOffset;
	protected final int bufferSize;
	protected boolean bufferLocked = false;
	protected boolean bufferChanged = false;
    public int cursor;
    protected int border=2;
    public boolean DEBUG=false;
    private JPanel panel;
    private JScrollBar sb;
    private JHexEditorHEX hex;
    private JHexEditorASCII ascii;
    private int inicio=0;
    private int lineas=10;

    public JHexEditor(byte[] data){
        super();
        buffer = data;
        bufferOffset = 0;
        bufferSize = data.length-bufferOffset;
        initialize();
    }
    public JHexEditor(byte[] data, int offset, int size){
        super();
        buffer = data;
        bufferOffset = offset;
        bufferSize = size;
        if(buffer.length<(bufferSize+bufferOffset)){
        	throw new ArrayIndexOutOfBoundsException(bufferSize+bufferOffset);
        }
    	initialize();
    }
    
    public void setEnabled(boolean enabled){
    	bufferLocked = !enabled;
    }
  
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
        this.addMouseWheelListener(this);

        sb=new JScrollBar(JScrollBar.VERTICAL);
        sb.addAdjustmentListener(this);
        sb.setMinimum(0);
        sb.setMaximum(bufferSize/getLineas());

        JPanel p1,p2,p3;
        //centro
        p1=new JPanel(new BorderLayout(1,1));
        hex = new JHexEditorHEX(this);
        p1.add(hex,BorderLayout.CENTER);
        p1.add(new Columnas(),BorderLayout.NORTH);

        // izq.
        p2=new JPanel(new BorderLayout(1,1));
        p2.add(new Filas(),BorderLayout.CENTER);
        p2.add(new Caja(),BorderLayout.NORTH);

        // der
        p3=new JPanel(new BorderLayout(1,1));
        p3.add(sb,BorderLayout.EAST);
        ascii = new JHexEditorASCII(this);
        p3.add(ascii,BorderLayout.CENTER);
        p3.add(new Caja(),BorderLayout.NORTH);

        panel=new JPanel();
        panel.setLayout(new BorderLayout(1,1));
        panel.add(p1,BorderLayout.CENTER);
        panel.add(p2,BorderLayout.WEST);
        panel.add(p3,BorderLayout.EAST);

        this.setLayout(new BorderLayout(1,1));
        this.add(panel,BorderLayout.CENTER);
	}


    public void paint(Graphics g)
    {
        FontMetrics fn=getFontMetrics(font);
        Rectangle rec=this.getBounds();
        lineas=(rec.height/fn.getHeight())-1;
        int n=rowCount();//(int)Math.ceil(((double)buff.length/16.0));
        if(lineas>n) { lineas=n; inicio=0; }

        sb.setValues(getInicio(),+getLineas(),0,n);
        sb.setValueIsAdjusting(true);
        super.paint(g);
    }

    protected void actualizaCursor()
    {
        int n=(cursor/16);

        if(DEBUG) System.out.print("- "+inicio+"<"+n+"<"+(lineas+inicio)+"("+lineas+")");

        if(n<inicio) inicio=n;
        else if(n>=inicio+lineas) inicio=n-(lineas-1);

        if(DEBUG) System.out.println(" - "+inicio+"<"+n+"<"+(lineas+inicio)+"("+lineas+")");

        repaint();
    }
    
    protected int rowCount() {
		return (int)Math.ceil(((double)bufferSize/16.0));
	}


    protected int getInicio()
    {
        return inicio;
    }

    protected int getLineas()
    {
        return lineas;
    }

    protected void fondo(Graphics g,int x,int y,int s)
    {
        FontMetrics fn=getFontMetrics(font);
        g.fillRect(((fn.stringWidth(" ")+1)*x)+border,(fn.getHeight()*y)+border,((fn.stringWidth(" ")+1)*s),fn.getHeight()+1);
    }

    protected void cuadro(Graphics g,int x,int y,int s)
    {
        FontMetrics fn=getFontMetrics(font);
        g.drawRect(((fn.stringWidth(" ")+1)*x)+border,(fn.getHeight()*y)+border,((fn.stringWidth(" ")+1)*s),fn.getHeight()+1);
    }

    protected void printString(Graphics g,String s,int x,int y)
    {
        FontMetrics fn=getFontMetrics(font);
        g.drawString(s,((fn.stringWidth(" ")+1)*x)+border,((fn.getHeight()*(y+1))-fn.getMaxDescent())+border);
    }

    public void focusGained(FocusEvent e)
    {
        this.repaint();
    }

    public void focusLost(FocusEvent e)
    {
        this.repaint();
    }

    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        inicio=e.getValue();
        if(inicio<0) inicio=0;
        repaint();
    }

    public void mouseWheelMoved(MouseWheelEvent e)
    {
        inicio+=(e.getUnitsToScroll());
        if((inicio+lineas)>=rowCount()) inicio=(rowCount())-lineas;
        if(inicio<0) inicio=0;
        repaint();
    }

    public void keyPressed(KeyEvent e)
    {
        switch(e.getKeyCode())
        {
            case 33:    // rep
                if(cursor>=(16*lineas)) cursor-=(16*lineas);
                actualizaCursor();
                break;
            case 34:    // fin
                if(cursor<(bufferSize-(16*lineas))) cursor+=(16*lineas);
                actualizaCursor();
                break;
            case 35:    // fin
                cursor=bufferSize-1;
                actualizaCursor();
                break;
            case 36:    // ini
                cursor=0;
                actualizaCursor();
                break;
            case 37:    // <--
                if(cursor!=0) cursor--;
                actualizaCursor();
                break;
            case 38:    // <--
                if(cursor>15) cursor-=16;
                actualizaCursor();
                break;
            case 39:    // -->
                if(cursor!=(bufferSize-1)) cursor++;
                actualizaCursor();
                break;
            case 40:    // -->
                if(cursor<(bufferSize-16)) cursor+=16;
                actualizaCursor();
                break;
        }
    }

    private class Columnas extends JPanel{
		private static final long serialVersionUID = -1734199617526339842L;

		public Columnas()
        {
            this.setLayout(new BorderLayout(1,1));
        }
        public Dimension getPreferredSize()
        {
            return getMinimumSize();
        }

        public Dimension getMinimumSize()
        {
            Dimension d=new Dimension();
            FontMetrics fn=getFontMetrics(font);
            int h=fn.getHeight();
            int nl=1;
            d.setSize(((fn.stringWidth(" ")+1)*+((16*3)-1))+(border*2)+1,h*nl+(border*2)+1);
            return d;
        }

        public void paint(Graphics g)
        {
            Dimension d=getMinimumSize();
            g.setColor(Color.white);
            g.fillRect(0,0,d.width,d.height);
            g.setColor(Color.blue);
            g.setFont(font);

            for(int n=0;n<16;n++)
            {
                if(n==(cursor%16)) cuadro(g,n*3,0,2);
                String s="00"+Integer.toHexString(n).toUpperCase();
                s=s.substring(s.length()-2);
                printString(g,s,n*3,0);
            }
        }
    }

    private class Caja extends JPanel{
		private static final long serialVersionUID = -6124062720565016834L;

		public Dimension getPreferredSize()
        {
            return getMinimumSize();
        }

        public Dimension getMinimumSize()
        {
            Dimension d=new Dimension();
            FontMetrics fn=getFontMetrics(font);
            int h=fn.getHeight();
            d.setSize((fn.stringWidth(" ")+1)+(border*2)+1,h+(border*2)+1);
            return d;
        }

    }

    private class Filas extends JPanel{
		private static final long serialVersionUID = 8797347523486018051L;

		public Filas()
        {
            this.setLayout(new BorderLayout(1,1));
        }
        public Dimension getPreferredSize()
        {
            return getMinimumSize();
        }

        public Dimension getMinimumSize()
        {
            Dimension d=new Dimension();
            FontMetrics fn=getFontMetrics(font);
            int h=fn.getHeight();
            int nl=getLineas();
            d.setSize((fn.stringWidth(" ")+1)*(8)+(border*2)+1,h*nl+(border*2)+1);
            return d;
        }

        public void paint(Graphics g)
        {
            Dimension d=getMinimumSize();
            g.setColor(Color.white);
            g.fillRect(0,0,d.width,d.height);
            g.setColor(Color.blue);
            g.setFont(font);

            int ini=getInicio();
            int fin=ini+getLineas();
            int y=0;
            for(int n=ini;n<fin;n++)
            {
                if(n==(cursor/16)) cuadro(g,0,y,8);
                String s="0000000000000"+Integer.toHexString(n*16).toUpperCase();
                s=s.substring(s.length()-8);
                printString(g,s,0,y++);
            }
        }
    }

}
