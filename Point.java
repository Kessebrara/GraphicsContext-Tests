/**
 * A point on a two dimension surface.
 * 
 * @author Todd O'Dell
 * @version 14 May 2016
 */

import java.text.DecimalFormat;

public class Point
{
    private double x;
    private double y;
    static final DecimalFormat dc = new DecimalFormat("#.####");
    
    public Point()
    {
        this(0, 0);
    }
    
    public Point(double x, double y)
    {
        this.x = Double.parseDouble(dc.format(x));
        this.y = Double.parseDouble(dc.format(y));;
    }
    
    public Point(Point p)
    {
        this(p.getX(), p.getY());
    }
    
    public double angle(Point p)
    {
        return Math.atan2( p.getY() - y, p.getX() - x );
    }
    
    public double getX()
    {
        return x;
    }
    
    public double getY()
    {
        return y;
    }
    
    public void setLocation(double x, double y)
    {
        this.x = Double.parseDouble(dc.format(x));
        this.y = Double.parseDouble(dc.format(y));;
    }
    
    public void setLocation(Point p)
    {
        setLocation(p.getX(), p.getY());
    }
    
    public double distance(Point p)
    {
        double distance = Math.sqrt( Math.pow(p.getX() - x, 2) + Math.pow(p.getY() - y, 2) );
        
        return Double.parseDouble(dc.format(distance));
    }
    
    public double clockDirection(Point p, double angle)
    {
        if(angle >= 0 && angle(p) >= 0)
        {
            if(angle > angle(p))
            return -1;
            
            else
            return 1;
        }
        
        if(angle <= 0 && angle(p) >= 0)
        {
            if(Math.abs(angle) + angle(p) > Math.PI)
            return -1;
            
            else
            return 1;
        }
        
        if(angle <= 0 && angle(p) <= 0)
        {
            if(angle < angle(p))
            return 1;
            
            else
            return -1;
        }
        
        if(angle >= 0 && angle(p) <= 0)
        {
            if(angle + Math.abs(angle(p)) > Math.PI)
            return 1;
            
            else
            return -1;
        }
        
        return 0;
    }
    
    public double angleDifference(Point p, double angle)
    {
        if(angle >= 0 && angle(p) >= 0)
        return Math.abs(angle - angle(p));
        
        if(angle <= 0 && angle(p) <= 0)
        return Math.abs(Math.abs(angle) - Math.abs(angle(p)));
        
        if(angle <= 0 && angle(p) >= 0)
        {
            if(Math.abs(angle - angle(p)) > Math.PI)
            return Math.abs(angle - angle(p)) - ((Math.abs(angle - angle(p))) - Math.PI)*2;
            
            else
            return Math.abs(angle - angle(p));            
        }
        
        if(angle >= 0 && angle(p) <= 0)
        {
            if(Math.abs(angle - angle(p)) > Math.PI)
            return Math.abs(angle - angle(p)) - ((Math.abs(angle - angle(p))) - Math.PI)*2;
            
            else
            return Math.abs(angle - angle(p));    
        }
        
        return Math.abs(angle(p) - angle);
    }
    
    public void translatePolar(Point p, double distance)
    {
        double angle = angle(p);
        this.x += distance * Math.cos(angle);
        this.y += distance * Math.sin(angle);
    }
    
    @Override
    public String toString()
    {
        return "" + x + ", " + y;
    }
}