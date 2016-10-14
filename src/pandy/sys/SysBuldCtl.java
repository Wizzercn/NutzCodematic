package pandy.sys;

import pandy.db.DBConnector;
import pandy.db.DBUtil;
import pandy.util.DateUtil;
import pandy.file.FileUtil;
import pandy.com.Globa;

import java.sql.*;

public class SysBuldCtl {
    public static String[] getClassCode(Connection con, String pagename, String tablename, String author, String modelname) {
        String pandy[] = new String[7];
        /**
         * pandy[0]为生成的基本类，pandy[1]为生成的控制类
         */
        String topstr = "package " + pagename + ".models." + modelname + ";\n\nimport cn.wizzer.common.base.Model;\nimport org.nutz.dao.entity.annotation.*;\n\nimport java.io.Serializable;\n";
        try {
            int i = 0;
            int dbtype = 0;   //默认为SQLSERVER类型
            int pk = -1;
            String pkname = "";
            Statement stmt = con.createStatement();
            System.out.println("drivername:" + con.getMetaData().getDriverName());
            if (con.getMetaData().getDriverName().toLowerCase().indexOf("oracle") != -1)  //判断是不是ORACLE
            {
                dbtype = 1;
            }
            DatabaseMetaData dbMeta = con.getMetaData();
            ResultSet pkRSet = dbMeta.getPrimaryKeys(null, null, tablename);
            if (pkRSet.next()) {
                pkname = ((String) pkRSet.getObject(4)).toLowerCase();
            }
            ResultSet result = stmt.executeQuery("select * from " + tablename);
            ResultSetMetaData rsmd = result.getMetaData();
            String listname[][] = new String[rsmd.getColumnCount()][2];     //存放列名和列类型
            String rsname[] = new String[rsmd.getColumnCount()];
            String getset[][] = new String[rsmd.getColumnCount()][2];
            int type = 4;
            String sname = tablename.toUpperCase().substring(0, 1) + tablename.toLowerCase().substring(1);
            String xname = tablename.toLowerCase();
            String mname = tablename.toUpperCase().substring(0, 1) + tablename.toLowerCase().substring(1);
            if (tablename.indexOf("_") > 0) {
                String[] s = tablename.split("_");//shop_goods_product  ShopGoodsProduct
                String temp = "";
                for (String s1 : s) {
                    temp = temp + s1.toUpperCase().substring(0, 1) + s1.toLowerCase().substring(1);
                }
                sname = temp;
                xname = temp.toLowerCase().substring(0, 1) + temp.substring(1);
            }
            pandy[0] = mname + " extends Model implements Serializable {\n\tprivate static final long serialVersionUID = 1L;\n\t";
            /**
             * 得到所有的列名
             */
            for (i = 1; i <= rsmd.getColumnCount(); i++) {
                listname[i - 1][0] = rsmd.getColumnName(i).toLowerCase();
                type = rsmd.getColumnType(i);
                System.out.println(listname[i - 1][0] + ":" + type); //输出每种类型编号
                String temp = "";
                //完成各种类型的判断设置基本类的GET、SET方法。
                if (type == 4 || type == 5 || type == 2 || type == -7) {
                    if (pkname.equals(listname[i - 1][0].toLowerCase())) {
                        pk = 1;
                        temp = "@Id\n\t@Prev({\n\t\t@SQL(db = DB.ORACLE, value=\"SELECT " + tablename.toUpperCase() + "_S.nextval FROM dual\")\n\t})\n\t";
                        topstr += "import org.nutz.dao.entity.annotation.Id;\nimport org.nutz.dao.entity.annotation.Prev;\nimport org.nutz.dao.entity.annotation.SQL;import org.nutz.dao.DB;\n";
                    }
                    listname[i - 1][1] = "Integer ";
                    pandy[0] += "@Column\n\t" + temp + "private Integer " + listname[i - 1][0] + ";\n\t";
                    getset[i - 1][0] = "\tpublic Integer get" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "()\n\t{\n\t\treturn " + listname[i - 1][0] + ";\n\t}\n";
                    getset[i - 1][1] = "\tpublic void set" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "(Integer " + listname[i - 1][0] + ")\n\t{\n\t\tthis." + listname[i - 1][0] + "=" + listname[i - 1][0] + ";\n\t}\n";
                } else if (type == -5) {
                    if (pkname.equals(listname[i - 1][0].toLowerCase())) {
                        pk = 3;
                        temp = "@Id\n\t@Prev({\n\t\t@SQL(db = DB.ORACLE, value=\"SELECT " + tablename.toUpperCase() + "_S.nextval FROM dual\")\n\t})\n\t";
                        topstr += "import org.nutz.dao.entity.annotation.Id;\nimport org.nutz.dao.entity.annotation.Prev;\nimport org.nutz.dao.entity.annotation.SQL;import org.nutz.dao.DB;\n";

                    }
                    listname[i - 1][1] = "Long ";
                    pandy[0] += "@Column\n\t" + temp + "private Long " + listname[i - 1][0] + ";\n\t";
                    getset[i - 1][0] = "\tpublic Long get" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "()\n\t{\n\t\treturn " + listname[i - 1][0] + ";\n\t}\n";
                    getset[i - 1][1] = "\tpublic void set" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "(Long " + listname[i - 1][0] + ")\n\t{\n\t\tthis." + listname[i - 1][0] + "=" + listname[i - 1][0] + ";\n\t}\n";

                } else if (type == 6 || type == 7) //mssql float型
                {
                    listname[i - 1][1] = "double ";
                    pandy[0] += "@Column\n\tprivate double " + listname[i - 1][0] + ";\n\t";
                    getset[i - 1][0] = "\tpublic double get" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "()\n\t{\n\t\treturn " + listname[i - 1][0] + ";\n\t}\n";
                    getset[i - 1][1] = "\tpublic void set" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "(double " + listname[i - 1][0] + ")\n\t{\n\t\tthis." + listname[i - 1][0] + "=" + listname[i - 1][0] + ";\n\t}\n";

                } else if (type == 12) {

                    if (pkname.equals(listname[i - 1][0].toLowerCase())) {
                        pk = 2;
                        temp = "@Name\n\t@Prev(els = {@EL(\"uuid()\")})\n\t";
                        topstr += "import org.nutz.dao.entity.annotation.Name;\n";
                    }
                    listname[i - 1][1] = "String ";
                    pandy[0] += "@Column\n\t" + temp + "private String " + listname[i - 1][0] + ";\n\t";
//                    rsname[i - 1] = "\t\t\t" + listname[i - 1][0] + "=rs.getString(\"" + listname[i - 1][0] + "\");\n";
                    getset[i - 1][0] = "\tpublic String get" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "()\n\t{\n\t\treturn " + listname[i - 1][0] + ";\n\t}\n";
                    getset[i - 1][1] = "\tpublic void set" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "(String " + listname[i - 1][0] + ")\n\t{\n\t\tthis." + listname[i - 1][0] + "=" + listname[i - 1][0] + ";\n\t}\n";
                } else if (type == 19)   //MYSQL 日期类型
                {
                    listname[i - 1][1] = "java.sql.Date ";
                    pandy[0] += "@Column\n\tprivate java.sql.Date " + listname[i - 1][0] + ";\n\t";
                    getset[i - 1][0] = "\tpublic java.sql.Date get" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "()\n\t{\n\t\treturn " + listname[i - 1][0] + ";\n\t}\n";
                    getset[i - 1][1] = "\tpublic void set" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "(java.sql.Date " + listname[i - 1][0] + ")\n\t{\n\t\tthis." + listname[i - 1][0] + "=" + listname[i - 1][0] + ";\n\t}\n";

                } else if (type == 93)   //为ORACLE时间类的的处理
                {
                    listname[i - 1][1] = "java.sql.Date ";
                    pandy[0] += "@Column\n\tprivate java.sql.Date " + listname[i - 1][0] + ";\n\t";
                    getset[i - 1][0] = "\tpublic java.sql.Date get" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "()\n\t{\n\t\treturn " + listname[i - 1][0] + ";\n\t}\n";
                    getset[i - 1][1] = "\tpublic void set" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "(java.sql.Date " + listname[i - 1][0] + ")\n\t{\n\t\tthis." + listname[i - 1][0] + "=" + listname[i - 1][0] + ";\n\t}\n";

                } else if (type == 2005)   //ORCALE大字段处理
                {
                    listname[i - 1][1] = "Clob ";
                    pandy[0] += "@Column\n\tprivate String " + listname[i - 1][0] + ";\n\t";
                    getset[i - 1][0] = "\tpublic String get" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "()\n\t{\n\t\treturn " + listname[i - 1][0] + ";\n\t}\n";
                    getset[i - 1][1] = "\tpublic void set" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "(String " + listname[i - 1][0] + ")\n\t{\n\t\tthis." + listname[i - 1][0] + "=" + listname[i - 1][0] + ";\n\t}\n";
                } else {
                    listname[i - 1][1] = "String ";
                    pandy[0] += "@Column\n\tprivate String " + listname[i - 1][0] + ";\n\t";
                    getset[i - 1][0] = "\tpublic String get" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "()\n\t{\n\t\treturn " + listname[i - 1][0] + ";\n\t}\n";
                    getset[i - 1][1] = "\tpublic void set" + listname[i - 1][0].toUpperCase().substring(0, 1) + listname[i - 1][0].toLowerCase().substring(1) + "(String " + listname[i - 1][0] + ")\n\t{\n\t\tthis." + listname[i - 1][0] + "=" + listname[i - 1][0] + ";\n\t}\n";
                }

            }
            topstr += "/**\n" +
                    "* @author " + author + "\n" +
                    "* @time   " + DateUtil.date2str(new java.util.Date()) + "\n" +
                    "*/\n@Table(\"" + tablename + "\")\npublic class ";
            for (i = 0; i < listname.length; i++) {
                pandy[0] += getset[i][0] + getset[i][1];
            }
            pandy[0] = topstr + pandy[0] + "\n}";
            //Service
            pandy[1] = "";
            pandy[1] += "package " + pagename + ".services." + modelname + ";\n";
            pandy[1] += "\nimport cn.wizzer.common.base.Service;";
            pandy[1] += "\nimport " + pagename + ".models." + modelname + "." + mname + ";";
            pandy[1] += "\nimport org.nutz.dao.Dao;";
            pandy[1] += "\nimport org.nutz.ioc.loader.annotation.IocBean;";
            pandy[1] += "\n\n";
            pandy[1] += "/**\n * @author " + author + "\n * @time " + DateUtil.date2str(new java.util.Date()) + "\n * \n */";
            pandy[1] += "\n@IocBean(args = {\"refer:dao\"})";
            pandy[1] += "\npublic class " + sname + "Service extends Service<" + mname + "> {";
            pandy[1] += "\n\t\tpublic " + sname + "Service(Dao dao) {";
            pandy[1] += "\n\t\t\t\tsuper(dao);";
            pandy[1] += "\n\t\t}";
            pandy[1] += "\n}";
            String pktype = "long";
            if (pk == 2) {
                pktype = "String";
            }
            //Controller页面代码生成
            pandy[2] = "";
            pandy[2] += "package " + pagename + ".controllers.platform." + modelname + ";\n";
            pandy[2] += "\nimport cn.wizzer.common.annotation.SLog;";
            pandy[2] += "\nimport cn.wizzer.common.base.Result;";
            pandy[2] += "\nimport cn.wizzer.common.filter.PrivateFilter;";
            pandy[2] += "\nimport cn.wizzer.common.page.DataTableColumn;";
            pandy[2] += "\nimport cn.wizzer.common.page.DataTableOrder;";
            pandy[2] += "\nimport org.apache.shiro.authz.annotation.RequiresAuthentication;";
            pandy[2] += "\nimport org.apache.shiro.authz.annotation.RequiresPermissions;";
            pandy[2] += "\nimport org.nutz.dao.Cnd;";
            pandy[2] += "\nimport org.nutz.ioc.loader.annotation.Inject;";
            pandy[2] += "\nimport org.nutz.ioc.loader.annotation.IocBean;";
            pandy[2] += "\nimport org.nutz.log.Log;";
            pandy[2] += "\nimport org.nutz.log.Logs;";
            pandy[2] += "\nimport org.nutz.mvc.annotation.*;\n";
            pandy[2] += "\nimport javax.servlet.http.HttpServletRequest;";
            pandy[2] += "\nimport java.util.List;\n";
            pandy[2] += "\nimport " + pagename + ".models." + modelname + "." + mname + ";";
            pandy[2] += "\nimport " + pagename + ".services." + modelname + "." + sname + "Service;";
            pandy[2] += "\n\n";
            pandy[2] += "/**\n * @author " + author + "\n * @time " + DateUtil.date2str(new java.util.Date()) + "\n * \n */";
            pandy[2] += "\n@IocBean";
            pandy[2] += "\n@At(\"/platform/" + modelname.toLowerCase().replace(".", "/") + "/" + sname.toLowerCase() + "\")";
            pandy[2] += "\n@Filters({ @By(type = PrivateFilter.class)})";
            pandy[2] += "\npublic class " + sname + "Controller {\nprivate static final Log log = Logs.get();";
            pandy[2] += "\n\t@Inject";
            pandy[2] += "\n\t" + sname + "Service " + xname + "Service;\n";
            pandy[2] += "\n\t@At(\"\")";
            pandy[2] += "\n\t@Ok(\"beetl:/platform/" + modelname.toLowerCase().replace(".", "/") + "/index.html\")";
            pandy[2] += "\n\t@RequiresAuthentication";
            pandy[2] += "\n\tpublic void index() {";
            pandy[2] += "\n\t\t";
            pandy[2] += "\n\t}";
            pandy[2] += "\n\t";
            pandy[2] += "\n\t@At";
            pandy[2] += "\n\t@Ok(\"beetl:/platform/" + modelname.toLowerCase().replace(".", "/") + "/add.html\")";
            pandy[2] += "\n\t@RequiresAuthentication";
            pandy[2] += "\n\tpublic void add() {";
            pandy[2] += "\n\t";
            pandy[2] += "\n\t}";
            pandy[2] += "\n\t";
            pandy[2] += "\n\t@At";
            pandy[2] += "\n\t@Ok(\"json\")";
            pandy[2] += "\n\t@RequiresPermissions(\"platform." + modelname + ".add\")";
            pandy[2] += "\n\t@SLog(tag = \"Add\", msg = \"Add:" + tablename + "\")";
            pandy[2] += "\n\tpublic Object addDo(@Param(\"..\") " + mname + " " + tablename.toLowerCase() + ", HttpServletRequest req) {";
            pandy[2] += "\n\t\ttry {";
            pandy[2] += "\n\t\t\t" + xname + "Service.insert(" + tablename.toLowerCase() + ");";
            pandy[2] += "\n\t\t\treturn Result.success(\"system.success\");";
            pandy[2] += "\n\t\t} catch (Exception e) {";
            pandy[2] += "\n\t\t\treturn Result.error(\"system.error\");";
            pandy[2] += "\n\t\t}";
            pandy[2] += "\n\t}";
            pandy[2] += "\n\t";
            pandy[2] += "\n\t@At(\"/edit/?\")";
            pandy[2] += "\n\t@Ok(\"beetl:/platform/" + modelname.toLowerCase().replace(".", "/") + "/edit.html\")";
            pandy[2] += "\n\t@RequiresAuthentication";
            pandy[2] += "\n\tpublic Object edit(" + pktype + " id) {";
            pandy[2] += "\n\t\treturn " + xname + "Service.fetch(id);";
            pandy[2] += "\n\t}";
            pandy[2] += "\n\t";
            pandy[2] += "\n\t@At";
            pandy[2] += "\n\t@Ok(\"json\")";
            pandy[2] += "\n\t@RequiresPermissions(\"platform." + modelname + ".edit\")";
            pandy[2] += "\n\t@SLog(tag = \"Edit\", msg = \"Edit:" + tablename + "\")";
            pandy[2] += "\n\tpublic Object editDo(@Param(\"..\") " + mname + " " + tablename.toLowerCase() + ", HttpServletRequest req) {";
            pandy[2] += "\n\t\ttry {";
            pandy[2] += "\n\t\t\t" + xname + "Service.updateIgnoreNull(" + tablename.toLowerCase() + ");";
            pandy[2] += "\n\t\t\treturn Result.success(\"system.success\");";
            pandy[2] += "\n\t\t} catch (Exception e) {";
            pandy[2] += "\n\t\t\treturn Result.error(\"system.error\");";
            pandy[2] += "\n\t\t}";
            pandy[2] += "\n\t}";
            pandy[2] += "\n\t";
            pandy[2] += "\n\t@At({\"/delete\",\"/delete/?\"})";
            pandy[2] += "\n\t@Ok(\"json\")";
            pandy[2] += "\n\t@RequiresPermissions(\"platform." + modelname + ".delete\")";
            pandy[2] += "\n\t@SLog(tag = \"Delete\", msg = \"Delete:" + tablename + "\")";
            pandy[2] += "\n\tpublic Object delete(" + pktype + " id,@Param(\"ids\") " + pktype + "[] ids, HttpServletRequest req) {";
            pandy[2] += "\n\t\ttry {";
            pandy[2] += "\n\t\t\tif(ids!=null&&ids.length>0){";
            pandy[2] += "\n\t\t\t\t" + xname + "Service.deleteByIds(ids);";
            pandy[2] += "\n\t\t\t}else{";
            pandy[2] += "\n\t\t\t\t" + xname + "Service.delete(id);";
            pandy[2] += "\n\t\t\t}";
            pandy[2] += "\n\t\t\treturn Result.success(\"system.success\");";
            pandy[2] += "\n\t\t} catch (Exception e) {";
            pandy[2] += "\n\t\t\treturn Result.error(\"system.error\");";
            pandy[2] += "\n\t\t}";
            pandy[2] += "\n\t}";
            pandy[2] += "\n\t";
            pandy[2] += "\n\t@At";
            pandy[2] += "\n\t@Ok(\"json:full\")";
            pandy[2] += "\n\t@RequiresAuthentication";
            pandy[2] += "\n\tpublic Object data(@Param(\"length\") int length, @Param(\"start\") int start, @Param(\"draw\") int draw, @Param(\"::order\") List<DataTableOrder> order, @Param(\"::columns\") List<DataTableColumn> columns) {";
            pandy[2] += "\n\t\tCnd cnd = Cnd.NEW();";
            pandy[2] += "\n\t\treturn " + xname + "Service.data(length, start, draw, order, columns, cnd, null);";
            pandy[2] += "\n\t}";
            pandy[2] += "\n\n";
            pandy[2] += "}";
            pandy[2] += "";

            /**
             * index.html
             */
            pandy[3] = FileUtil.getFileCnt("templete//index.html");
            String th = "";
            String table = "";
            String tpath = "/platform/" + modelname.toLowerCase().replace(".", "/") + "/" + sname.toLowerCase();
            for (i = 0; i < listname.length; i++) {
                th += "\t\t\t\t<th>" + listname[i][0] + "</th>\n";
                if (i == listname.length - 1) {
                    table += "\t\t\t\t{\"data\": \"" + listname[i][0] + "\", \"bSortable\": true}\n";
                } else {
                    table += "\t\t\t\t{\"data\": \"" + listname[i][0] + "\", \"bSortable\": true},\n";
                }
            }
            pandy[3] = pandy[3].replace("#th#", th).replace("#tablejs#", table).replaceAll("#path#", tpath);

            /**
             * add.html
             */
            pandy[4] = FileUtil.getFileCnt("templete//add.html");
            String div = "";
            for (i = 0; i < listname.length; i++) {
                div += "\t\t\t\t\t\t<div class=\"form-group\">\n";
                div += "\t\t\t\t\t\t\t<label for=\"" + listname[i][0] + "\" class=\"col-sm-2 control-label\">" + listname[i][0] + "</label>\n";
                div += "\t\t\t\t\t\t\t<div class=\"col-sm-8\">\n";
                div += "\t\t\t\t\t\t\t\t<input type=\"text\" id=\"" + listname[i][0] + "\" class=\"form-control\" name=\"" + listname[i][0] + "\" data-parsley-required=\"true\"\n" +
                        "                                       placeholder=\"\">\n";
                div += "\t\t\t\t\t\t\t</div>\n";
                div += "\t\t\t\t\t\t</div>\n";

            }
            pandy[4] = pandy[4].replace("#div#", div).replaceAll("#path#", tpath);
            /**
             * edit.html
             */
            pandy[5] = FileUtil.getFileCnt("templete//edit.html");
            div = "";
            for (i = 0; i < listname.length; i++) {
                div += "\t\t\t\t\t\t<div class=\"form-group\">\n";
                div += "\t\t\t\t\t\t\t<label for=\"" + listname[i][0] + "\" class=\"col-sm-2 control-label\">" + listname[i][0] + "</label>\n";
                div += "\t\t\t\t\t\t\t<div class=\"col-sm-8\">\n";
                div += "\t\t\t\t\t\t\t\t<input type=\"text\" id=\"" + listname[i][0] + "\" class=\"form-control\" name=\"" + listname[i][0] + "\" data-parsley-required=\"true\"\n" +
                        "                                       placeholder=\"\" value=\"${obj." + listname[i][0] + "!}\">\n";
                div += "\t\t\t\t\t\t\t</div>\n";
                div += "\t\t\t\t\t\t</div>\n";

            }
            pandy[5] = pandy[5].replace("#div#", div).replaceAll("#pkname#", pkname).replaceAll("#path#", tpath);
            result.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return pandy;
    }
}
