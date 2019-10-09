<?php
require 'filter.php';
if (!isset($_SESSION)) {
	session_start();
	if ($_SERVER["REQUEST_METHOD"] == "POST") {
		$username = filter($_POST["username"]);
		$password = md5($_POST["password"]);
		try {
			$db = new PDO("sqlite:database/database.db");
		} catch (PDOException $e) {
			echo "Connection error ".$e->getMessage();
		}
		$sql = "SELECT * FROM user WHERE username = '$username'";
		$query = $db->prepare($sql);
		$query->execute();
		$data = $query->fetch();
		if ($data["username"] == $username && $data["password"] == $password) {
			$_SESSION["user_id"] = $data["id"];
			$_SESSION["username"] = $username;
			header("location: index.php");
		}else{
			echo "<div class='container'><br><div class='alert alert-danger'><center>Username or Password incorrect ! Please try again.</center></div></div>";
		}
	}
}
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Loigin || Outgoings</title>
	<link rel="stylesheet" type="text/css" href="lib/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="lib/style.css">
</head>
<body>
	<div class="loginbody">
		<div class="loginform">
			<form method="POST">
				<input type="text" class="form-control" name="username" placeholder="Username" autocomplete="off">
				<br>
			    <input type="password" class="form-control" name="password" placeholder="Password">
			    <br>
			    <input type="submit" class="btn btn-primary login-btn" value="Login">
			</form>
			<br><br>
			<p>Don't have account ? <a href="signup.php">Signup</a> here</p>
		</div>
	</div>
</body>
</html>