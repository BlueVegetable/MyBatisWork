import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Birth {
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Birth birth = new Birth();
        System.out.println(birth.createPoJo(("admin")));
    }
    private void linkMysqlDataBase(String url,String userName,String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(url,userName,password);
    }
    private String getColName(String origin) {
        StringBuffer stringBuffer = new StringBuffer("");
        boolean flag = false;
        for (int i=0;i<origin.length();i++) {
            char ch = origin.charAt(i);
            if (i == 0) {
                ch = Character.toLowerCase(ch);
            } else {
                if(flag) {
                    ch = Character.toUpperCase(ch);
                    flag = false;
                } else {
                    if(ch == '_') {
                        flag = true;
                        continue;
                    }
                }
            }
            stringBuffer.append(ch);
        }
        return stringBuffer.toString();
    }

    /** -------------------------------------一级service----------------------------------------------- **/

    private String createPoJo(String tableName) throws SQLException, ClassNotFoundException {
        linkMysqlDataBase("jdbc:mysql://localhost:3306/blue14","root","391231");
        String sql = "desc "+tableName;
        statement = connection.createStatement();
        statement.execute(sql);
        resultSet = statement.getResultSet();
        String columnsString = "";
        List<String> colNames = new ArrayList<>();
        List<String> colNameTypes = new ArrayList<>();
        while (resultSet.next()) {
            colNames.add(resultSet.getString("Field"));
            String colNameType = resultSet.getString("Type").split("\\(")[0];
            boolean isNull = resultSet.getBoolean("Null");
            if (!isNull) {
                switch (colNameType) {
                    case "text":
                    case "varchar":colNameTypes.add("String");break;
                    case "int":colNameTypes.add("int");break;
                    case "tinyint":
                    case "smallint":colNameTypes.add("short");break;
                    case "bigint":colNameTypes.add("long");break;
                    default: colNameTypes.add(colNameType);break;
                }
            }
        }
        return null;
    }

    /** -------------------------------------二级service----------------------------------------------- **/

    private String GetSetMethodToString(List<String> methods) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i=0;i<methods.size();i++) {
            if(i == methods.size()-1) {
                stringBuffer.append("\t"+methods.get(i));
            } else {
                stringBuffer.append("\t"+methods.get(i)+"\r\n");
            }
        }
        return stringBuffer.toString();
    }
    private String getEqualMethod(String className,List<String> colNames,List<String> colNameTypes) {
        List<String> bigColNames = new ArrayList<>();
        for (String colName:colNames) {
            bigColNames.add(Character.toUpperCase(colName.charAt(0)) + colName.substring(1));
        }
        StringBuffer stringBuffer = new StringBuffer();
        String result = "\t@Override\n" +
                "\tpublic boolean equals(Object o) {\n" +
                "\t\tif (this == o) return true;\n" +
                "\t\tif (o == null || getClass() != o.getClass()) return false;\n\n";
        result += "\t\t"+toUpper(className)+" "+className + " = ("+toUpper(className)+") o;\n\n";
        stringBuffer.append(result);
        for (int i=0;i<colNames.size();i++) {
            if(!isClass(colNameTypes.get(i))) {
                stringBuffer.append("\t\tif("+toSmaller(colNames.get(i))+" != "+toSmaller(className)+"."+toSmaller(colNames.get(i))+") return false;\n");
            } else {
                stringBuffer.append("\t\tif("+toSmaller(colNames.get(i))+" != null ? !"+
                        toSmaller(colNames.get(i))+".equals("+toSmaller(className)+"."+toSmaller(colNames.get(i))+"!=null) return false;\n");
            }
        }
        stringBuffer.append("\n\t\treturn true;\n");
        stringBuffer.append("\t}");
        return stringBuffer.toString();
    }
    private String getHashCodeMethod(String className,List<String> colNames,List<String> colNameTypes) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\t@Override\n" +
                "\tpublic boolean hashCode() {\n" +
                "\t\tint result = ");
        switch (colNameTypes.get(0)) {
            case "byte":
            case "short":
            case "int":
            case "long":
            case "char":stringBuffer.append(toSmaller(colNames.get(0))+";\n");break;
            case "double":
            case "float":stringBuffer.append("(int)"+toSmaller(colNames.get(0))+";\n");break;
            case "boolean":stringBuffer.append(toSmaller(colNames.get(0)+"?1:0;\n"));break;
            default:stringBuffer.append(toSmaller(colNames.get(0))+"!=null ? "+toSmaller(colNames.get(0))+".hashCode() : 0;\n");
        }
        for (int i=1;i<colNames.size();i++) {
            stringBuffer.append("\t\tresult = 31 * result + ");
            switch (colNameTypes.get(i)) {
                case "byte":
                case "short":
                case "int":
                case "long":
                case "char":stringBuffer.append(toSmaller(colNames.get(i))+";\n");break;
                case "double":
                case "float":stringBuffer.append("(int)"+toSmaller(colNames.get(i))+";\n");break;
                case "boolean":stringBuffer.append(toSmaller(colNames.get(i)+"?1:0;\n"));break;
                default:stringBuffer.append("("+toSmaller(colNames.get(i))+"!=null ? "+toSmaller(colNames.get(i))+".hashCode() : 0);\n");
            }
        }
        stringBuffer.append("\t\treturn result;\n");
        stringBuffer.append("\t}");
        return stringBuffer.toString();
    }

    /** -------------------------------------三级service----------------------------------------------- **/

    private List<String> getGetSetMethod(String colName) {
        List<String> results = new ArrayList<>();
        String bigColName = Character.toUpperCase(colName.charAt(0)) + colName.substring(1);
        results.add("\tpublic "+bigColName+" get"+bigColName+"() {");
        results.add("\t\treturn this."+colName+";");
        results.add("\t}\n");
        results.add("\tpublic void set"+bigColName+"("+bigColName+" "+colName+") {");
        results.add("\t\treturn this."+colName+"="+colName+";");
        results.add("\t}");
        return results;
    }

    /** -------------------------------------四级service----------------------------------------------- **/

    private String toUpper(String origin) {
        return Character.toUpperCase(origin.charAt(0)) + origin.substring(1);
    }
    private String toSmaller(String origin) {
        return Character.toLowerCase(origin.charAt(0)) + origin.substring(1);
    }
    private boolean isClass(String colType) {
        switch (colType) {
            case "byte":
            case "short":
            case "int":
            case "long":
            case "char":
            case "float":
            case "double":
            case "boolean": return false;
            default:return true;
        }
    }
}