<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Rocket Ruckus - Sign Up</title>
</head>
<body>

<% String error = (String)request.getAttribute("error"); if (error!=null && !error.isEmpty()) out.print(error+"<br>"); %>

<form action="signup" method="post">
<label for="username">Username</label><br>
<input type="text" id="username" name="user" placeholder="Username"><br>
<label for="Email">Email</label><br>
<input type="text" id="email" name="mail" placeholder="Email"><br>
<label for="password">Password</label><br>
<input type="password" id="password" name="pass" placeholder="Password"><br>
<button type="submit">Register</button>
</form>

</body>
</html>