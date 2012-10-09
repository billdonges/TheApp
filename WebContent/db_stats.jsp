<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jstl/fmt" %>

<jsp:directive.page import="java.util.List" />
<jsp:directive.page import="the.app.admin.DBMgmt" />
<jsp:directive.page import="the.app.admin.Stats" />
<jsp:useBean id="DBMgmt" class="the.app.admin.DBMgmt" scope="session" />
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <link rel="stylesheet" type="text/css" href="css/basic.css" id="thecss">
        <title>TheApp :: DB Statistics</title>
        <script language="javascript">
        <!--
            function toggle(id) {
	            var e = document.getElementById(id);
	            if(e.style.display == 'none') {
	                e.style.display = 'block';
	            } else {
	                e.style.display = 'none';
	            }
	        }
        -->
        </script>
        <style type="text/css">#div1, #div2, #div3 {position:absolute; top: 100; left: 200; width:200; visibility:hidden}</style>        
    </head>
    <body>
        TheApp Project
        <br/><br/>
        Statistics<small>&nbsp;&nbsp;(<a href="index.jsp">home</a>)</small>
        <br/><br/>
        <c:if test="${DBMgmt.messageLength > 0}">
            ${DBMgmt.message}<br/>
        </c:if>
        <c:if test="${DBMgmt.errorMessageLength > 0}">
            ${DBMgmt.errorMessage}<br/>
        </c:if>        
        <br/>
        
        actions:
        &nbsp;&nbsp;<a href="#" onclick="javascript:toggle('searchblock')">connect</a>
        <br/><br/>
        
        <div id="searchblock" style="display:none">
        <form action="db_management" method="post" name="searchform">
        <input type="hidden" id="a" name="a" value="dbstats"/>
        <input type="hidden" id="connect" name="connect" value="new"/>
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
            <tr>
                <td height="30" valign="middle" width="15%" bgcolor="#C0C0C0">IP</td>
                <td height="30" valign="middle" width="15%" bgcolor="#C0C0C0">Port</td>
                <td height="30" valign="middle" width="15%" bgcolor="#C0C0C0">DB</td>
                <td height="30" valign="middle" width="10%" bgcolor="#C0C0C0">Use Validation</td>
                <td height="30" valign="middle" width="15%" bgcolor="#C0C0C0">Username</td>
                <td height="30" valign="middle" width="15%" bgcolor="#C0C0C0">Password</td>
                <td height="30" valign="middle" width="5%" bgcolor="#C0C0C0">Use to Auth</td>
                <td height="30" valign="middle" width="10%" bgcolor="#C0C0C0">&nbsp;</td>
            </tr>
            <tr>
                <td height="30" valign="middle" width="15%"><input type="text" size="10" id="dblocation" name="dblocation" value="${DBMgmt.dbLocation}"/></td>
                <td height="30" valign="middle" width="15%"><input type="text" size="10" id="dbport" name="dbport" value="${DBMgmt.dbPort}"/></td>
                <td height="30" valign="middle" width="15%"><input type="text" size="10" id="dbname" name="dbname" value="${DBMgmt.dbName}"/></td>
                <td height="30" valign="middle" width="10%"><input type="checkbox" id="dbusevalidation" name="dbusevalidation" /></td>
                <td height="30" valign="middle" width="15%"><input type="text" size="10" id="dbusername" name="dbusername" value="${DBMgmt.dbUsername}"/></td>
                <td height="30" valign="middle" width="15%"><input type="text" size="10" id="dbpassword" name="dbpassword" value="${DBMgmt.dbPassword}"/></td>
                <td height="30" valign="middle" width="5%"><input type="checkbox" id="dbusetoauth" name="dbusetoauth" /></td>
                <td height="30" valign="middle" width="10%"><input type="submit" value="search"/></td>
            </tr>
            <tr><td height="20">&nbsp;</td></tr>
        </table>
        </form>
        </div>
        
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
            <tr>
                <td height="30" valign="middle" width="15%" bgcolor="#C0C0C0">DB</td>
                <td height="30" valign="middle" width="85%" bgcolor="#C0C0C0">Statistics</td>
            </tr>
            <tr><td colspan="3" height="10">&nbsp;</td></tr>
            <c:forEach var="stat" items="${DBMgmt.allDbStats}">
            <tr>
                <td height="30" valign="top" width="15%">
                    <a href="db_management?a=dbstats&dbname=${stat.getDb()}">${stat.getDb()}</a>
                </td>
                <td valign="top" width="85%">Server:
                ${stat.getServerUsed()}
                </td>
            </tr>
            <tr>
                <td height="30" valign="top" width="15%">
                    &nbsp;
                </td>
                <td valign="top" width="85%">Database:
                ${stat.getDb()}
                </td>
            </tr>
            <tr>
                <td height="30" valign="top" width="15%">
                    &nbsp;
                </td>
                <td valign="top" width="85%">Collections:
                 ${stat.getCollections()}
                </td>
            </tr>
            <tr>
                <td height="30" valign="top" width="15%">
                    &nbsp;
                </td>
                <td valign="top" width="85%">Objects:
                ${stat.getObjects()}
                </td>
            </tr>
            <tr>
                <td height="30" valign="top" width="15%">
                    &nbsp;
                </td>
                <td valign="top" width="85%">Average Object size:
                ${stat.getAvgObjSize()}
                </td>
            </tr>
            <tr>
                <td height="30" valign="top" width="15%">
                    &nbsp;
                </td>
                <td valign="top" width="85%">Data size:
                ${stat.getDataSize()}
                </td>
            </tr>
            </c:forEach>
        </table>
        
    </body>
</html>