import javax.swing.event.*;

import pandy.db.DBConnector;
import pandy.sys.*;
import pandy.util.DateUtil;
import pandy.util.StringUtil;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Vector;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.*;
/*
 * Created by JFormDesigner on Sun Jul 30 12:20:03 CST 2006
 */


/**
 * @author pandy pandy
 */
public class pandy extends JFrame {
    private int schemeselect = 0;  //当前所选择的数据库
    private Connection con = null;   //数据库连接
    private ResultSet result = null;  //定义一个结果集
    private Statement stmt = null;    //定义一个状态集
    private ResultSetMetaData rsmd = null;   //定义一个结果栅格集
    private DatabaseMetaData dbmd = null;////定义一个数据栅格集
    private Vector tableName = new Vector(), viewName = new Vector(), IndexName = new Vector(), triggerName = new Vector(), ProceduresName = new Vector(); //表、引索、视图、触发器、存储过程的列表数据
    private int sysrowselect = -1; //系统数据源的默认选择
    private BufferedOutputStream bufferout;
    private Object noteinfo[][] = new Object[40][5];//存数据源向量
    private FileDialog filedialog = new FileDialog(this, "选择路径对话框", FileDialog.SAVE);
    private File pandyfile;
    private File beanfile;
    private File servicefile;//建立输出目录
    private File controllerfile;
    private File viewfile;
    private String onetablename = "";
    private int pagesize = 40, curpage = 1, pagecount = 1, curpagecount = 1;

    public pandy() {

        initComponents();
        mycard.show(JworkPanel, "card1");
        setVisible(true);
    }

    private void flushtable() {
        try {
            onetablename = JLitemTableList.getSelectedValue().toString();
        } catch (Exception e) {

//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return;
        }
        if (onetablename == null || onetablename.length() == 0) {
            return;
        }
        try {
            con = DBConnector.getconecttion();
            stmt = con.createStatement();
            result = stmt.executeQuery("select * from " + onetablename);
            rsmd = result.getMetaData();
            String listname[] = new String[rsmd.getColumnCount()];
            int colcount = rsmd.getColumnCount();

            /**
             * 得到所有的列名
             */
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                listname[i - 1] = rsmd.getColumnName(i);
            }
            /**
             * 得到表中数据，分页显示
             */
            Vector viewtable = SysTableCtl.getListPage(con, onetablename, curpage, pagesize, colcount);
            pagecount = StringUtil.StringToInt(viewtable.get(0).toString());
            curpagecount = (pagecount - 1) / pagesize + 1;
            paginfoLB.setText("　共" + curpagecount + "页，每页40条记录，共" + pagecount + "条记录，当前第" + curpage + "页　");
            Object noteinfo[][] = (Object[][]) viewtable.get(1);
            TableTable = new JTable(noteinfo, listname) {
                public boolean isCellEditable(int rowIndex, int vColIndex) {
                    return false;
                }
            };
            TablePanel.remove(scrollPane2);
            scrollPane2 = new JScrollPane(TableTable);
            TablePanel.add(BorderLayout.CENTER, scrollPane2);
            validate();
            result.close();
            stmt.close();
        } catch (SQLException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {

            DBConnector.freecon(con);
        }
    }

    private void clear(Object obj[][]) {
        for (int i = 0; i < obj.length; i++) {
            for (int j = 0; j < obj[i].length; j++) {
                obj[i][j] = "";
            }
        }
    }

    private void JLitemdatasourceCBoxActionPerformed(ActionEvent e) {
        // TODO add your code here
        if (schemeselect == JLitemdatasourceCBox.getSelectedIndex() || JLitemdatasourceCBox.getSelectedItem() == null) {
            return;
        } else if (JLitemdatasourceCBox.getSelectedIndex() != 0) {
            schemeselect = JLitemdatasourceCBox.getSelectedIndex();
            con = DBConnector.newgetconecttion(JLitemdatasourceCBox.getSelectedItem().toString()); //  取一个数据库链接

            if (con == null) {
                JOptionPane.showMessageDialog(this, "连接数据库失败，请确认URL是否正确！", "警告对话框", JOptionPane.WARNING_MESSAGE);
                return;
            } else {
                tableName.removeAllElements();   //消除所有的表
                IndexName.removeAllElements();  //消除所有的引索
                viewName.removeAllElements();  //消除所有的视图
                triggerName.removeAllElements();  //消除所有的触发器
                ProceduresName.removeAllElements();  //消除所有的存储过程
                JDtablelistCBox.removeAllItems();
                try {
                    dbmd = con.getMetaData();
                    // Specify the type of object; in this case we want tables
                    String[] types = {"TABLE"};
                    result = dbmd.getTables(null, null, "%", types);
                    // Get the table names
                    while (result.next()) {
                        // Get the table name
                        if (result.getString(2) == null) {
                            tableName.add(result.getString(3));
                            JDtablelistCBox.addItem(result.getString(3));
                            // System.out.println(result.getString(1)+","+result.getString(2));
                        } else if (result.getString(2).trim().equalsIgnoreCase(DBConnector.user)) {
                            tableName.add(result.getString(3));
                            JDtablelistCBox.addItem(result.getString(3));
                        }
                    }
                    JLitemTableList.setListData(tableName);
                    /**
                     * 得到所有的索引  (太多。在此不列)
                     */
/*
                    for (int i = 0; i < tableName.size(); i++)
                    {
                        result = dbmd.getIndexInfo(null, null, tableName.get(i).toString(), true, true);

                        while (result.next())
                        {
                            // Get the view name
                            if (result.getString(2) == null)
                            {
                                IndexName.add(result.getString(3));
                                // System.out.println(result.getString(1)+","+result.getString(2));
                            }
                            else if (result.getString(2).trim().equalsIgnoreCase(DBConnector.user))
                            {
                                IndexName.add(result.getString(3));
                            }
                        }
                    }
                    JLitemTableList2.setListData(IndexName);*/
                    /**
                     * 得到所有的视图
                     */
                    String[] vtypeS = {"VIEW"};
                    result = dbmd.getTables(null, null, "%", vtypeS);

                    while (result.next()) {
                        // Get the view name
                        if (result.getString(2) == null) {
                            viewName.add(result.getString(3));
                            // System.out.println(result.getString(1)+","+result.getString(2));
                        } else if (result.getString(2).trim().equalsIgnoreCase(DBConnector.user)) {
                            viewName.add(result.getString(3));
                        }
                    }
                    JLitemTableList2.setListData(viewName);

                    /**
                     * 得到所有的触发器
                     */

                    //  JLitemTableList4.setListData(triggerName);
                    /**
                     * 得到所有的存储过程
                     */
                    result = dbmd.getProcedures(null, null, null);

                    while (result.next()) {
                        // Get the view name
                        if (result.getString(2) == null) {
                            ProceduresName.add(result.getString(3));
                            // System.out.println(result.getString(1)+","+result.getString(2));
                        } else if (result.getString(2).trim().equalsIgnoreCase(DBConnector.user)) {
                            ProceduresName.add(result.getString(3));
                        }
                    }
                    JLitemTableList3.setListData(ProceduresName);
                    result.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } finally {
                    DBConnector.freecon(con);
                    validate();
                    JlitemTPanel.setSelectedIndex(0);
                }
            }
        }
    }

