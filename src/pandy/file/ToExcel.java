package pandy.file;

/**
 * Created by IntelliJ IDEA.
 * User: Admin12
 * Date: 2006-1-19
 * Time: 15:58:48
 * To change this template use File | Settings | File Templates.
 */
public class ToExcel
{
	public static String SaveToExcel(String title[],Object data[][])
	{
		String exceldata = "<table width='100%'>" +"<tr>" +"<td><table ><tr>";
		int row=data.length,col=data[0].length;
		for(int i=0;i<col;i++)
		{
			if(i<=title.length)
			exceldata+="<td>"+title[i]+"</td>";
		}
		exceldata+="</tr>";
		for(int i=0;i<row;i++)
		{
			exceldata+="<tr>";
			for(int j=0;j<col;j++)
			{
				exceldata+="<td align=center>"+data[i][j]+"</td>";
			}
			exceldata+="</tr>";
		}
		exceldata+="</td></tr></table>";
		System.out.println(exceldata);
		return exceldata;
	}
}
