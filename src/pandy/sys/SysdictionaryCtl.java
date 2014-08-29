package pandy.sys;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2006-8-5
 * Time: 15:01:35
 * To change this template use File | Settings | File Templates.
 */
public class SysdictionaryCtl
{
    /**
     * 新建一个字典时所用到的方法
     * @param sqlscript
     * @param filename
     */
    public static String getDictionary(String sqlscript, String filename)
    {
        String pandy = "数据字典生成成功！";
        try
        {
            File pandyfile = new File(filename.substring(0, filename.lastIndexOf("\\") + 1));
            pandyfile.mkdirs();//建立输出目录
            pandyfile = new File(filename);  //输出文件路径


            if (sqlscript.indexOf("/**#") == -1 || sqlscript.indexOf("#**/") == -1)
            {
                pandy = "数据字典生成失败，可能脚本代码不满足要求";
            }
            else
            {
                String doc = getDoc(sqlscript);
                int k = sqlscript.indexOf("/**####");
                int k2 = sqlscript.indexOf("####**/");
                if (k != -1 && k2 != -1)
                {
                    doc = doc.replaceAll("##系统名称##", sqlscript.substring(k + 7, k2));
                }
                byte[] buffer = doc.getBytes();
                BufferedOutputStream bufferout = new BufferedOutputStream(new FileOutputStream(pandyfile));
                bufferout.write(buffer);
                bufferout.flush();
                bufferout.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return pandy;
    }

    /**
     * 读出文档的模块和生成字典
     * @param sqlnot
     */
    public static String getDoc(String sqlnot)
    {
        FileReader filereader;
        BufferedReader bufferedReader;
        StringBuffer doc = new StringBuffer();
        String tempdoc = "";
        try
        {
            int one = 2, two = 1, three = 1;
            filereader = new FileReader("properties/doc.ini");
            bufferedReader = new BufferedReader(filereader);
            while ((tempdoc = bufferedReader.readLine()) != null)
            {
                doc.append(tempdoc + '\n');
            }
            String lowsql = sqlnot.toLowerCase();
            if (lowsql.indexOf("####**/") != -1)
            {
                lowsql = lowsql.substring(lowsql.indexOf("####**/") + 7);
            }
            String onetable = "";
            String onegroup = "";
            String extkey = "";
            if (lowsql.indexOf("/**##") == -1 || lowsql.indexOf("##**/") == -1)  //当没有输入表所属于的模块的处理
            {
                while (lowsql.indexOf("/**#") != -1) //当只输入表时的处理
                {
                    onetable = lowsql.substring(lowsql.indexOf("#**/") + 4, lowsql.indexOf("constraint pk_"));
                    extkey = lowsql.substring(lowsql.indexOf("primary key") + 4, lowsql.indexOf(");"));
                    doc.append("<h3 style='margin-top:6.0pt;margin-right:0cm;margin-bottom:6.0pt;margin-left:\n" +
                            "0cm;text-indent:0cm;line-height:150%'><a name=\"_Toc142558776\"></a><a\n" +
                            "name=\"_Toc141538353\"><span style='mso-bookmark:_Toc142558776'><![if !supportLists]><b><span\n" +
                            "lang=EN-US style='font-family:宋体;mso-bidi-font-family:宋体'><span\n" +
                            "style='mso-list:Ignore'>" + one + "." + two + "." + three + "<span style='font:7.0pt \"Times New Roman\"'>&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                            "</span></span></span></b><![endif]><b><span style='font-family:宋体'>" + lowsql.substring(lowsql.indexOf("/**#") + 4, lowsql.indexOf("#**/")) + "（<span\n" +
                            "lang=EN-US>" + onetable.substring(onetable.indexOf("create table") + 12, onetable.indexOf("(")).trim() + "</span>）</span></b></span></a><b><span lang=EN-US\n" +
                            "style='font-family:宋体'><o:p></o:p></span></b></h3>\n" +
                            "\n" +
                            "<table class=MsoNormalTable border=1 cellspacing=0 cellpadding=0 width=562\n" +
                            " style='width:421.45pt;border-collapse:collapse;border:none;mso-border-alt:\n" +
                            " solid windowtext .5pt;mso-padding-alt:0cm 5.4pt 0cm 5.4pt;mso-border-insideh:\n" +
                            " .5pt solid windowtext;mso-border-insidev:.5pt solid windowtext'>\n" +
                            " <tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;height:21.3pt'>\n" +
                            "  <td width=111 valign=top style='width:83.4pt;border:solid windowtext 1.0pt;\n" +
                            "  mso-border-alt:solid windowtext .5pt;background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;\n" +
                            "  height:21.3pt'>\n" +
                            "  <p class=MsoNormal align=center style='text-align:center'><b><span\n" +
                            "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>字段<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                            "  </td>\n" +
                            "  <td width=104 valign=top style='width:77.95pt;border:solid windowtext 1.0pt;\n" +
                            "  border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n" +
                            "  solid windowtext .5pt;background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;\n" +
                            "  height:21.3pt'>\n" +
                            "  <p class=MsoNormal align=center style='text-align:center'><b><span\n" +
                            "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>字段名称<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                            "  </td>\n" +
                            "  <td valign=top style='border:solid windowtext 1.0pt;border-left:none;\n" +
                            "  mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                            "  background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;height:21.3pt'>\n" +
                            "  <p class=MsoNormal align=center style='text-align:center'><b><span\n" +
                            "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>字段类型长度<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                            "  </td>\n" +
                            "  <td width=81 valign=top style='width:61.05pt;border:solid windowtext 1.0pt;\n" +
                            "  border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n" +
                            "  solid windowtext .5pt;background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;\n" +
                            "  height:21.3pt'>\n" +
                            "  <p class=MsoNormal align=center style='text-align:center'><b><span\n" +
                            "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>是否为空<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                            "  </td>\n" +
                            "  <td width=47 valign=top style='width:35.4pt;border:solid windowtext 1.0pt;\n" +
                            "  border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n" +
                            "  solid windowtext .5pt;background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;\n" +
                            "  height:21.3pt'>\n" +
                            "  <p class=MsoNormal align=center style='text-align:center'><b><span\n" +
                            "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>主键<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                            "  </td>\n" +
                            "  <td width=101 valign=top style='width:75.85pt;border:solid windowtext 1.0pt;\n" +
                            "  border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n" +
                            "  solid windowtext .5pt;background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;\n" +
                            "  height:21.3pt'>\n" +
                            "  <p class=MsoNormal style='margin-right:-14.5pt;mso-para-margin-right:-1.32gd;\n" +
                            "  text-indent:14.6pt;mso-char-indent-count:1.32'><b><span style='mso-bidi-font-size:\n" +
                            "  10.5pt;font-family:宋体'>备注<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                            "  </td>\n" +
                            " </tr>\n");
                    onetable = onetable.substring(onetable.indexOf("(") + 1);
                    onetable = onetable.replaceAll("\n", "");
                    String onerow[] = onetable.split(",");
                    for (int i = 0; i < onerow.length - 1; i++)
                    {
                        String onecol[] = onerow[i].split(" ");
                        String twocol[] = onerow[i + 1].split(" ");
                        String notnull = "";
                        String extnot = "";
                        String mainkey = "";
                        String colname = "";
                        String colcnname = "";
                        String coltype = "";
                        int colindex = 0;
                        for (int j = 0; j < twocol.length; j++)   //得到字段意义
                        {
                            if (twocol[j].indexOf("/**") != -1)
                            {
                                colcnname = twocol[j].substring(twocol[j].indexOf("/**") + 3, twocol[j].indexOf("**/"));
                            }
                            else
                            {
                                continue;
                            }
                        }
                        for (int j = 0; j < onecol.length; j++)  //得到其它字段的名称
                        {
                            if (colindex == 0)    //得到字段名称
                            {
                                if (onecol[j].length() > 0 && onecol[j].indexOf("/**") == -1)
                                {
                                    colname = onecol[j];
                                    colindex++;
                                }
                                else
                                {
                                    continue;
                                }
                            }
                            else if (colindex == 1)  //得到字段类型
                            {
                                if (onecol[j].length() > 0)
                                {
                                    coltype = onecol[j];
                                    colindex++;
                                }
                                else
                                {
                                    continue;
                                }
                            }
                            else if (colindex == 2)  //得到字段是否为空或得到字段的备注
                            {
                                if (onecol[j].length() > 0 && onecol[j].equals("not"))
                                {
                                    notnull = "not null";
                                    break;
                                }
                                if (onecol[j].length() > 0 && onecol[j].equals("default"))
                                {
                                    extnot = onecol[j] + " ";
                                    colindex++;
                                }
                                else
                                {
                                    continue;
                                }
                            }
                            else if (colindex == 3)  //得到字段的备注的默认值　
                            {
                                if (onecol[j].length() > 0)
                                {
                                    extnot += onecol[j];
                                    break;
                                }
                                else
                                {
                                    continue;
                                }
                            }
                        }

                        if (extkey.indexOf(colname) != -1)
                        {
                            mainkey = "Y";
                        }

                        doc.append(" <tr style='mso-yfti-irow:1'>\n" +
                                "  <td width=111 valign=top style='width:83.4pt;border:solid windowtext 1.0pt;\n" +
                                "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                                "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                "  <p class=MsoNormal><span lang=EN-US style='mso-bidi-font-size:10.5pt;\n" +
                                "  font-family:宋体'>" + colname + "<o:p></o:p></span></p>\n" +
                                "  </td>\n" +
                                "  <td width=104 valign=top style='width:77.95pt;border-top:none;border-left:\n" +
                                "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
                                "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
                                "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                "  <p class=MsoNormal><span style='mso-bidi-font-size:10.5pt;font-family:宋体'>" + colcnname + "<span\n" +
                                "  lang=EN-US><o:p></o:p></span></span></p>\n" +
                                "  </td>\n" +
                                "  <td valign=top style='border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;\n" +
                                "  border-right:solid windowtext 1.0pt;mso-border-top-alt:solid windowtext .5pt;\n" +
                                "  mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                                "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                "  <p class=MsoNormal><span lang=EN-US style='mso-bidi-font-size:10.5pt;\n" +
                                "  font-family:宋体'>" + coltype + "<o:p></o:p></span></p>\n" +
                                "  </td>\n" +
                                "  <td width=81 valign=top style='width:61.05pt;border-top:none;border-left:\n" +
                                "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
                                "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
                                "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                "  <p class=MsoNormal align=center style='margin-top:0cm;margin-right:-5.4pt;\n" +
                                "  margin-bottom:6.0pt;margin-left:-15.2pt;mso-para-margin-top:0cm;mso-para-margin-right:\n" +
                                "  -.49gd;mso-para-margin-bottom:6.0pt;mso-para-margin-left:-1.38gd;text-align:\n" +
                                "  center;text-indent:15.2pt;mso-char-indent-count:1.38'><span lang=EN-US\n" +
                                "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>" + notnull + "<o:p></o:p></span></p>\n" +
                                "  </td>\n" +
                                "  <td width=47 valign=top style='width:35.4pt;border-top:none;border-left:none;\n" +
                                "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
                                "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
                                "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                "  <p class=MsoNormal align=center style='margin-top:0cm;margin-right:-12.45pt;\n" +
                                "  margin-bottom:6.0pt;margin-left:0cm;mso-para-margin-top:0cm;mso-para-margin-right:\n" +
                                "  -1.13gd;mso-para-margin-bottom:6.0pt;mso-para-margin-left:-.49gd;text-align:\n" +
                                "  center;text-indent:-5.4pt;mso-char-indent-count:-.49'><span lang=EN-US\n" +
                                "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>" + mainkey + "<o:p></o:p></span></p>\n" +
                                "  </td>\n" +
                                "  <td width=101 valign=top style='width:75.85pt;border-top:none;border-left:\n" +
                                "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
                                "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
                                "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                "  <p class=MsoNormal align=center style='margin-left:4.5pt;mso-para-margin-left:\n" +
                                "  .41gd;text-align:center;text-indent:2.4pt;mso-char-indent-count:.22'><span\n" +
                                "  lang=EN-US style='mso-bidi-font-size:10.5pt;font-family:宋体'>" + extnot + "<o:p>&nbsp;</o:p></span></p>\n" +
                                "  </td>\n" +
                                " </tr>\n");
                    }
                    doc.append("</table>"); //一个表结束
                    lowsql = lowsql.substring(lowsql.substring(lowsql.indexOf("/**#") + 4).indexOf("/**#") + lowsql.indexOf("/**#") + 4);    //指向下一个表格的开始
                }
            }
            else   //当有输入表所属于的模块的处理
            {
                while (lowsql.indexOf("/**##") != -1) //当有输入表所属于的模块的处理
                {
                    two++;
                    three = 0;

                    doc.append("<h2 style='margin-left:0cm;text-indent:0cm'><a name=\"_Toc142558778\"><a name=\"_Toc142558775\"></a><a\n" +
                            "name=\"_Toc141538352\"><span style='mso-bookmark:_Toc142558775'><![if !supportLists]><span\n" +
                            "lang=EN-US style='mso-fareast-font-family:Arial;mso-bidi-font-family:Arial'><span\n" +
                            "style='mso-list:Ignore'>" + one + "." + two + "<span style='font:7.0pt \"Times New Roman\"'>&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                            "</span></span></span><![endif]><span style='font-family:黑体;mso-ascii-font-family:\n" +
                            "Arial'>" + lowsql.substring(lowsql.indexOf("/**##") + 5, lowsql.indexOf("##**/")) + "</span></span></a></h2>");
                    lowsql = lowsql.substring(lowsql.indexOf("##**/") + 5);
                    while (lowsql.indexOf("/**#") != -1) //当只输入表时的处理
                    {
                        if (lowsql.indexOf("/**#") == lowsql.indexOf("/**##"))
                        {

                            break;
                        }
                        onetable = lowsql.substring(lowsql.indexOf("#**/") + 4, lowsql.indexOf("constraint pk_"));
                        extkey = lowsql.substring(lowsql.indexOf("primary key") + 4, lowsql.indexOf(");"));
                        doc.append("<h3 style='margin-top:6.0pt;margin-right:0cm;margin-bottom:6.0pt;margin-left:\n" +
                                "0cm;text-indent:0cm;line-height:150%'><a name=\"_Toc142558776\"></a><a\n" +
                                "name=\"_Toc141538353\"><span style='mso-bookmark:_Toc142558776'><![if !supportLists]><b><span\n" +
                                "lang=EN-US style='font-family:宋体;mso-bidi-font-family:宋体'><span\n" +
                                "style='mso-list:Ignore'>" + one + "." + two + "." + three + "<span style='font:7.0pt \"Times New Roman\"'>&nbsp;&nbsp;&nbsp;&nbsp;\n" +
                                "</span></span></span></b><![endif]><b><span style='font-family:宋体'>" + lowsql.substring(lowsql.indexOf("/**#") + 4, lowsql.indexOf("#**/")) + "（<span\n" +
                                "lang=EN-US>" + onetable.substring(onetable.indexOf("create table") + 12, onetable.indexOf("(")).trim() + "</span>）</span></b></span></a><b><span lang=EN-US\n" +
                                "style='font-family:宋体'><o:p></o:p></span></b></h3>\n" +
                                "\n" +
                                "<table class=MsoNormalTable border=1 cellspacing=0 cellpadding=0 width=562\n" +
                                " style='width:421.45pt;border-collapse:collapse;border:none;mso-border-alt:\n" +
                                " solid windowtext .5pt;mso-padding-alt:0cm 5.4pt 0cm 5.4pt;mso-border-insideh:\n" +
                                " .5pt solid windowtext;mso-border-insidev:.5pt solid windowtext'>\n" +
                                " <tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;height:21.3pt'>\n" +
                                "  <td width=111 valign=top style='width:83.4pt;border:solid windowtext 1.0pt;\n" +
                                "  mso-border-alt:solid windowtext .5pt;background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;\n" +
                                "  height:21.3pt'>\n" +
                                "  <p class=MsoNormal align=center style='text-align:center'><b><span\n" +
                                "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>字段<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                                "  </td>\n" +
                                "  <td width=104 valign=top style='width:77.95pt;border:solid windowtext 1.0pt;\n" +
                                "  border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n" +
                                "  solid windowtext .5pt;background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;\n" +
                                "  height:21.3pt'>\n" +
                                "  <p class=MsoNormal align=center style='text-align:center'><b><span\n" +
                                "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>字段名称<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                                "  </td>\n" +
                                "  <td valign=top style='border:solid windowtext 1.0pt;border-left:none;\n" +
                                "  mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                                "  background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;height:21.3pt'>\n" +
                                "  <p class=MsoNormal align=center style='text-align:center'><b><span\n" +
                                "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>字段类型长度<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                                "  </td>\n" +
                                "  <td width=81 valign=top style='width:61.05pt;border:solid windowtext 1.0pt;\n" +
                                "  border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n" +
                                "  solid windowtext .5pt;background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;\n" +
                                "  height:21.3pt'>\n" +
                                "  <p class=MsoNormal align=center style='text-align:center'><b><span\n" +
                                "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>是否为空<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                                "  </td>\n" +
                                "  <td width=47 valign=top style='width:35.4pt;border:solid windowtext 1.0pt;\n" +
                                "  border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n" +
                                "  solid windowtext .5pt;background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;\n" +
                                "  height:21.3pt'>\n" +
                                "  <p class=MsoNormal align=center style='text-align:center'><b><span\n" +
                                "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>主键<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                                "  </td>\n" +
                                "  <td width=101 valign=top style='width:75.85pt;border:solid windowtext 1.0pt;\n" +
                                "  border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n" +
                                "  solid windowtext .5pt;background:#E0E0E0;padding:0cm 5.4pt 0cm 5.4pt;\n" +
                                "  height:21.3pt'>\n" +
                                "  <p class=MsoNormal style='margin-right:-14.5pt;mso-para-margin-right:-1.32gd;\n" +
                                "  text-indent:14.6pt;mso-char-indent-count:1.32'><b><span style='mso-bidi-font-size:\n" +
                                "  10.5pt;font-family:宋体'>备注<span lang=EN-US><o:p></o:p></span></span></b></p>\n" +
                                "  </td>\n" +
                                " </tr>\n");
                        onetable = onetable.substring(onetable.indexOf("(") + 1);
                        onetable = onetable.replaceAll("\n", "");
                        String onerow[] = onetable.split(",");
                        for (int i = 0; i < onerow.length - 1; i++)
                        {
                            String onecol[] = onerow[i].split(" ");
                            String twocol[] = onerow[i + 1].split(" ");
                            String notnull = "";
                            String extnot = "";
                            String mainkey = "";
                            String colname = "";
                            String colcnname = "";
                            String coltype = "";
                            int colindex = 0;
                            for (int j = 0; j < twocol.length; j++)   //得到字段意义
                            {
                                if (twocol[j].indexOf("/**") != -1)
                                {
                                    colcnname = twocol[j].substring(twocol[j].indexOf("/**") + 3, twocol[j].indexOf("**/"));
                                }
                                else
                                {
                                    continue;
                                }
                            }
                            for (int j = 0; j < onecol.length; j++)  //得到其它字段的名称
                            {
                                if (colindex == 0)    //得到字段名称
                                {
                                    if (onecol[j].length() > 0 && onecol[j].indexOf("/**") == -1)
                                    {
                                        colname = onecol[j];
                                        colindex++;
                                    }
                                    else
                                    {
                                        continue;
                                    }
                                }
                                else if (colindex == 1)  //得到字段类型
                                {
                                    if (onecol[j].length() > 0)
                                    {
                                        coltype = onecol[j];
                                        colindex++;
                                    }
                                    else
                                    {
                                        continue;
                                    }
                                }
                                else if (colindex == 2)  //得到字段是否为空或得到字段的备注
                                {
                                    if (onecol[j].length() > 0 && onecol[j].equals("not"))
                                    {
                                        notnull = "not null";
                                        break;
                                    }
                                    if (onecol[j].length() > 0 && onecol[j].equals("default"))
                                    {
                                        extnot = onecol[j] + " ";
                                        colindex++;
                                    }
                                    else
                                    {
                                        continue;
                                    }
                                }
                                else if (colindex == 3)  //得到字段的备注的默认值　
                                {
                                    if (onecol[j].length() > 0)
                                    {
                                        extnot += onecol[j];
                                        break;
                                    }
                                    else
                                    {
                                        continue;
                                    }
                                }
                            }
                            if (extkey.indexOf(onecol[1]) != -1)
                            {
                                mainkey = "Y";
                            }

                            doc.append(" <tr style='mso-yfti-irow:1'>\n" +
                                    "  <td width=111 valign=top style='width:83.4pt;border:solid windowtext 1.0pt;\n" +
                                    "  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                                    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                    "  <p class=MsoNormal><span lang=EN-US style='mso-bidi-font-size:10.5pt;\n" +
                                    "  font-family:宋体'>" + colname + "<o:p></o:p></span></p>\n" +
                                    "  </td>\n" +
                                    "  <td width=104 valign=top style='width:77.95pt;border-top:none;border-left:\n" +
                                    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
                                    "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
                                    "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                    "  <p class=MsoNormal><span style='mso-bidi-font-size:10.5pt;font-family:宋体'>" + colcnname + "<span\n" +
                                    "  lang=EN-US><o:p></o:p></span></span></p>\n" +
                                    "  </td>\n" +
                                    "  <td valign=top style='border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;\n" +
                                    "  border-right:solid windowtext 1.0pt;mso-border-top-alt:solid windowtext .5pt;\n" +
                                    "  mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
                                    "  padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                    "  <p class=MsoNormal><span lang=EN-US style='mso-bidi-font-size:10.5pt;\n" +
                                    "  font-family:宋体'>" + coltype + "<o:p></o:p></span></p>\n" +
                                    "  </td>\n" +
                                    "  <td width=81 valign=top style='width:61.05pt;border-top:none;border-left:\n" +
                                    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
                                    "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
                                    "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                    "  <p class=MsoNormal align=center style='margin-top:0cm;margin-right:-5.4pt;\n" +
                                    "  margin-bottom:6.0pt;margin-left:-15.2pt;mso-para-margin-top:0cm;mso-para-margin-right:\n" +
                                    "  -.49gd;mso-para-margin-bottom:6.0pt;mso-para-margin-left:-1.38gd;text-align:\n" +
                                    "  center;text-indent:15.2pt;mso-char-indent-count:1.38'><span lang=EN-US\n" +
                                    "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>" + notnull + "<o:p></o:p></span></p>\n" +
                                    "  </td>\n" +
                                    "  <td width=47 valign=top style='width:35.4pt;border-top:none;border-left:none;\n" +
                                    "  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
                                    "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
                                    "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                    "  <p class=MsoNormal align=center style='margin-top:0cm;margin-right:-12.45pt;\n" +
                                    "  margin-bottom:6.0pt;margin-left:0cm;mso-para-margin-top:0cm;mso-para-margin-right:\n" +
                                    "  -1.13gd;mso-para-margin-bottom:6.0pt;mso-para-margin-left:-.49gd;text-align:\n" +
                                    "  center;text-indent:-5.4pt;mso-char-indent-count:-.49'><span lang=EN-US\n" +
                                    "  style='mso-bidi-font-size:10.5pt;font-family:宋体'>" + mainkey + "<o:p></o:p></span></p>\n" +
                                    "  </td>\n" +
                                    "  <td width=101 valign=top style='width:75.85pt;border-top:none;border-left:\n" +
                                    "  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
                                    "  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
                                    "  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt'>\n" +
                                    "  <p class=MsoNormal align=center style='margin-left:4.5pt;mso-para-margin-left:\n" +
                                    "  .41gd;text-align:center;text-indent:2.4pt;mso-char-indent-count:.22'><span\n" +
                                    "  lang=EN-US style='mso-bidi-font-size:10.5pt;font-family:宋体'>" + extnot + "<o:p>&nbsp;</o:p></span></p>\n" +
                                    "  </td>\n" +
                                    " </tr>\n");
                        }
                        doc.append("</table>"); //一个表结束
                        lowsql = lowsql.substring(lowsql.substring(lowsql.indexOf("/**#") + 4).indexOf("/**#") + lowsql.indexOf("/**#") + 4);    //指向下一个表格的开始

                    }
                }
            }


        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        doc.append("<p class=MsoNormal><span lang=EN-US><o:p>&nbsp;</o:p></span></p>\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>");

        return doc.toString();
    }

    /**
     * 当选择追加时的处理
     * @param sqlscript
     * @param filename
     */
    public static String getModifD(String sqlscript, String filename)
    {
        String pandy = "数据字典追加成功！";
        try
        {
            File pandyfile = null;
            pandyfile = new File(filename);  //输出文件路径


            if (sqlscript.indexOf("/**#") == -1 || sqlscript.indexOf("#**/") == -1)
            {
                pandy = "数据字典追加失败，可能脚本代码不满足要求";
            }
            else
            {
                String doc = getMondifDoc(sqlscript,filename);
                byte[] buffer = doc.getBytes();
                BufferedOutputStream bufferout = new BufferedOutputStream(new FileOutputStream(pandyfile));
                bufferout.write(buffer);
                bufferout.flush();
                bufferout.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return pandy;
    }

    public static String getMondifDoc(String sqlnot,String filename)
    {
        FileReader filereader;
        BufferedReader bufferedReader;
        StringBuffer doc = new StringBuffer();
        String tempdoc = "";
        try
        {
            int one = 2, two = 1, three = 1;
            filereader = new FileReader(filename);
            bufferedReader = new BufferedReader(filereader);
            while ((tempdoc = bufferedReader.readLine()) != null)
            {
                doc.append(tempdoc + '\n');
            }
            String lowsql = sqlnot.toLowerCase();
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return doc.toString();
    }

}
