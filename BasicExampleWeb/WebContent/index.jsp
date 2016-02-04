<!DOCTYPE HTML><%@page language="java"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <title>index</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
    <form action="servlet/hello" method="POST">
        <div>
            Name:
            <input type="text" name="name">
        </div>
        <div>
            <input type="submit">
        </div>
    </form>
    
    <div>
        ${message}
    </div>
</body>
</html>