    private void JTdatasourceButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        mycard.show(JworkPanel, "card7");


    }

    private void JDtableMouseClicked(MouseEvent e) {
        // TODO add your code here
        if (!(JDtable.getSelectedRow() == sysrowselect)) {
            sysrowselect = JDtable.getSelectedRow();
            if (sysrowselect >= popvt.size()) {
                drivertext.setText("");
                urljtext.setText("");
                userjtext.setText("");
                passwordjtext.setText("");
                dbnamejtext.setText("");
                return;
            }
            SysproInfo proinfo = (SysproInfo) popvt.get(sysrowselect);
            drivertext.setText(proinfo.getDriver());
            urljtext.setText(proinfo.getUrl());
            userjtext.setText(proinfo.getUser());
            passwordjtext.setText(proinfo.getPassword());
            dbnamejtext.setText(proinfo.getDbname());
        }
    }

    private void radioButton1ActionPerformed(ActionEvent e) {
        // TODO add your code here
        drivertext.setText("com.microsoft.jdbc.sqlserver.SQLServerDriver");
        urljtext.setText("jdbc:microsoft:sqlserver://主机IP:1433;DatabaseName=数据库名");
        userjtext.setText("");
        passwordjtext.setText("");
        dbnamejtext.setText("");
    }

    private void radioButton2ActionPerformed(ActionEvent e) {
        // TODO add your code here
        drivertext.setText("oracle.jdbc.OracleDriver");
        urljtext.setText("jdbc:oracle:thin:@主机IP:1521:数据库名");
        userjtext.setText("");
        passwordjtext.setText("");
        dbnamejtext.setText("");
    }

    private void radioButton3ActionPerformed(ActionEvent e) {
        // TODO add your code here
        drivertext.setText("sun.jdbc.odbc.JdbcOdbcDriver");
        urljtext.setText("jdbc:odbc:数据源名");
        userjtext.setText("");
        passwordjtext.setText("");
        dbnamejtext.setText("");
    }

    private void radioButton4ActionPerformed(ActionEvent e) {
        // TODO add your code here
        drivertext.setText("com.mysql.jdbc.Driver");
        urljtext.setText("jdbc:mysql://127.0.0.1:3306/db?characterEncoding=gbk");
        userjtext.setText("");
        passwordjtext.setText("");
        dbnamejtext.setText("");
    }

    private void button9ActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void button10ActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void JTexitsysButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        int closedialog = JOptionPane.showConfirmDialog(null, "真的要退出吗?", "是否确定窗口", JOptionPane.YES_NO_OPTION);
        if (closedialog == JOptionPane.YES_OPTION) {
            System.exit(1);
        }
    }

    private void modifyBTActionPerformed(ActionEvent e) {
        // TODO add your code here
        if (drivertext.getText().trim().length() != 0 && urljtext.getText().trim().length() != 0 && userjtext.getText().trim().length() != 0 && passwordjtext.getText().trim().length() != 0 && dbnamejtext.getText().trim().length() != 0 && drivertext.getText().trim().length() != 0) {
            File file = new File("properties/" + dbnamejtext.getText().trim() + ".properties");
            try {
                String newdriver = "#时间：" + DateUtil.date2str(new java.util.Date()) + "\ndriver=" + drivertext.getText().trim() + "\nsqlserverbase.url=" + urljtext.getText().trim() + "\nsqlserverbase.user=" + userjtext.getText().trim() + "\nsqlserverbase.password=" + passwordjtext.getText().trim() + "\nsqlserverbase.maxconn=0";
                byte[] buffer = newdriver.getBytes();
                bufferout = new BufferedOutputStream(new FileOutputStream(file));
                bufferout.write(buffer);
                bufferout.flush();
                bufferout.close();

                /**
                 * 重新列出所有的数据源
                 */

                popvt = SysproCtl.getsysprolist();
                if (popvt.size() <= 40) {
                    clear(noteinfo);
                    for (int i = 0; i < popvt.size(); i++) {
                        SysproInfo propinfo = (SysproInfo) popvt.get(i);
                        noteinfo[i][0] = propinfo.getDbname();
                        noteinfo[i][1] = propinfo.getDriver();
                        noteinfo[i][2] = propinfo.getUrl();
                        noteinfo[i][3] = propinfo.getUser();
                        noteinfo[i][4] = propinfo.getPassword();
                    }
                    JDtable.validate();
                    JDtable.repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "数据源不要超过40个！", "警告对话框", JOptionPane.WARNING_MESSAGE);
                }
                /**
                 * 重新加载表空间或库
                 */


                JLitemdatasourceCBox.removeAllItems();

                JLitemdatasourceCBox.addItem("请选择一个表空间或库　　");
                File dir = new File("properties/");
                String[] children = dir.list();
                if (children != null) {
                    for (int i = 0; i < children.length; i++) {
                        // Get filename of file or directory
                        String filename = children[i];
                        if (filename.endsWith(".properties")) {
                            JLitemdatasourceCBox.addItem(filename.substring(0, filename.length() - 11));
                        }
                    }
                }
                validate();
                schemeselect = 0;
                JOptionPane.showMessageDialog(this, "数据源保存成功！", "数据源保存对话框", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            JOptionPane.showMessageDialog(this, "请保证每个字段不为空！", "警告对话框", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void delBTActionPerformed(ActionEvent e) {
        // TODO add your code here
        try {
            File file = new File("properties/" + dbnamejtext.getText().trim() + ".properties");
            System.out.println("properties/" + dbnamejtext.getText() + ".properties");
            if (dbnamejtext.getText().trim().length() > 0 && file.delete()) {
                /**
                 * 重新列出所有的数据源
                 */

                popvt = SysproCtl.getsysprolist();
                clear(noteinfo);
                if (popvt.size() <= 40) {
                    for (int i = 0; i < popvt.size(); i++) {
                        SysproInfo propinfo = (SysproInfo) popvt.get(i);
                        noteinfo[i][0] = propinfo.getDbname();
                        noteinfo[i][1] = propinfo.getDriver();
                        noteinfo[i][2] = propinfo.getUrl();
                        noteinfo[i][3] = propinfo.getUser();
                        noteinfo[i][4] = propinfo.getPassword();
                    }
                    JDtable.validate();
                    JDtable.repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "数据源不要超过40个！", "警告对话框", JOptionPane.WARNING_MESSAGE);
                }
                /**
                 * 重新加载表空间或库
                 */

                JLitemdatasourceCBox.removeAllItems();

                JLitemdatasourceCBox.addItem("请选择一个表空间或库　　");
                File dir = new File("properties/");
                String[] children = dir.list();
                if (children != null) {
                    for (int i = 0; i < children.length; i++) {
                        // Get filename of file or directory
                        String filename = children[i];
                        if (filename.endsWith(".properties")) {
                            JLitemdatasourceCBox.addItem(filename.substring(0, filename.length() - 11));
                        }
                    }
                }
                validate();
                schemeselect = 0;
                JOptionPane.showMessageDialog(this, "数据源删除成功！", "数据源删除对话框", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "数据源删除失败！", "数据源删除对话框", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void flushBTActionPerformed(ActionEvent e) {
        // TODO add your code here
        /**
         * 重新列出所有的数据源
         */

        popvt = SysproCtl.getsysprolist();
        if (popvt.size() <= 40) {
            clear(noteinfo);
            for (int i = 0; i < popvt.size(); i++) {
                SysproInfo propinfo = (SysproInfo) popvt.get(i);
                noteinfo[i][0] = propinfo.getDbname();
                noteinfo[i][1] = propinfo.getDriver();
                noteinfo[i][2] = propinfo.getUrl();
                noteinfo[i][3] = propinfo.getUser();
                noteinfo[i][4] = propinfo.getPassword();
            }
            JDtable.validate();
            JDtable.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "数据源不要超过40个！", "警告对话框", JOptionPane.WARNING_MESSAGE);
        }
        /**
         * 重新加载表空间或库
         */
        JLitemdatasourceCBox.removeAllItems();
        JLitemdatasourceCBox.addItem("请选择一个表空间或库　　");
        File dir = new File("properties/");
        String[] children = dir.list();
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                // Get filename of file or directory
                String filename = children[i];
                if (filename.endsWith(".properties")) {
                    JLitemdatasourceCBox.addItem(filename.substring(0, filename.length() - 11));
                }
            }
        }
        validate();
        schemeselect = 0;
    }

    private void JTclassButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        if (JLitemdatasourceCBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个数据库！", "警告对话框", JOptionPane.WARNING_MESSAGE);
            return;
        } else {
            classlist.setListData(tableName);
            mycard.show(JworkPanel, "card5");
        }
    }

    private void JTsearchButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        mycard.show(JworkPanel, "card2");
    }

    private void classurlBTActionPerformed(ActionEvent e) {
        // TODO add your code here
        filedialog.setFile("选择正确的目录后点保存");
        filedialog.setVisible(true);
        try {
            sysclassurl.setText(filedialog.getDirectory());
        } catch (Exception e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void selalltableBTActionPerformed(ActionEvent e) {
        // TODO add your code here
        classlist.setSelectionInterval(0, tableName.size() - 1);
        classlist.repaint();
        validate();
    }

    private void buldclassActionPerformed(ActionEvent e) {
        // TODO add your code here
        Object selobj[] = classlist.getSelectedValues();
        if (modelname.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "请输入模块名称！", "警告对话框", JOptionPane.WARNING_MESSAGE);
        } else if (sysclassurl.getText().trim().length() == 0 || selobj.length == 0) {
            JOptionPane.showMessageDialog(this, "请保证保存路径不为空且至少选择一个表！", "警告对话框", JOptionPane.WARNING_MESSAGE);
        } else {
            String dir = sysclassurl.getText();
            StringTokenizer fenxi = new StringTokenizer(syspage.getText(), ".");
            while (fenxi.hasMoreTokens()) {
                dir += fenxi.nextToken() + "\\";
            }
            pandyfile = new File(dir);
            pandyfile.mkdirs();//建立输出目录
            beanfile = new File(dir + "models\\"+modelname.getText()+"\\");
            beanfile.mkdirs();//建立输出目录
            servicefile = new File(dir + "services\\"+modelname.getText()+"\\");
            servicefile.mkdirs();//建立输出目录
            controllerfile = new File(dir + "controllers\\platform\\"+modelname.getText()+"\\");
            controllerfile.mkdirs();//建立输出目录
            viewfile = new File(dir + "views\\");
            viewfile.mkdirs();//建立输出目录
            try {
                con = DBConnector.getconecttion();
                for (int i = 0; i < selobj.length; i++) {
                    String sname = selobj[i].toString().toUpperCase().substring(0, 1) + selobj[i].toString().toLowerCase().substring(1);
                    String xname = selobj[i].toString().toLowerCase();
                    String mname = selobj[i].toString().toUpperCase().substring(0, 1) + selobj[i].toString().toLowerCase().substring(1);
                    if (selobj[i].toString().indexOf("_") > 0) {
                        String[] s = selobj[i].toString().split("_");//shop_goods_product  ShopGoodsProduct
                        String temp = "";
                        for (String s1 : s) {
                            temp = temp + s1.toUpperCase().substring(0, 1) + s1.toLowerCase().substring(1);
                        }
                        sname = temp;
                        xname = temp.toLowerCase().substring(0, 1) + temp.substring(1);
                    }
                    String te[] = SysBuldCtl.getClassCode(con, syspage.getText(), selobj[i].toString(), author.getText(), modelname.getText());
                    /**
                     * Models　
                     */
                    beanfile = new File(dir + "\\models\\"+modelname.getText(), selobj[i].toString().toUpperCase().substring(0, 1) + selobj[i].toString().toLowerCase().substring(1) + ".java");
                    byte[] buffer = te[0].getBytes();
                    bufferout = new BufferedOutputStream(new FileOutputStream(beanfile));
                    bufferout.write(buffer);
                    bufferout.flush();
                    bufferout.close();
                    /**
                     * Service　
                     */
                    servicefile = new File(dir + "\\services\\"+modelname.getText(), sname + "Service.java");
                    buffer = te[1].getBytes();
                    bufferout = new BufferedOutputStream(new FileOutputStream(servicefile));
                    bufferout.write(buffer);
                    bufferout.flush();
                    bufferout.close();
                    /*
                     * Controller
                     */

                    controllerfile = new File(dir + "\\controllers\\platform\\"+modelname.getText(), sname + "Controller.java");
                    buffer = te[2].getBytes();
                    bufferout = new BufferedOutputStream(new FileOutputStream(controllerfile));
                    bufferout.write(buffer);
                    bufferout.flush();
                    bufferout.close();
                    /**
                     * Index.html
                     */
                    viewfile = new File(dir + "\\views", "index.html");
                    buffer = te[3].getBytes();
                    bufferout = new BufferedOutputStream(new FileOutputStream(viewfile));
                    bufferout.write(buffer);
                    bufferout.flush();
                    bufferout.close();
                    /**
                     * Add.html
                     */
                    viewfile = new File(dir + "\\views", "add.html");
                    buffer = te[4].getBytes();
                    bufferout = new BufferedOutputStream(new FileOutputStream(viewfile));
                    bufferout.write(buffer);
                    bufferout.flush();
                    bufferout.close();
                    /**
                     * Edit.html
                     */
                    viewfile = new File(dir + "\\views", "edit.html");
                    buffer = te[5].getBytes();
                    bufferout = new BufferedOutputStream(new FileOutputStream(viewfile));
                    bufferout.write(buffer);
                    bufferout.flush();
                    bufferout.close();

                }
                JOptionPane.showMessageDialog(this, "所有类生成成功！", "生成类对话框", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } finally {
                DBConnector.freecon(con);
            }
        }
    }

    private void codesetButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void JLitemTableListMouseReleased(MouseEvent e) {
        // TODO add your code here
        curpage = 1;
        flushtable();
        mycard.show(JworkPanel, "card9");
    }

    private void JLitemTableList2MouseReleased(MouseEvent e) {
        // TODO add your code here
    }

    private void JLitemTableList3MouseReleased(MouseEvent e) {
        // TODO add your code here
    }

    private void button1ActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void firstBTActionPerformed(ActionEvent e) {
        // TODO add your code here
        if (curpage == 1) {
            return;
        } else {
            curpage = 1;
            flushtable();
        }
    }

    private void purviewBTActionPerformed(ActionEvent e) {
        // TODO add your code here
        if (curpage == 1) {
            return;
        } else if (curpage > 1) {
            curpage -= 1;
            flushtable();
        }
    }

    private void nextBTActionPerformed(ActionEvent e) {
        // TODO add your code here
        if (curpage == curpagecount) {
            return;
        } else if (curpage < curpagecount) {
            curpage += 1;
            flushtable();
        }

    }

    private void lastBTActionPerformed(ActionEvent e) {
        // TODO add your code here
        if (curpage == curpagecount) {
            return;
        } else {
            curpage = curpagecount;
            flushtable();
        }
    }

    private void classlistValueChanged(ListSelectionEvent e) {
        // TODO add your code here
    }


    private void runsqlButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        if (JLitemdatasourceCBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个数据库！", "警告对话框", JOptionPane.WARNING_MESSAGE);
            return;
        } else if (sqlnoteTArea.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "脚本不可以为空！", "警告对话框", JOptionPane.WARNING_MESSAGE);
            return;
        } else {
            con = DBConnector.getconecttion();
            sqlrsTArea.setText(SysRunsqlCtl.Runsql(con, sqlnoteTArea.getText().trim()));
            DBConnector.freecon(con);
        }
    }

    private void JTdictionaryButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        mycard.show(JworkPanel, "card3");
    }

    private void JTsysconfigButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        mycard.show(JworkPanel, "card6");
    }

    private void JTexportButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        mycard.show(JworkPanel, "card4");
    }

    private void JThelpButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        mycard.show(JworkPanel, "card8");
    }

    private void JDselectpathButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        filedialog.setFile("sqlscript.doc");
        filedialog.setVisible(true);
        try {
            JDpathTField.setText(filedialog.getDirectory() + filedialog.getFile());
        } catch (Exception e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void JDrunButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        if (JDsqlnoteArea.getText().trim().length() == 0 || JDpathTField.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "脚本和保存路径不可以为空！", "警告对话框", JOptionPane.WARNING_MESSAGE);
            return;
        } else {


            try {

                /**
                 * 生成数据字典
                 */
                String pan = "";
                if (JDextRButton1.isSelected() == true) {
                    pan = SysdictionaryCtl.getDictionary(JDsqlnoteArea.getText().trim(), JDpathTField.getText());
                } else {
                    pan = SysdictionaryCtl.getModifD(JDsqlnoteArea.getText().trim(), JDpathTField.getText());

                }
                JOptionPane.showMessageDialog(this, pan, "脚本执行对话框", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }


    private void JDsearchButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
        JOptionPane.showMessageDialog(this, "正在建设中", "数据字典查询对话框", JOptionPane.INFORMATION_MESSAGE);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - pandy pandy
        toolBar1 = new JToolBar();
        JTsearchButton = new JButton();
        JTdictionaryButton = new JButton();
        JTexportButton = new JButton();
        JTclassButton = new JButton();
        JTsysconfigButton = new JButton();
        JTdatasourceButton = new JButton();
        JThelpButton = new JButton();
        JTexitsysButton = new JButton();
        JLitemPanel = new JPanel();
        JLitemdatasourceCBox = new JComboBox();
        JLitemdatasourceCBox.addItem("请选择一个表空间或库　　");
        /**
         * 加载表空间或库
         */
        File dir = new File("properties/");
        String[] children = dir.list();
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                // Get filename of file or directory
                String filename = children[i];
                if (filename.endsWith(".properties")) {
                    JLitemdatasourceCBox.addItem(filename.substring(0, filename.length() - 11));
                }
            }
        }
        JlitemTPanel = new JTabbedPane();
        JSP1 = new JScrollPane();
        JLitemTableList = new JList();
        JSP2 = new JScrollPane();
        JLitemTableList2 = new JList();
        JSP3 = new JScrollPane();
        JLitemTableList3 = new JList();
        JworkPanel = new JPanel(mycard);//设置当前页面布局为卡片式
        JwelcomePanel = new JPanel();
        label1 = new JLabel();
        JsearchPanel = new JPanel();
        scrollPane8 = new JScrollPane();
        sqlnoteTArea = new JTextArea();
        scrollPane1 = new JScrollPane();
        sqlrsTArea = new JTextArea();
        panel1 = new JPanel();
        label12 = new JLabel();
        runsqlButton = new JButton();
        JdictionaryPanel = new JPanel();
        panel2 = new JPanel();
        label14 = new JLabel();
        JDpathTField = new JTextField();
        JDselectpathButton = new JButton();
        label15 = new JLabel();
        JDextRButton1 = new JRadioButton();
        JDextRButton2 = new JRadioButton();
        JDrunButton = new JButton();
        scrollPane9 = new JScrollPane();
        JDsqlnoteArea = new JTextArea();
        panel12 = new JPanel();
        label16 = new JLabel();
        JDtablenameTArea = new JTextField();
        JDtablelistCBox = new JComboBox();
        JDsearchButton = new JButton();
        label17 = new JLabel();
        JDtablejsLabel = new JLabel();
        label19 = new JLabel();
        JexportPanel = new JPanel();
        label23 = new JLabel();
        JclassPanel = new JPanel();
        panel11 = new JPanel();
        label8 = new JLabel();
        syspage = new JTextField();
        selalltableBT = new JButton();
        label9 = new JLabel();
        sysclassurl = new JTextField();
        classurlBT = new JButton();
        buldclass = new JButton();
        label11 = new JLabel();
        scrollPane5 = new JScrollPane();
        classlist = new JList();
        JsysconfigPanel = new JPanel();
        panel13 = new JPanel();
        label20 = new JLabel();
        textField3 = new JTextField();
        label18 = new JLabel();
        textField1 = new JTextField();
        modellab = new JLabel();
        modelname = new JTextField();
        label21 = new JLabel();
        textField4 = new JTextField();
        button9 = new JButton();
        button1 = new JButton();
        label24 = new JLabel();
        radioButton6 = new JRadioButton();
        radioButton7 = new JRadioButton();
        radioButton8 = new JRadioButton();
        label22 = new JLabel();
        checkBox4 = new JCheckBox();
        checkBox5 = new JCheckBox();
        checkBox6 = new JCheckBox();
        checkBox7 = new JCheckBox();
        checkBox8 = new JCheckBox();
        checkBox9 = new JCheckBox();
        label25 = new JLabel();
        JdatasourcePanel = new JPanel();
        scrollPane6 = new JScrollPane();
        popvt = SysproCtl.getsysprolist();

        for (int i = 0; i < popvt.size(); i++) {
            SysproInfo propinfo = (SysproInfo) popvt.get(i);
            noteinfo[i][0] = propinfo.getDbname();
            noteinfo[i][1] = propinfo.getDriver();
            noteinfo[i][2] = propinfo.getUrl();
            noteinfo[i][3] = propinfo.getUser();
            noteinfo[i][4] = propinfo.getPassword();
        }
        JDtable = new JTable(noteinfo, systablecolname) {
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };

        JDtable.setSelectionMode(0);
        panel6 = new JPanel();
        label3 = new JLabel();
        panel7 = new JPanel();
        drivertext = new JTextField();
        radioButton1 = new JRadioButton();
        radioButton2 = new JRadioButton();
        radioButton3 = new JRadioButton();
        radioButton4 = new JRadioButton();
        label4 = new JLabel();
        panel8 = new JPanel();
        urljtext = new JTextField();
        label5 = new JLabel();
        panel9 = new JPanel();
        userjtext = new JTextField();
        modifyBT = new JButton();
        label6 = new JLabel();
        panel10 = new JPanel();
        passwordjtext = new JTextField();
        delBT = new JButton();
        label7 = new JLabel();
        label99 = new JLabel();
        dbnamejtext = new JTextField();
        author = new JTextField();
        flushBT = new JButton();
        JhelpPanel = new JPanel();
        label26 = new JLabel();
        TablePanel = new JPanel();
        panel3 = new JPanel();
        firstBT = new JButton();
        purviewBT = new JButton();
        paginfoLB = new JLabel();
        nextBT = new JButton();
        lastBT = new JButton();
        scrollPane2 = new JScrollPane();
        TableTable = new JTable();
        ViewPanel = new JPanel();
        scrollPane3 = new JScrollPane();
        ViewTable = new JTable();
        panel4 = new JPanel();
        button5 = new JButton();
        button7 = new JButton();
        label13 = new JLabel();
        button8 = new JButton();
        button6 = new JButton();
        StorPanel = new JPanel();
        scrollPane4 = new JScrollPane();
        textArea3 = new JTextArea();
        panel5 = new JPanel();
        label2 = new JLabel();

        //======== this ========
        setTitle("Nutz Codematic v3.0 \uff0d \u6570\u636e\u5e93");
        setIconImage(new ImageIcon(getClass().getResource("/images/title.gif")).getImage());
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== toolBar1 ========
        {
            toolBar1.setAutoscrolls(true);
            toolBar1.setAlignmentY(0.5F);
            toolBar1.setBorder(null);
            toolBar1.setForeground(new Color(204, 204, 255));
            toolBar1.setMargin(new Insets(0, 5, 0, 5));
            toolBar1.setRollover(true);
            toolBar1.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            toolBar1.setBorderPainted(false);

            //---- JTsearchButton ----
//        	JTsearchButton.setIcon(new ImageIcon(getClass().getResource("/images/\u67e5\u8be2\u5668.gif")));
//        	JTsearchButton.setToolTipText("\u67e5\u8be2\u5668");
//        	JTsearchButton.setText(" \u67e5\u8be2\u5668 ");
//        	JTsearchButton.setHorizontalTextPosition(SwingConstants.CENTER);
//        	JTsearchButton.setVerticalTextPosition(SwingConstants.BOTTOM);
//        	JTsearchButton.setSelected(true);
//        	JTsearchButton.setDefaultCapable(false);
//        	JTsearchButton.setBorder(new BevelBorder(BevelBorder.RAISED));
//        	JTsearchButton.setMinimumSize(new Dimension(80, 65));
//        	JTsearchButton.setMaximumSize(new Dimension(80, 65));
//        	JTsearchButton.setPreferredSize(new Dimension(80, 65));
//        	JTsearchButton.setVerticalAlignment(SwingConstants.BOTTOM);
//        	JTsearchButton.addActionListener(new ActionListener() {
//        		public void actionPerformed(ActionEvent e) {
//        			JTsearchButtonActionPerformed(e);
//        		}
//        	});
//        	toolBar1.add(JTsearchButton);

//        	//---- JTdictionaryButton ----
//        	JTdictionaryButton.setIcon(new ImageIcon(getClass().getResource("/images/\u751f\u6210\u4ee3\u7801.gif")));
//        	JTdictionaryButton.setText("\u6570\u636e\u5b57\u5178");
//        	JTdictionaryButton.setVerticalTextPosition(SwingConstants.BOTTOM);
//        	JTdictionaryButton.setHorizontalTextPosition(SwingConstants.CENTER);
//        	JTdictionaryButton.setToolTipText("\u6570\u636e\u5b57\u5178");
//        	JTdictionaryButton.setBorder(new BevelBorder(BevelBorder.RAISED));
//        	JTdictionaryButton.setMaximumSize(new Dimension(80, 65));
//        	JTdictionaryButton.setMinimumSize(new Dimension(80, 65));
//        	JTdictionaryButton.setPreferredSize(new Dimension(80, 65));
//        	JTdictionaryButton.setVerticalAlignment(SwingConstants.BOTTOM);
//        	JTdictionaryButton.addActionListener(new ActionListener() {
//        		public void actionPerformed(ActionEvent e) {
//        			JTdictionaryButtonActionPerformed(e);
//        		}
//        	});
//        	toolBar1.add(JTdictionaryButton);

//        	//---- JTexportButton ----
//        	JTexportButton.setIcon(new ImageIcon(getClass().getResource("/images/\u7ba1\u7406.gif")));
//        	JTexportButton.setText("\u5bfc\u5165\u5bfc\u51fa");
//        	JTexportButton.setVerticalTextPosition(SwingConstants.BOTTOM);
//        	JTexportButton.setHorizontalTextPosition(SwingConstants.CENTER);
//        	JTexportButton.setToolTipText("\u5bfc\u5165\u5bfc\u51fa");
//        	JTexportButton.setBorder(new BevelBorder(BevelBorder.RAISED));
//        	JTexportButton.setMaximumSize(new Dimension(80, 65));
//        	JTexportButton.setMinimumSize(new Dimension(80, 65));
//        	JTexportButton.setPreferredSize(new Dimension(80, 65));
//        	JTexportButton.setVerticalAlignment(SwingConstants.BOTTOM);
//        	JTexportButton.addActionListener(new ActionListener() {
//        		public void actionPerformed(ActionEvent e) {
//        			JTexportButtonActionPerformed(e);
//        		}
//        	});
//        	toolBar1.add(JTexportButton);

            //---- JTclassButton ----
            JTclassButton.setIcon(new ImageIcon(getClass().getResource("/images/\u7528\u6237\u4fe1\u606f.gif")));
            JTclassButton.setText(" \u751f\u6210\u7c7b ");
            JTclassButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            JTclassButton.setHorizontalTextPosition(SwingConstants.CENTER);
            JTclassButton.setToolTipText("\u751f\u6210\u7c7b");
            JTclassButton.setBorder(new BevelBorder(BevelBorder.RAISED));
            JTclassButton.setMaximumSize(new Dimension(80, 65));
            JTclassButton.setMinimumSize(new Dimension(80, 65));
            JTclassButton.setPreferredSize(new Dimension(80, 65));
            JTclassButton.setVerticalAlignment(SwingConstants.BOTTOM);
            JTclassButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JTclassButtonActionPerformed(e);
                }
            });
            toolBar1.add(JTclassButton);

