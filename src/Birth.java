import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Birth {
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        Birth birth = new Birth();
        birth.birthPoJo("jdbc:mysql://localhost:3306/blue14","root","391231","profession_course_picture","C:\\Users\\Administrator\\Desktop\\逆向工程");
        birth.birthDao("jdbc:mysql://localhost:3306/blue14","root","391231","profession_course_picture","C:\\Users\\Administrator\\Desktop\\逆向工程");
        birth.birthService("jdbc:mysql://localhost:3306/blue14","root","391231","profession_course_picture","C:\\Users\\Administrator\\Desktop\\逆向工程");
        birth.birthServiceImpl("jdbc:mysql://localhost:3306/blue14","root","391231","profession_course_picture","C:\\Users\\Administrator\\Desktop\\逆向工程");
        birth.birthController("jdbc:mysql://localhost:3306/blue14","root","391231","profession_course_picture","C:\\Users\\Administrator\\Desktop\\逆向工程");
//        birth.birthMyBatis("jdbc:mysql://localhost:3306/blue14","root","391231","profession_course_picture","C:\\Users\\Administrator\\Desktop\\逆向工程");
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

    private void birthPoJo(String url,String user,String password,String tableName,String pathValue) throws IOException, SQLException, ClassNotFoundException {
        Path path = Paths.get(pathValue+"/"+toUpper(getColName(tableName))+".java");
        Files.deleteIfExists(path);
        Files.createFile(path);
        String value = createPoJo(url,user,password,tableName);
        OutputStream outputStream = new FileOutputStream(path.toFile());
        byte[] buffer = value.getBytes();
        outputStream.write(buffer);
        outputStream.flush();
    }
    private void birthDao(String url,String user,String password,String tableName,String pathValue) throws IOException, SQLException, ClassNotFoundException {
        Path path = Paths.get(pathValue+"/"+toUpper(getColName(tableName))+"Dao.java");
        Files.deleteIfExists(path);
        Files.createFile(path);
        String value = createDao(url,user,password,tableName);
        OutputStream outputStream = new FileOutputStream(path.toFile());
        byte[] buffer = value.getBytes();
        outputStream.write(buffer);
        outputStream.flush();
    }
    private void birthService(String url,String user,String password,String tableName,String pathValue) throws IOException, SQLException, ClassNotFoundException {
        Path path = Paths.get(pathValue+"/"+toUpper(getColName(tableName))+"Service.java");
        Files.deleteIfExists(path);
        Files.createFile(path);
        String value = createService(url,user,password,tableName);
        OutputStream outputStream = new FileOutputStream(path.toFile());
        byte[] buffer = value.getBytes();
        outputStream.write(buffer);
        outputStream.flush();
    }
    private void birthServiceImpl(String url,String user,String password,String tableName,String pathValue) throws IOException, SQLException, ClassNotFoundException {
        Path path = Paths.get(pathValue+"/"+toUpper(getColName(tableName))+"ServiceImpl.java");
        Files.deleteIfExists(path);
        Files.createFile(path);
        String value = createServiceImpl(url,user,password,tableName);
        OutputStream outputStream = new FileOutputStream(path.toFile());
        byte[] buffer = value.getBytes();
        outputStream.write(buffer);
        outputStream.flush();
    }
    private void birthController(String url,String user,String password,String tableName,String pathValue) throws IOException, SQLException, ClassNotFoundException {
        Path path = Paths.get(pathValue+"/"+toUpper(getColName(tableName))+"Controller.java");
        Files.deleteIfExists(path);
        Files.createFile(path);
        String value = createController(url,user,password,tableName);
        OutputStream outputStream = new FileOutputStream(path.toFile());
        byte[] buffer = value.getBytes();
        outputStream.write(buffer);
        outputStream.flush();
    }
    private void birthMyBatis(String url,String user,String password,String tableName,String pathValue) throws IOException, SQLException, ClassNotFoundException {
//        Path path = Paths.get(pathValue+"/"+toUpper(getColName(tableName))+"Mapper.xml");
//        Files.deleteIfExists(path);
//        Files.createFile(path);
        String value = createMyBatis(url,user,password,tableName);
//        OutputStream outputStream = new FileOutputStream(path.toFile());
//        byte[] buffer = value.getBytes();
//        outputStream.write(buffer);
//        outputStream.flush();
    }

    /** -------------------------------------二级service----------------------------------------------- **/

    private String createPoJo(String url,String user,String password,String tableName) throws SQLException, ClassNotFoundException {
        StringBuffer result = new StringBuffer();
        Map<String,Object> info = getTableInfo(url,user,password,tableName);
        String categoryName = (String) info.get("categoryName");
        List<String> colNames = (List<String>) info.get("colNames");
        List<String> colNameTypes = (List<String>) info.get("colNameTypes");
        result.append("public class "+categoryName+" {\n\n");
        for (int i=0;i<colNames.size();i++) {
            result.append("\tprivate "+colNameTypes.get(i)+" "+colNames.get(i)+";\r\n");
        }
        result.append("\r\n");
        for (int i=0;i<colNames.size();i++) {
            result.append(GetSetMethodToString(getGetSetMethod(colNames.get(i),colNameTypes.get(i)))+"\n\n");
        }
        result.append(getEqualMethod(categoryName,colNames,colNameTypes));
        result.append("\n\n"+getHashCodeMethod(categoryName,colNames,colNameTypes));
        result.append("\n}");
        return result.toString();
    }
    private String createDao(String url,String user,String password,String tableName) throws SQLException, ClassNotFoundException {
        StringBuffer result = new StringBuffer();
        Map<String,Object> info = getTableInfo(url, user, password, tableName);
        String categoryName = (String) info.get("categoryName");
        List<String> colNames = (List<String>) info.get("colNames");
        List<String> colNameTypes = (List<String>) info.get("colNameTypes");
        result.append("public interface "+categoryName+"Dao {\n");
        result.append("\n");
        result.append("\tint add"+categoryName+"("+categoryName+" "+toSmaller(categoryName)+");\n\n");
        result.append("\tint delete"+categoryName+"(int "+toSmaller(categoryName)+"ID);\n\n");
        result.append("\t"+categoryName+" get"+categoryName+"ByID(int "+toSmaller(categoryName)+"ID)"+";\n\n");
        result.append("\tList<"+categoryName+"> get"+categoryName+"s(");
        for (int i=0;i<colNames.size();i++) {
            result.append("@Param(\""+toSmaller(colNames.get(i))+"\")"+
                    colNameTypes.get(i)+" "+toSmaller(colNames.get(i))+
                    "");
            if(i+1!=colNames.size()) {
                result.append(",");
            }
        }
        result.append(");\n\n");
        result.append("\tint update"+categoryName+"("+categoryName+" "+toSmaller(categoryName)+");\n\n");
        result.append("}");
        return result.toString();
    }
    private String createService(String url,String user,String password,String tableName) throws SQLException, ClassNotFoundException {
        StringBuffer result = new StringBuffer();
        Map<String,Object> info = getTableInfo(url, user, password, tableName);
        String categoryName = (String) info.get("categoryName");
        List<String> colNames = (List<String>) info.get("colNames");
        List<String> colNameTypes = (List<String>) info.get("colNameTypes");
        result.append("public interface "+categoryName+"Service {\n");
        result.append("\n");
        result.append("\tboolean add"+categoryName+"("+categoryName+" "+toSmaller(categoryName)+");\n\n");
        result.append("\tboolean delete"+categoryName+"(int "+toSmaller(categoryName)+"ID);\n\n");
        result.append("\t"+categoryName+" get"+categoryName+"ByID(int "+toSmaller(categoryName)+"ID)"+";\n\n");
        result.append("\tList<"+categoryName+"> get"+categoryName+"s(");
        for (int i=0;i<colNames.size();i++) {
            result.append(colNameTypes.get(i)+" "+toSmaller(colNames.get(i)));
            if(i+1!=colNames.size()) {
                result.append(",");
            }
        }
        result.append(");\n\n");
        result.append("\tboolean update"+categoryName+"("+categoryName+" "+toSmaller(categoryName)+");\n\n");
        result.append("}");
        return result.toString();
    }
    private String createServiceImpl(String url,String user,String password,String tableName) throws SQLException, ClassNotFoundException {
        StringBuffer result = new StringBuffer();
        Map<String,Object> info = getTableInfo(url, user, password, tableName);
        String categoryName = (String) info.get("categoryName");
        List<String> colNames = (List<String>) info.get("colNames");
        List<String> colNameTypes = (List<String>) info.get("colNameTypes");
        result.append("@Service\n");
        result.append("public class "+toUpper(categoryName)+"ServiceImpl implements "+toUpper(categoryName)+"Service{\n\n");
        result.append("\t@Autowired\n");
        result.append("\tprivate "+toUpper(categoryName)+"Dao "+toSmaller(categoryName)+"Dao;\n\n");
        result.append("\t@Override\n");
        result.append("\tpublic boolean add"+categoryName+"("+categoryName+" "+toSmaller(categoryName)+") {\n");
        result.append("\t\treturn "+toSmaller(categoryName)+"Dao.add"+toUpper(categoryName)+"("+toSmaller(categoryName)+")>0;\n");
        result.append("\t}\n\n");
        result.append("\t@Override\n");
        result.append("\tpublic boolean delete"+categoryName+"(int "+toSmaller(categoryName)+"ID) {\n");
        result.append("\t\treturn "+toSmaller(categoryName)+"Dao.delete"+toUpper(categoryName)+"("+toSmaller(categoryName)+"ID)>0;\n");
        result.append("\t}\n\n");
        result.append("\t@Override\n");
        result.append("\tpublic "+categoryName+" get"+categoryName+"ByID(int "+toSmaller(categoryName)+"ID) {\n");
        result.append("\t\treturn "+toSmaller(categoryName)+"Dao.get"+toUpper(categoryName)+"ByID("+toSmaller(categoryName)+"ID);\n");
        result.append("\t}\n\n");
        result.append("\t@Override\n");
        result.append("\tpublic List<"+categoryName+"> get"+categoryName+"s(");
        for (int i=0;i<colNames.size();i++) {
            result.append(colNameTypes.get(i)+" "+toSmaller(colNames.get(i)));
            if(i+1!=colNames.size()) {
                result.append(",");
            }
        }
        result.append(") {\n");
        result.append("\t\treturn "+toSmaller(categoryName)+"Dao."+"get"+toUpper(categoryName)+"s(");
        for (int i=0;i<colNames.size();i++) {
            result.append(toSmaller(colNames.get(i)));
            if(i+1!=colNames.size()) {
                result.append(",");
            }
        }
        result.append(");\n");
        result.append("\t}\n\n");
        result.append("\t@Override\n");
        result.append("\tpublic boolean update"+categoryName+"("+categoryName+" "+toSmaller(categoryName)+") {\n");
        result.append("\t\treturn "+toSmaller(categoryName)+"Dao.update"+toUpper(categoryName)+"("+toSmaller(categoryName)+")>0;\n");
        result.append("\t}\n\n");
        result.append("}");
        return result.toString();
    }
    private String createController(String url,String user,String password,String tableName) throws SQLException, ClassNotFoundException {
        StringBuffer result = new StringBuffer();
        Map<String,Object> info = getTableInfo(url, user, password, tableName);
        String categoryName = (String) info.get("categoryName");
        List<String> colNames = (List<String>) info.get("colNames");
        List<String> colNameTypes = (List<String>) info.get("colNameTypes");
        result.append("import java.util.Map;\n");
        result.append("import java.util.List;\n\n");
        result.append("@Controller@RequestMapping(\""+toSmaller(categoryName)+"\")\n");
        result.append("public class "+toUpper(categoryName)+"Controller{\n\n");
        result.append("\t@Autowired\n");
        result.append("\tprivate "+toUpper(categoryName)+"Service "+toSmaller(categoryName)+"Service;\n\n");
        result.append("\t@ResponseBody@RequestMapping(\"add"+toSmaller(categoryName)+"\")\n");
        result.append("\tpublic Map<String,Object> add"+categoryName+"("+categoryName+" "+toSmaller(categoryName)+") {\n");
        result.append("\t\tif("+toSmaller(categoryName)+"Service.add"+toUpper(categoryName)+"("+toSmaller(categoryName)+"))\n");
        result.append("\t\t\treturn Response.getResponseMap(0,\"添加成功\",null);\n");
        result.append("\t\telse\n");
        result.append("\t\t\treturn Response.getResponseMap(1,\"添加失败\",null);\n");
        result.append("\t}\n\n");
        result.append("\t@ResponseBody@RequestMapping(\"delete"+toSmaller(categoryName)+"\")\n");
        result.append("\tpublic Map<String,Object> delete"+categoryName+"(@RequestParam(\""+toSmaller(categoryName)+"ID\")" +
                "int "+toSmaller(categoryName)+"ID) {\n");
        result.append("\t\tif("+toSmaller(categoryName)+"Service.delete"+toUpper(categoryName)+"("+toSmaller(categoryName)+"ID)) {\n");
        result.append("\t\t\treturn Response.getResponseMap(0,\"修改成功\",null);\n");
        result.append("\t\t}\n");
        result.append("\t\telse {\n");
        result.append("\t\t\treturn Response.getResponseMap(1,\"修改失败\",null);\n");
        result.append("\t\t}\n");
        result.append("\t}\n\n");
        result.append("\t@ResponseBody@RequestMapping(\"get"+categoryName+"ByID\")\n");
        result.append("\tpublic "+categoryName+" get"+categoryName+"ByID(int "+toSmaller(categoryName)+"ID) {\n");
        result.append("\t\treturn "+toSmaller(categoryName)+"Service.get"+toUpper(categoryName)+"ByID("+toSmaller(categoryName)+"ID);\n");
        result.append("\t}\n\n");
        result.append("\t@ResponseBody@RequestMapping(\"get"+categoryName+"s\")\n");
        result.append("\tpublic List<"+categoryName+"> get"+categoryName+"s(");
        result.append(") {\n");
        result.append("\t\treturn "+toSmaller(categoryName)+"Service."+"get"+toUpper(categoryName)+"s(");
        for (int i=0;i<colNames.size();i++) {
            result.append("null");
            if(i+1!=colNames.size()) {
                result.append(",");
            }
        }
        result.append(");\n");
        result.append("\t}\n\n");
        result.append("\t@ResponseBody@RequestMapping(\"get"+categoryName+"sDeal\")\n");
        result.append("\tpublic Map<String,Object> get"+categoryName+"sDeal(");
        for (int i=0;i<colNames.size();i++) {
            result.append("@RequestParam(value = \""+toSmaller(colNames.get(i))+"\",required=false)");
            result.append(colNameTypes.get(i)+" "+toSmaller(colNames.get(i)));
            if(i+1!=colNames.size()) {
                result.append(",");
            }
        }
        result.append(") {\n");
        result.append("\t\treturn Response.getResponseMap(0,\"\","+toSmaller(categoryName)+"Service."+"get"+toUpper(categoryName)+"s(");
        for (int i=0;i<colNames.size();i++) {
            result.append(toSmaller(colNames.get(i)));
            if(i+1!=colNames.size()) {
                result.append(",");
            }
        }
        result.append("));\n");
        result.append("\t}\n\n");
        result.append("\t@ResponseBody@RequestMapping(\"update"+categoryName+"\")\n");
        result.append("\tpublic Map<String,Object> update"+categoryName+"("+categoryName+" "+toSmaller(categoryName)+") {\n");
        result.append("\t\tif("+toSmaller(categoryName)+"Service.update"+toUpper(categoryName)+"("+toSmaller(categoryName)+")) {\n");
        result.append("\t\t\treturn Response.getResponseMap(0,\"\",null);\n");
        result.append("\t\t}");
        result.append(" else {\n");
        result.append("\t\t\treturn Response.getResponseMap(1,\"\",null);\n");
        result.append("\t\t}\n");
        result.append("\t}\n\n");
        result.append("}");
        return result.toString();
    }
    private String createMyBatis(String url,String user,String password,String tableName) throws SQLException, ClassNotFoundException {
        StringBuffer result = new StringBuffer();
        Map<String,Object> info = getTableInfo(url, user, password, tableName);
        String categoryName = (String) info.get("categoryName");
        List<String> colNames = (List<String>) info.get("colNames");
        List<String> colNameTypes = (List<String>) info.get("colNameTypes");
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        result.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n");
        result.append("<mapper namespace=\"请自行替换\">\n");
        result.append("\t<resultMap id=\""+toUpper(categoryName)+"Map\" type=\""+toUpper(categoryName)+"\" >\n");
        result.append("\t</resultMap>\n");
        for (int i=0;i<colNames.size();i++) {
            result.append("\t\t<result column=\""+"");
        }
        result.append("</mapper>");
        System.out.printf(result.toString());
        return result.toString();
    }

    /** -------------------------------------三级service----------------------------------------------- **/

    private String GetSetMethodToString(List<String> methods) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i=0;i<methods.size();i++) {
            if(i == methods.size()-1) {
                stringBuffer.append(methods.get(i));
            } else {
                stringBuffer.append(methods.get(i)+"\r\n");
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
        result += "\t\t"+toUpper(className)+" "+toSmaller(className) + " = ("+toUpper(className)+") o;\n\n";
        stringBuffer.append(result);
        for (int i=0;i<colNames.size();i++) {
            if(!isClass(colNameTypes.get(i))) {
                stringBuffer.append("\t\tif("+toSmaller(colNames.get(i))+" != "+toSmaller(className)+"."+toSmaller(colNames.get(i))+") return false;\n");
            } else {
                stringBuffer.append("\t\tif("+toSmaller(colNames.get(i))+" != null ? !"+
                        toSmaller(colNames.get(i))+".equals("+toSmaller(className)+"."+toSmaller(colNames.get(i))+
                        ") : "+toSmaller(className)+"."+toSmaller(colNames.get(i)) +
                        " !=null) return false;\n");
            }
        }
        stringBuffer.append("\n\t\treturn true;\n");
        stringBuffer.append("\t}");
        return stringBuffer.toString();
    }
    private String getHashCodeMethod(String className,List<String> colNames,List<String> colNameTypes) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\t@Override\n" +
                "\tpublic int hashCode() {\n" +
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
    private Map<String,Object> getTableInfo(String url,String user,String password,String tableName) throws SQLException, ClassNotFoundException {
        Map<String,Object> result = new HashMap<>();
        linkMysqlDataBase(url,user,password);
        String sql = "desc " + tableName;
        statement = connection.createStatement();
        statement.execute(sql);
        resultSet = statement.getResultSet();
        List<String> colNames = new ArrayList<>();
        List<String> colNameTypes = new ArrayList<>();
        while (resultSet.next()) {
            colNames.add(getColName(resultSet.getString("Field")));
            String colNameType = resultSet.getString("Type").split("\\(")[0];
            boolean isNull = resultSet.getBoolean("Null");
            if (!isNull) {
                switch (colNameType) {
                    case "bit":colNameTypes.add("boolean");break;
                    case "text":
                    case "char":
                    case "varchar":colNameTypes.add("String");break;
                    case "mediumint":
                    case "int":colNameTypes.add("int");break;
                    case "boolean":
                    case "tinyint":
                    case "smallint":colNameTypes.add("short");break;
                    case "bigint":colNameTypes.add("long");break;
                    case "float":colNameTypes.add("float");break;
                    case "double":colNameTypes.add("double");break;
                    case "decimal":colNameTypes.add("BigDecimal");break;
                    case "date":colNameTypes.add("Date");break;
                    case "time":colNameTypes.add("Time");break;
                    case "datetime":colNameTypes.add("Timestamp");break;
                    case "timestamp":colNameTypes.add("Timestamp");break;
                    case "year":colNameTypes.add("Date");
//                    case "blob":colNameTypes.add("Byte[]");break;
                    default: colNameTypes.add(colNameType);break;
                }
            } else {
                switch (colNameType) {
                    case "bit":
                        colNameTypes.add("Boolean");
                        break;
                    case "text":
                    case "char":
                    case "varchar":
                        colNameTypes.add("String");
                        break;
                    case "mediumint":
                    case "int":
                        colNameTypes.add("Integer");
                        break;
                    case "boolean":
                    case "tinyint":
                    case "smallint":
                        colNameTypes.add("Short");
                        break;
                    case "bigint":
                        colNameTypes.add("Long");
                        break;
                    case "float":
                        colNameTypes.add("Float");
                        break;
                    case "double":
                        colNameTypes.add("Double");
                        break;
                    case "decimal":
                        colNameTypes.add("BigDecimal");
                        break;
                    case "date":
                        colNameTypes.add("Date");
                        break;
                    case "time":
                        colNameTypes.add("Time");
                        break;
                    case "datetime":
                        colNameTypes.add("Timestamp");
                        break;
                    case "timestamp":
                        colNameTypes.add("Timestamp");
                        break;
                    case "year":
                        colNameTypes.add("Date");
//                    case "blob":colNameTypes.add("Byte[]");break;
                    default:
                        colNameTypes.add(colNameType);
                        break;
                }
            }
        }
        String categoryName = toUpper(getColName(tableName));
        result.put("categoryName",categoryName);
        result.put("colNames",colNames);
        result.put("colNameTypes",colNameTypes);
        return result;
    }

    /** -------------------------------------四级service----------------------------------------------- **/

    private List<String> getGetSetMethod(String colName,String colNameType) {
        List<String> results = new ArrayList<>();
        String bigColName = Character.toUpperCase(colName.charAt(0)) + colName.substring(1);
        results.add("\tpublic "+colNameType+" get"+toUpper(colName)+"() {");
        results.add("\t\treturn this."+colName+";");
        results.add("\t}\n");
        results.add("\tpublic void set"+bigColName+"("+colNameType+" "+colName+") {");
        results.add("\t\tthis."+colName+"="+colName+";");
        results.add("\t}");
        return results;
    }

    /** -------------------------------------五级service----------------------------------------------- **/

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