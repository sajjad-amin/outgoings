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
		$title = filter($_POST["title"]);
		$description = filter($_POST["description"]);
		$date = filter($_POST["date"]);
		$amount = filter($_POST["amount"]);
		if ($title != "" && $date != "" && $amount != "") {
			$sql = "INSERT INTO budget(user_id, title, description, date, amount) VALUES ('$user_id', '$title', '$description', '$date', '$amount')";
			$query = $db->prepare($sql);
			if ($query->execute()) {
				header("location: index.php");
			}
		}
	}
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>New Budget || Outgoings</title>
	<link rel="stylesheet" type="text/css" href="lib/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="lib/style.css">
</head>
<body>
	<div class="container">
		<div class="jumbotron">
			<h1>New Budget</h1>
		</div>
		<form method="post">
			<table class="table">
				<tr>
					<th class="form-title">Title</th>
					<td>
						<input class="form-control" type="text" name="title">
					</td>
				</tr>
				<tr>
					<th class="form-title">Description</th>
					<td>
						<textarea class="form-control" rows="5" name="description"></textarea>
					</td>
				</tr>
				<tr>
					<th class="form-title">Issue Date</th>
					<td>
						<input class="form-control" type="date" name="date">
					</td>
				</tr>
				<tr>
					<th class="form-title">Amount</th>
					<td>
						<input class="form-control" type="number" name="amount">
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input class="btn btn-success btn-inp" type="submit" value="Create">
						<a href="index.php" class="btn btn-danger btn-inp">Cancel</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>