//        	//---- JTsysconfigButton ----
//        	JTsysconfigButton.setIcon(new ImageIcon(getClass().getResource("/images/\u7528\u6237.gif")));
//        	JTsysconfigButton.setText("\u7ad9\u70b9\u90e8\u7f72");
//        	JTsysconfigButton.setVerticalTextPosition(SwingConstants.BOTTOM);
//        	JTsysconfigButton.setHorizontalTextPosition(SwingConstants.CENTER);
//        	JTsysconfigButton.setToolTipText("\u7ad9\u70b9\u90e8\u7f72");
//        	JTsysconfigButton.setBorder(new BevelBorder(BevelBorder.RAISED));
//        	JTsysconfigButton.setMaximumSize(new Dimension(80, 65));
//        	JTsysconfigButton.setMinimumSize(new Dimension(80, 65));
//        	JTsysconfigButton.setPreferredSize(new Dimension(80, 65));
//        	JTsysconfigButton.setVerticalAlignment(SwingConstants.BOTTOM);
//        	JTsysconfigButton.addActionListener(new ActionListener() {
//        		public void actionPerformed(ActionEvent e) {
//        			JTsysconfigButtonActionPerformed(e);
//        		}
//        	});
//        	toolBar1.add(JTsysconfigButton);

            //---- JTdatasourceButton ----
            JTdatasourceButton.setIcon(new ImageIcon(getClass().getResource("/images/\u5907\u4efd.gif")));
            JTdatasourceButton.setText(" \u6570\u636e\u6e90 ");
            JTdatasourceButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            JTdatasourceButton.setHorizontalTextPosition(SwingConstants.CENTER);
            JTdatasourceButton.setToolTipText("\u6570\u636e\u6e90");
            JTdatasourceButton.setBorder(new BevelBorder(BevelBorder.RAISED));
            JTdatasourceButton.setPreferredSize(new Dimension(80, 65));
            JTdatasourceButton.setMaximumSize(new Dimension(80, 65));
            JTdatasourceButton.setMinimumSize(new Dimension(80, 65));
            JTdatasourceButton.setVerticalAlignment(SwingConstants.BOTTOM);
            JTdatasourceButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JTdatasourceButtonActionPerformed(e);
                }
            });
            toolBar1.add(JTdatasourceButton);
            toolBar1.addSeparator(new Dimension(5, 5));

            //---- JThelpButton ----
