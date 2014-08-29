package pandy.com;
import java.util.Hashtable;
/**
 * Created by IntelliJ IDEA.
 * User: Admin12
 * Date: 2005-11-25
 * Time: 15:33:43
 * To change this template use File | Settings | File Templates.
 */
public class PageValue {
    public Hashtable tempvalues;
    public int pageStyle=1;
    public PageValue(int pageStyle){
     this.pageStyle=pageStyle;
    }
    public void addValue(String name,String value){
        if(tempvalues==null) tempvalues=new Hashtable();
        tempvalues.put(name, value);
    }
    protected void finalize()  {
        try {
            tempvalues.clear();
            tempvalues=null;
        } catch (Exception e) {
        }
    }
}