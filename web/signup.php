<?php
require 'filter.php';
try {
	$db = new PDO("sqlite:database/database.db");
} catch (PDOException $e) {
	echo "Connection error ".$e->getMessage();
}
if ($_SERVER["REQUEST_METHOD"] == "POST") {
	date_default_timezone_set("Asia/Dhaka");
	$date = date("Y-m-d");
	$firstname = filter($_POST["firstname"]);
	$lastname = filter($_POST["lastname"]);
	$email = filter($_POST["email"]);
	$phone = filter($_POST["phone"]);
	$username = filter($_POST["username"]);
	$password = md5($_POST["password"]);
	$sql = "SELECT username FROM user WHERE username LIKE '%$username%'";
	$query = $db->prepare($sql);
	$query->execute();
	$user = $query->fetch();
	if ($user["username"] != $username && $firstname != "" && $lastname != "" && $email != "" && $phone != "" && $username != "" && $password != "") {
		$sql = "INSERT INTO user (firstname, lastname, email, phone, reg_date, permission, username, password) VALUES ('$firstname', '$lastname', '$email', '$phone', '$date', 'allow', '$username', '$password')";
		$query = $db->prepare($sql);
		if ($query->execute()) {
			echo "<div class='container'><br><div class='alert alert-success'><center>Your account has been created successfully. <a href='login.php'>Login here</a></center></div></div>";
		}
	}else{
		echo "<div class='container'><br><div class='alert alert-danger'><center>Username exists ! Please try another username.</center></div></div>";
	}
}
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Signup || Outgoings</title>
	<link rel="stylesheet" type="text/css" href="lib/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="lib/style.css">
</head>
<body>
	<div class="loginbody">
		<div class="signupform">
			<center><h1>Signup</h1></center>
			<form method="post">
				<div class="form-group">
					<label>Firstname :</label>
					<input class="form-control" type="text" name="firstname" required>
				</div>
				<div class="form-group">
					<label>Lastname :</label>
					<input class="form-control" type="text" name="lastname" required>
				</div>
				<div class="form-group">
					<label>Email :</label>
					<input class="form-control" type="email" name="email" required>
				</div>
				<div class="form-group">
					<label>Phone :</label>
					<input class="form-control" type="text" name="phone" required>
				</div>
				<div class="form-group">
					<label>Username :</label>
					<input class="form-control" type="text" name="username" required>
				</div>
				<div class="form-group">
					<label>Password :</label>
					<input class="form-control" type="password" name="password" required>
				</div>
				<input class="btn btn-primary login-btn" type="submit" value="Signup">
			</form>
			<p>Already have account ? <a href="login.php">Login</a> here.</p>
		</div>
	</div>
</body>
</html>