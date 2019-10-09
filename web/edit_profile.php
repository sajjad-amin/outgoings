<?php
require 'filter.php';
session_start();
$user_id = $_SESSION["user_id"];
try {
		$db = new PDO("sqlite:database/database.db");
	} catch (PDOException $e) {
		echo "Connection error ".$e->getMessage();
	}
	if ($_SERVER["REQUEST_METHOD"] == "POST") {
		$firstname = filter($_POST["firstname"]);
		$lastname = filter($_POST["lastname"]);
		$email = filter($_POST["email"]);
		$phone = filter($_POST["phone"]);
		$password = md5($_POST["password"]);
		if ($firstname != "" && $lastname != "" && $email != "" && $phone != "" && $password != "") {
			$sql = "UPDATE user SET firstname = '$firstname', lastname = '$lastname', email = '$email', phone = '$phone', password = '$password' WHERE id = $user_id";
			$query = $db->prepare($sql);
			$query->execute();
		}
	}
	$sql = "SELECT * FROM user WHERE id = $user_id";
	$query = $db->prepare($sql);
	$query->execute();
	$user = $query->fetch(PDO::FETCH_OBJ);
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Edit Profile || Outgoings</title>
	<link rel="stylesheet" type="text/css" href="lib/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="lib/style.css">
	<script src="lib/jquery.min.js"></script>
</head>
<body>
	<div class="container">
		<div class="jumbotron">
			<h1>Edit Profile</h1>
		</div>
		<form method="post">
			<table class="table">
				<tr>
					<th class="form-title">Firstname : </th>
					<td>
						<input class="form-control" type="text" name="firstname" value="<?php echo $user->firstname;?>" required>
					</td>
				</tr>
				<tr>
					<th class="form-title">Lastname : </th>
					<td>
						<input class="form-control" type="text" name="lastname" value="<?php echo $user->lastname;?>" required>
					</td>
				</tr>
				<tr>
					<th class="form-title">Email : </th>
					<td>
						<input class="form-control" type="email" name="email" value="<?php echo $user->email;?>" required>
					</td>
				</tr>
				<tr>
					<th class="form-title">Phone : </th>
					<td>
						<input class="form-control" type="text" name="phone" value="<?php echo $user->phone;?>" required>
					</td>
				</tr>
				<tr>
					<th class="form-title">Password : </th>
					<td>
						<input class="form-control" placeholder="Input old or new password" type="password" name="password" required>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input class="btn btn-success btn-inp" type="submit" value="Update">
						<a class="btn btn-inp btn-danger" href="delete_profile.php?id=<?php echo $user_id;?>">Delete Account</a>
						<a class="btn btn-inp btn-warning" href="index.php">Back</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<script>
		$(".btn-danger").click(function(){
			if (!confirm("Are you sure ?")) {
				return false;
			}
		});
	</script>
</body>
</html>