//        	JThelpButton.setIcon(new ImageIcon(getClass().getResource("/images/\u5e2e\u52a9.gif")));
//        	JThelpButton.setToolTipText("\u5e2e\u52a9");
//        	JThelpButton.setText("  \u5e2e\u52a9  ");
//        	JThelpButton.setVerticalTextPosition(SwingConstants.BOTTOM);
//        	JThelpButton.setHorizontalTextPosition(SwingConstants.CENTER);
//        	JThelpButton.setBorder(new BevelBorder(BevelBorder.RAISED));
//        	JThelpButton.setMinimumSize(new Dimension(80, 65));
//        	JThelpButton.setMaximumSize(new Dimension(80, 65));
//        	JThelpButton.setPreferredSize(new Dimension(80, 65));
//        	JThelpButton.setVerticalAlignment(SwingConstants.BOTTOM);
//        	JThelpButton.addActionListener(new ActionListener() {
//        		public void actionPerformed(ActionEvent e) {
//        			JThelpButtonActionPerformed(e);
//        		}
//        	});
//        	toolBar1.add(JThelpButton);

//        	//---- JTexitsysButton ----
//        	JTexitsysButton.setIcon(new ImageIcon(getClass().getResource("/images/\u9000\u51fa\u7cfb\u7edf.gif")));
//        	JTexitsysButton.setToolTipText("\u9000\u51fa\u7cfb\u7edf");
//        	JTexitsysButton.setText("\u9000\u51fa\u7cfb\u7edf");
//        	JTexitsysButton.setVerticalTextPosition(SwingConstants.BOTTOM);
//        	JTexitsysButton.setHorizontalTextPosition(SwingConstants.CENTER);
//        	JTexitsysButton.setBorder(new BevelBorder(BevelBorder.RAISED));
//        	JTexitsysButton.setPreferredSize(new Dimension(80, 65));
//        	JTexitsysButton.setMaximumSize(new Dimension(80, 65));
//        	JTexitsysButton.setMinimumSize(new Dimension(80, 65));
//        	JTexitsysButton.setVerticalAlignment(SwingConstants.BOTTOM);
//        	JTexitsysButton.setFocusPainted(false);
//        	JTexitsysButton.setInheritsPopupMenu(true);
//        	JTexitsysButton.addActionListener(new ActionListener() {
//        		public void actionPerformed(ActionEvent e) {
//        			JTexitsysButtonActionPerformed(e);
//        		}
//        	});
//        	toolBar1.add(JTexitsysButton);
        }
        contentPane.add(toolBar1, BorderLayout.NORTH);

        //======== JLitemPanel ========
        {

            // JFormDesigner evaluation mark
            JLitemPanel.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                            "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                            javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                            java.awt.Color.red), JLitemPanel.getBorder()));
            JLitemPanel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    if ("border".equals(e.getPropertyName())) throw new RuntimeException();
                }
            });

            JLitemPanel.setLayout(new BorderLayout());

            //---- JLitemdatasourceCBox ----
            JLitemdatasourceCBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JLitemdatasourceCBoxActionPerformed(e);
                }
            });
            JLitemPanel.add(JLitemdatasourceCBox, BorderLayout.NORTH);

            //======== JlitemTPanel ========
            {

                //======== JSP1 ========
                {

                    //---- JLitemTableList ----
                    JLitemTableList.addMouseListener(new MouseAdapter() {
                        //
                        public void mouseReleased(MouseEvent e) {
                            JLitemTableListMouseReleased(e);
                        }
                    });
                    JSP1.setViewportView(JLitemTableList);
                }
                JlitemTPanel.addTab("\u8868", JSP1);


                //======== JSP2 ========
                {

                    //---- JLitemTableList2 ----
                    JLitemTableList2.addMouseListener(new MouseAdapter() {

                        public void mouseReleased(MouseEvent e) {
                            JLitemTableList2MouseReleased(e);
                        }
                    });
                    JSP2.setViewportView(JLitemTableList2);
                }
                JlitemTPanel.addTab("\u89c6\u56fe", JSP2);


                //======== JSP3 ========
                {

                    //---- JLitemTableList3 ----
                    JLitemTableList3.addMouseListener(new MouseAdapter() {

                        public void mouseReleased(MouseEvent e) {
                            JLitemTableList3MouseReleased(e);
                        }
                    });
                    JSP3.setViewportView(JLitemTableList3);
                }
                JlitemTPanel.addTab("\u5b58\u50a8\u8fc7\u7a0b", JSP3);

            }
            JLitemPanel.add(JlitemTPanel, BorderLayout.CENTER);
        }
        contentPane.add(JLitemPanel, BorderLayout.WEST);

        //======== JworkPanel ========
        {
            //JworkPanel.setLayout(new CardLayout());

            //======== JwelcomePanel ========
            {
                JwelcomePanel.setLayout(new BorderLayout());

                //---- label1 ----
                label1.setIcon(new ImageIcon(getClass().getResource("/images/\u80cc\u666f.jpg")));
                label1.setHorizontalTextPosition(SwingConstants.CENTER);
                label1.setHorizontalAlignment(SwingConstants.CENTER);
                JwelcomePanel.add(label1, BorderLayout.CENTER);
            }
            JworkPanel.add(JwelcomePanel, "card1");

            //======== JsearchPanel ========
            {
                JsearchPanel.setLayout(new BorderLayout());

                //======== scrollPane8 ========
                {

                    //---- sqlnoteTArea ----
                    sqlnoteTArea.setMinimumSize(new Dimension(4, 18));
                    sqlnoteTArea.setRows(15);
                    scrollPane8.setViewportView(sqlnoteTArea);
                }
                JsearchPanel.add(scrollPane8, BorderLayout.CENTER);

                //======== scrollPane1 ========
                {

                    //---- sqlrsTArea ----
                    sqlrsTArea.setRows(12);
                    scrollPane1.setViewportView(sqlrsTArea);
                }
                JsearchPanel.add(scrollPane1, BorderLayout.SOUTH);

                //======== panel1 ========
                {
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel1.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0};
                    ((GridBagLayout) panel1.getLayout()).rowHeights = new int[]{0, 0, 0, 0};
                    ((GridBagLayout) panel1.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel1.getLayout()).rowWeights = new double[]{0.0, 1.0, 0.0, 1.0E-4};

                    //---- label12 ----
                    label12.setText("<html>\u8bf4\u660e:<br>\u3000\u3000\u5728\u4e0a\u9762\u7684\u6587\u672c\u4e2d\u8f93\u5165<br>SQL\u8bed\u53e5\u3002\u70b9\u51fb\u6267\u884c\u811a\u672c\u3002<br></html>");
                    label12.setVerticalAlignment(SwingConstants.TOP);
                    panel1.add(label12, new GridBagConstraints(0, 0, 4, 2, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                    //---- runsqlButton ----
                    runsqlButton.setText("\u6267\u884c\u811a\u672c");
                    runsqlButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            runsqlButtonActionPerformed(e);
                        }
                    });
                    panel1.add(runsqlButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                }
                JsearchPanel.add(panel1, BorderLayout.EAST);
            }
            JworkPanel.add(JsearchPanel, "card2");

            //======== JdictionaryPanel ========
            {
                JdictionaryPanel.setLayout(new BorderLayout());

                //======== panel2 ========
                {
                    panel2.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel2.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel2.getLayout()).rowHeights = new int[]{0, 0, 0};
                    ((GridBagLayout) panel2.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel2.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};

                    //---- label14 ----
                    label14.setText("\u6570\u636e\u5b57\u5178\u79ef\u5b58\u8def\u5f84\uff1a");
                    panel2.add(label14, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- JDpathTField ----
                    JDpathTField.setEnabled(false);
                    panel2.add(JDpathTField, new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- JDselectpathButton ----
                    JDselectpathButton.setText("\u9009\u62e9");
                    JDselectpathButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JDselectpathButtonActionPerformed(e);
                        }
                    });
                    panel2.add(JDselectpathButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- label15 ----
                    label15.setText("\u9009\u9879\uff1a");
                    panel2.add(label15, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- JDextRButton1 ----
                    JDextRButton1.setText("\u65b0\u5efa");
                    JDextRButton1.setSelected(true);
                    panel2.add(JDextRButton1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- JDextRButton2 ----
                    JDextRButton2.setText("\u8ffd\u52a0");
                    panel2.add(JDextRButton2, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- JDrunButton ----
                    JDrunButton.setText("\u751f\u6210\u5b57\u5178");
                    JDrunButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JDrunButtonActionPerformed(e);
                        }
                    });
                    panel2.add(JDrunButton, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));
                }
                JdictionaryPanel.add(panel2, BorderLayout.SOUTH);

                //======== scrollPane9 ========
                {

                    //---- JDsqlnoteArea ----
                    JDsqlnoteArea.setBorder(new TitledBorder("\u6570\u636e\u5e93\u811a\u672c"));
                    scrollPane9.setViewportView(JDsqlnoteArea);
                }
                JdictionaryPanel.add(scrollPane9, BorderLayout.CENTER);

                //======== panel12 ========
                {
                    panel12.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel12.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0};
                    ((GridBagLayout) panel12.getLayout()).rowHeights = new int[]{0, 0, 0};
                    ((GridBagLayout) panel12.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel12.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};

                    //---- label16 ----
                    label16.setText("\u8868\u5217\u540d\uff1a");
                    panel12.add(label16, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));
                    panel12.add(JDtablenameTArea, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));
                    panel12.add(JDtablelistCBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- JDsearchButton ----
                    JDsearchButton.setText("\u67e5\u3000\u8be2");
                    JDsearchButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JDsearchButtonActionPerformed(e);
                        }
                    });
                    panel12.add(JDsearchButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                    //---- label17 ----
                    label17.setText("\u5b57\u6bb5\u89e3\u91ca\uff1a");
                    panel12.add(label17, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- JDtablejsLabel ----
                    JDtablejsLabel.setVerticalAlignment(SwingConstants.TOP);
                    panel12.add(JDtablejsLabel, new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                }
                JdictionaryPanel.add(panel12, BorderLayout.NORTH);

                //---- label19 ----
                label19.setText("<html>\u8bf4\u660e\uff1a<br>1\u3001\u6570\u636e\u5b57\u5178\u67e5\u8be2\u529f\u80fd\uff1a\u3000\u53ea\u8981\u8f93<br>\u3000\u5165\u9700\u8981\u67e5\u627e\u7684\u5217\u540d\u540e\uff0c\u70b9\u51fb\u67e5\u8be2<br>\u3000\u5373\u53ef.<br>2\u3001\u6570\u636e\u5b57\u5178\u751f\u6210\uff1a\u3000\u8f93\u5165\u6570\u636e\u5e93<br>\u3000\u811a\u672c\u4ee3\u7801\u3002<br>3\u3001\u811a\u672c\u89c4\u8303\uff1a<br>/**####\u798f\u5f69\u4fe1\u606f\u7ba1\u7406\u7cfb\u7edf####**/<br>/**##\u7cfb\u7edf\u7ba1\u7406##**/<br>/**#\u9500\u552e\u4efb\u52a1\u6570\u636e#**/<br>\nCREATE TABLE welfare_zftask (<br>\n\u3000dotime\tVarchar2(200) , /**\u65f6\u95f4\u5e74**/<br>\n\u3000xzarea Varchar(20) , /**\u884c\u653f\u533a\u57df**/<br>\n\u3000saletask number,  /**\u9500\u552e\u4efb\u52a1**/<br>\n\u3000Ext1\tVarchar(255),   /**\u6269\u5c551**/<br>\n\u3000Ext2\tVarchar(255)  , /**\u6269\u5c552**/<br>\n\u3000Ext3\tnumber  , /**\u6269\u5c553**/<br>\n\u3000CONSTRAINT PK_welfare_zftask PRIMARY KEY (dotime,xzarea)<br>\n) ;");
                label19.setVerticalAlignment(SwingConstants.TOP);
                JdictionaryPanel.add(label19, BorderLayout.EAST);
            }
            JworkPanel.add(JdictionaryPanel, "card3");

            //======== JexportPanel ========
            {
                JexportPanel.setLayout(new BorderLayout());

                //---- label23 ----
                label23.setText("\u5efa\u8bbe\u4e2d....");
                JexportPanel.add(label23, BorderLayout.NORTH);
            }
            JworkPanel.add(JexportPanel, "card4");

            //======== JclassPanel ========
            {
                JclassPanel.setMaximumSize(new Dimension(10240, 1024));
                JclassPanel.setLayout(new BorderLayout());

                //======== panel11 ========
                {
                    panel11.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel11.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel11.getLayout()).rowHeights = new int[]{0, 0, 0};
                    ((GridBagLayout) panel11.getLayout()).columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel11.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};

                    //----  ----
                    modellab.setText("\u6a21\u5757\u540d\u79f0\uff1a");
                    modellab.setHorizontalAlignment(SwingConstants.RIGHT);
                    panel11.add(modellab, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    modelname.setText("xxx");
//                    modelname.addKeyListener(new KeyAdapter() {
//                        public void keyReleased(KeyEvent e) {
//                            syspage.setText("cn.wizzer.modules." + modelname.getText());
//
//                        }
//                    });
//
                    panel11.add(modelname, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- label99 ----
                    label99.setText("\u7c7b\u4f5c\u8005\uff1a");
                    label99.setHorizontalAlignment(SwingConstants.RIGHT);
                    panel11.add(label99, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    author.setText("Wizzer.cn");
                    panel11.add(author, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- label8 ----
                    label8.setText("\u751f\u6210\u5305\u540d\uff1a");
                    label8.setHorizontalAlignment(SwingConstants.RIGHT);
                    panel11.add(label8, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    syspage.setText("cn.wizzer.modules");
                    syspage.setEditable(true);
                    panel11.add(syspage, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    //---- selalltableBT ----
                    selalltableBT.setText("\u9009\u62e9\u6240\u6709\u8868");
                    selalltableBT.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            selalltableBTActionPerformed(e);
                        }
                    });
                    panel11.add(selalltableBT, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- label9 ----
                    label9.setText("\u7c7b\u4fdd\u5b58\u8def\u5f84\uff1a");
                    label9.setHorizontalAlignment(SwingConstants.RIGHT);
                    panel11.add(label9, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    //---- sysclassurl ----
                    sysclassurl.setEditable(false);
                    panel11.add(sysclassurl, new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- classurlBT ----
                    classurlBT.setText("\u9009\u62e9");
                    classurlBT.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            classurlBTActionPerformed(e);
                        }
                    });
                    panel11.add(classurlBT, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- buldclass ----
                    buldclass.setText("\u751f\u6210\u7c7b");
                    buldclass.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            buldclassActionPerformed(e);
                        }
                    });
                    panel11.add(buldclass, new GridBagConstraints(5, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                }
                JclassPanel.add(panel11, BorderLayout.NORTH);

                //---- label11 ----
                label11.setText("<html>\n<body>\n<p class=\"style1\">\u7c7b\u751f\u6210\u8bf4\u660e\uff1a</p>\n<p><span class=\"style1\">\u3000\u3000</span>1\u3001\u5305\u540d\u4e3a\u7c7b\u6240\u5728\u7684\u5305\uff0c\u5982com.hits\u3000\u5728\u7c7b\u4e2d\u4f1a\u751f\u6210package com.hits;</p>\n<p>\u3000\u30002\u3001\u7c7b\u7684\u4fdd\u5b58\u8def\u5f84\uff1a\u5373JAVA\u6e90\u6587\u4ef6\u6240\u5b58\u653e\u7684\u5730\u65b9\uff0c\u53ea\u9009\u62e9D:\\java\\\u3000\u7cfb\u7edf\u4f1a\u5c06\u76f8\u5e94\u7684JAVA\u4ee3\u7801\u4fdd\u5b58\u5230\u6b64\u6587\u4ef6\u5939\u4e0b\u3002\u6ce8\u610f\u4e0d\u8981\u5728\u6307\u5411\u76f8\u5e94\u7684\u5305\u4e0b\u3002\u5982\u4e0a\u9762\u7684\u5305\u3002\u4e0d\u7528\u6307\u5230D:\\java\\com\\hits\\\u3002\u7cfb\u7edf\u4f1a\u6839\u636e\u5f53\u524d\u7684\u5305\u540d\u81ea\u52a8\u751f\u6210\u3002</p>\n<p>&nbsp;</p>\n<p>\u3000\u3000 </p>\n</body>\n</html>");
                label11.setIcon(new ImageIcon(getClass().getResource("/images/newbg.jpg")));
                label11.setHorizontalTextPosition(SwingConstants.CENTER);
                label11.setHorizontalAlignment(SwingConstants.CENTER);
                label11.setVerticalAlignment(SwingConstants.TOP);
                label11.setVerticalTextPosition(SwingConstants.TOP);
                JclassPanel.add(label11, BorderLayout.CENTER);

                //======== scrollPane5 ========
                {
                    scrollPane5.setViewportView(classlist);
                }
                JclassPanel.add(scrollPane5, BorderLayout.WEST);
            }
            JworkPanel.add(JclassPanel, "card5");

            //======== JsysconfigPanel ========
            {
                JsysconfigPanel.setLayout(new BorderLayout());

                //======== panel13 ========
                {
                    panel13.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel13.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel13.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel13.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel13.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                    //---- label20 ----
                    label20.setText("\u7ad9\u70b9\u4e2d\u6587\u540d\u79f0\uff1a");
                    panel13.add(label20, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));
                    panel13.add(textField3, new GridBagConstraints(1, 0, 7, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- label18 ----
                    label18.setText("\u7ad9\u70b9\u540d\uff1a");
                    panel13.add(label18, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));
                    panel13.add(textField1, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- label21 ----
                    label21.setText("\u8f93\u51fa\u8def\u5f84\uff1a");
                    panel13.add(label21, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));
                    panel13.add(textField4, new GridBagConstraints(1, 2, 7, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- button9 ----
                    button9.setText("\u9009\u62e9");
                    panel13.add(button9, new GridBagConstraints(8, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- button1 ----
                    button1.setText("\u90e8\u7f72");
                    panel13.add(button1, new GridBagConstraints(9, 1, 2, 2, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- label24 ----
                    label24.setText("\u6570\u636e\u5e93\u9009\u9879\uff1a");
                    panel13.add(label24, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- radioButton6 ----
                    radioButton6.setText("oracle");
                    radioButton6.setSelected(true);
                    panel13.add(radioButton6, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- radioButton7 ----
                    radioButton7.setText("sql server");
                    panel13.add(radioButton7, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- radioButton8 ----
                    radioButton8.setText("mysql");
                    panel13.add(radioButton8, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                    //---- label22 ----
                    label22.setText("\u6a21\u5757\u9009\u9879\uff1a");
                    panel13.add(label22, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- checkBox4 ----
                    checkBox4.setText("\u7cfb\u7edf\u7ba1\u7406");
                    checkBox4.setSelected(true);
                    checkBox4.setEnabled(false);
                    panel13.add(checkBox4, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- checkBox5 ----
                    checkBox5.setText("\u4fe1\u606f\u53d1\u5e03");
                    panel13.add(checkBox5, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- checkBox6 ----
                    checkBox6.setText("\u65e5\u7a0b\u7ba1\u7406");
                    panel13.add(checkBox6, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- checkBox7 ----
                    checkBox7.setText("\u90ae\u5c40");
                    panel13.add(checkBox7, new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- checkBox8 ----
                    checkBox8.setText("\u8bba\u575b");
                    panel13.add(checkBox8, new GridBagConstraints(5, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- checkBox9 ----
                    checkBox9.setText("\u535a\u5ba2");
                    panel13.add(checkBox9, new GridBagConstraints(6, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));
                }
                JsysconfigPanel.add(panel13, BorderLayout.NORTH);
                JsysconfigPanel.add(label25, BorderLayout.SOUTH);
            }
            JworkPanel.add(JsysconfigPanel, "card6");

            //======== JdatasourcePanel ========
            {
                JdatasourcePanel.setLayout(new BorderLayout());

                //======== scrollPane6 ========
                {

                    //---- JDtable ----
                    JDtable.addMouseListener(new MouseAdapter() {

                        public void mouseClicked(MouseEvent e) {
                            JDtableMouseClicked(e);
                        }
                    });
                    scrollPane6.setViewportView(JDtable);
                }
                JdatasourcePanel.add(scrollPane6, BorderLayout.CENTER);

                //======== panel6 ========
                {
                    panel6.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel6.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel6.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel6.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel6.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};

                    //---- label3 ----
                    label3.setText("\u9a71\u52a8\u7a0b\u5e8f\uff1a");
                    label3.setHorizontalAlignment(SwingConstants.RIGHT);
                    panel6.add(label3, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //======== panel7 ========
                    {
                        panel7.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel7.getLayout()).columnWidths = new int[]{0, 0, 0};
                        ((GridBagLayout) panel7.getLayout()).rowHeights = new int[]{0, 0};
                        ((GridBagLayout) panel7.getLayout()).columnWeights = new double[]{1.0, 1.0, 1.0E-4};
                        ((GridBagLayout) panel7.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};
                        panel7.add(drivertext, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel6.add(panel7, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- radioButton1 ----
                    radioButton1.setText("sqlserver");
                    radioButton1.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            radioButton1ActionPerformed(e);
                        }
                    });
                    panel6.add(radioButton1, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- radioButton2 ----
                    radioButton2.setText("orcale");
                    radioButton2.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            radioButton2ActionPerformed(e);
                        }
                    });
                    panel6.add(radioButton2, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- radioButton3 ----
                    radioButton3.setText("odbc");
                    radioButton3.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            radioButton3ActionPerformed(e);
                        }
                    });
                    panel6.add(radioButton3, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    //---- radioButton3 ----
                    radioButton4.setText("MySQL\u3000\u3000\u3000");
                    radioButton4.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            radioButton4ActionPerformed(e);
                        }
                    });
                    panel6.add(radioButton4, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- label4 ----
                    label4.setText("\u5e93URL\uff1a");
                    label4.setHorizontalAlignment(SwingConstants.RIGHT);
                    panel6.add(label4, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //======== panel8 ========
                    {
                        panel8.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel8.getLayout()).columnWidths = new int[]{0, 0};
                        ((GridBagLayout) panel8.getLayout()).rowHeights = new int[]{0, 0};
                        ((GridBagLayout) panel8.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
                        ((GridBagLayout) panel8.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};
                        panel8.add(urljtext, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel6.add(panel8, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- label5 ----
                    label5.setText("\u767b\u9646\u540d\uff1a");
                    label5.setHorizontalAlignment(SwingConstants.RIGHT);
                    panel6.add(label5, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //======== panel9 ========
                    {
                        panel9.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel9.getLayout()).columnWidths = new int[]{0, 0};
                        ((GridBagLayout) panel9.getLayout()).rowHeights = new int[]{0, 0};
                        ((GridBagLayout) panel9.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
                        ((GridBagLayout) panel9.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};
                        panel9.add(userjtext, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel6.add(panel9, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- modifyBT ----
                    modifyBT.setText("\u4fdd\u3000\u5b58");
                    modifyBT.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            modifyBTActionPerformed(e);
                        }
                    });
                    panel6.add(modifyBT, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- label6 ----
                    label6.setText("\u5bc6\u7801\uff1a");
                    label6.setHorizontalAlignment(SwingConstants.RIGHT);
                    panel6.add(label6, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //======== panel10 ========
                    {
                        panel10.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel10.getLayout()).columnWidths = new int[]{0, 0};
                        ((GridBagLayout) panel10.getLayout()).rowHeights = new int[]{0, 0};
                        ((GridBagLayout) panel10.getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
                        ((GridBagLayout) panel10.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};
                        panel10.add(passwordjtext, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel6.add(panel10, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- delBT ----
                    delBT.setText("\u5220\u3000\u9664");
                    delBT.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            delBTActionPerformed(e);
                        }
                    });
                    panel6.add(delBT, new GridBagConstraints(4, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- label7 ----
                    label7.setText("\u4fdd\u5b58\u6587\u4ef6\u540d\uff1a");
                    panel6.add(label7, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    panel6.add(dbnamejtext, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));

                    //---- flushBT ----
                    flushBT.setText("\u5237\u3000\u65b0");
                    flushBT.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            button10ActionPerformed(e);
                            flushBTActionPerformed(e);
                        }
                    });
                    panel6.add(flushBT, new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                }
                JdatasourcePanel.add(panel6, BorderLayout.NORTH);
            }
            JworkPanel.add(JdatasourcePanel, "card7");

            //======== JhelpPanel ========
            {
                JhelpPanel.setLayout(new BorderLayout());

                //---- label26 ----
                label26.setText("\u5efa\u8bbe\u4e2d\u3002\u3002\u3002");
                JhelpPanel.add(label26, BorderLayout.NORTH);
            }
            JworkPanel.add(JhelpPanel, "card8");

            //======== TablePanel ========
            {
                TablePanel.setLayout(new BorderLayout());

                //======== panel3 ========
                {
                    panel3.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel3.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel3.getLayout()).rowHeights = new int[]{0, 0};
                    ((GridBagLayout) panel3.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel3.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

                    //---- firstBT ----
                    firstBT.setText("\u9996\u9875");
                    firstBT.setIcon(new ImageIcon(getClass().getResource("/images/first.gif")));
                    firstBT.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            firstBTActionPerformed(e);
                        }
                    });
                    panel3.add(firstBT, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- purviewBT ----
                    purviewBT.setText("\u4e0a\u4e00\u9875");
                    purviewBT.setIcon(new ImageIcon(getClass().getResource("/images/arrow_back.gif")));
                    purviewBT.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            purviewBTActionPerformed(e);
                        }
                    });
                    panel3.add(purviewBT, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- paginfoLB ----
                    paginfoLB.setText("\u51710\u9875\uff0c\u6bcf\u987540\u6761\u8bb0\u5f55\uff0c\u51710\u6761\u8bb0\u5f55\uff0c\u5f53\u524d\u7b2c1\u9875");
                    paginfoLB.setHorizontalAlignment(SwingConstants.CENTER);
                    panel3.add(paginfoLB, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- nextBT ----
                    nextBT.setText("\u4e0b\u4e00\u9875");
                    nextBT.setIcon(new ImageIcon(getClass().getResource("/images/arrow_forword.gif")));
                    nextBT.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            nextBTActionPerformed(e);
                        }
                    });
                    panel3.add(nextBT, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- lastBT ----
                    lastBT.setText("\u672b\u9875");
                    lastBT.setIcon(new ImageIcon(getClass().getResource("/images/last.gif")));
                    lastBT.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            lastBTActionPerformed(e);
                        }
                    });
                    panel3.add(lastBT, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                }
                TablePanel.add(panel3, BorderLayout.SOUTH);

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(TableTable);
                }
                TablePanel.add(scrollPane2, BorderLayout.CENTER);
            }
            JworkPanel.add(TablePanel, "card9");

            //======== ViewPanel ========
            {
                ViewPanel.setLayout(new BorderLayout());

                //======== scrollPane3 ========
                {
                    scrollPane3.setViewportView(ViewTable);
                }
                ViewPanel.add(scrollPane3, BorderLayout.CENTER);

                //======== panel4 ========
                {
                    panel4.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel4.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel4.getLayout()).rowHeights = new int[]{0, 0};
                    ((GridBagLayout) panel4.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel4.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

                    //---- button5 ----
                    button5.setText("text");
                    panel4.add(button5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- button7 ----
                    button7.setText("text");
                    panel4.add(button7, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- label13 ----
                    label13.setText("text");
                    panel4.add(label13, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- button8 ----
                    button8.setText("text");
                    panel4.add(button8, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- button6 ----
                    button6.setText("text");
                    panel4.add(button6, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                }
                ViewPanel.add(panel4, BorderLayout.SOUTH);
            }
            JworkPanel.add(ViewPanel, "card10");

            //======== StorPanel ========
            {
                StorPanel.setLayout(new BorderLayout());

                //======== scrollPane4 ========
                {
                    scrollPane4.setViewportView(textArea3);
                }
                StorPanel.add(scrollPane4, BorderLayout.CENTER);

                //======== panel5 ========
                {
                    panel5.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel5.getLayout()).columnWidths = new int[]{0, 0, 0};
                    ((GridBagLayout) panel5.getLayout()).rowHeights = new int[]{0, 0, 0, 0};
                    ((GridBagLayout) panel5.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel5.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 1.0E-4};
                }
                StorPanel.add(panel5, BorderLayout.SOUTH);
            }
            JworkPanel.add(StorPanel, "card11");
        }
        contentPane.add(JworkPanel, BorderLayout.CENTER);

        //---- label2 ----
        label2.setText(" Wizzer.cn");
        label2.setEnabled(false);
        label2.setFocusable(false);
        contentPane.add(label2, BorderLayout.SOUTH);
        setSize(865, 645);
        setLocationRelativeTo(getOwner());

        //---- buttonGroup2 ----
        ButtonGroup buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(JDextRButton1);
        buttonGroup2.add(JDextRButton2);

        //---- buttonGroup3 ----
        ButtonGroup buttonGroup3 = new ButtonGroup();
        buttonGroup3.add(radioButton6);
        buttonGroup3.add(radioButton7);
        buttonGroup3.add(radioButton8);

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(radioButton1);
        buttonGroup1.add(radioButton2);
        buttonGroup1.add(radioButton3);
        buttonGroup1.add(radioButton4);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - pandy pandy
    private JToolBar toolBar1;
    private JButton JTsearchButton;
    private JButton JTdictionaryButton;
    private JButton JTexportButton;
    private JButton JTclassButton;
    private JButton JTsysconfigButton;
    private JButton JTdatasourceButton;
    private JButton JThelpButton;
    private JButton JTexitsysButton;
    private JPanel JLitemPanel;
    private JComboBox JLitemdatasourceCBox;
    private JTabbedPane JlitemTPanel;
    private JScrollPane JSP1;
    private JList JLitemTableList;
    private JScrollPane JSP2;
    private JList JLitemTableList2;
    private JScrollPane JSP3;
    private JList JLitemTableList3;
    private CardLayout mycard = new CardLayout();//主面板布局
    private JPanel JworkPanel;
    private JPanel JwelcomePanel;
    private JLabel label1;
    private JPanel JsearchPanel;
    private JScrollPane scrollPane8;
    private JTextArea sqlnoteTArea;
    private JScrollPane scrollPane1;
    private JTextArea sqlrsTArea;
    private JPanel panel1;
    private JLabel label12;
    private JButton runsqlButton;
    private JPanel JdictionaryPanel;
    private JPanel panel2;
    private JLabel label14;
    private JTextField JDpathTField;
    private JButton JDselectpathButton;
    private JLabel label15;
    private JRadioButton JDextRButton1;
    private JRadioButton JDextRButton2;
    private JButton JDrunButton;
    private JScrollPane scrollPane9;
    private JTextArea JDsqlnoteArea;
    private JPanel panel12;
    private JLabel label16;
    private JTextField JDtablenameTArea;
    private JComboBox JDtablelistCBox;
    private JButton JDsearchButton;
    private JLabel label17;
    private JLabel JDtablejsLabel;
    private JLabel label19;
    private JPanel JexportPanel;
    private JLabel label23;
    private JPanel JclassPanel;
    private JPanel panel11;
    private JLabel label8;
    private JTextField syspage;
    private JButton selalltableBT;
    private JLabel label9;
    private JTextField sysclassurl;
    private JButton classurlBT;
    private JButton buldclass;
    private JLabel label11;
    private JScrollPane scrollPane5;
    private JList classlist;
    private JPanel JsysconfigPanel;
    private JPanel panel13;
    private JLabel label20;
    private JTextField textField3;
    private JLabel label18;
    private JTextField textField1;
    private JLabel label21;
    private JTextField textField4;
    private JLabel modellab;
    private JTextField modelname;
    private JButton button9;
    private JButton button1;
    private JLabel label24;
    private JRadioButton radioButton6;
    private JRadioButton radioButton7;
    private JRadioButton radioButton8;
    private JLabel label22;
    private JCheckBox checkBox4;
    private JCheckBox checkBox5;
    private JCheckBox checkBox6;
    private JCheckBox checkBox7;
    private JCheckBox checkBox8;
    private JCheckBox checkBox9;
    private JLabel label25;
    private JPanel JdatasourcePanel;
    private Vector popvt;//保存的数据源向量
    private JScrollPane scrollPane6;
    private String systablecolname[] = {"数据库名", "驱动程序", "库URL", "登陆名", "密码"};
    private JTable JDtable;
    private JPanel panel6;
    private JLabel label3;
    private JPanel panel7;
    private JTextField drivertext;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JRadioButton radioButton3;
    private JRadioButton radioButton4;
    private JLabel label4;
    private JPanel panel8;
    private JTextField urljtext;
    private JLabel label5;
    private JPanel panel9;
    private JTextField userjtext;
    private JButton modifyBT;
    private JLabel label6;
    private JPanel panel10;
    private JTextField passwordjtext;
    private JTextField author;
    private JButton delBT;
    private JLabel label7;
    private JLabel label99;
    private JTextField dbnamejtext;
    private JButton flushBT;
    private JPanel JhelpPanel;
    private JLabel label26;
    private JPanel TablePanel;
    private JPanel panel3;
    private JButton firstBT;
    private JButton purviewBT;
    private JLabel paginfoLB;
    private JButton nextBT;
    private JButton lastBT;
    private JScrollPane scrollPane2;
    private JTable TableTable;
    private JPanel ViewPanel;
    private JScrollPane scrollPane3;
    private JTable ViewTable;
    private JPanel panel4;
    private JButton button5;
    private JButton button7;
    private JLabel label13;
    private JButton button8;
    private JButton button6;
    private JPanel StorPanel;
    private JScrollPane scrollPane4;
    private JTextArea textArea3;
    private JPanel panel5;
    private JLabel label2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
