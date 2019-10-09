<?php
	require 'filter.php';
	session_start();
	$user_id = $_SESSION["user_id"];
	try {
		$db = new PDO("sqlite:database/database.db");
	} catch (PDOException $e) {
		echo "Connection error ".$e->getMessage();
	}
	$budgetID = filter($_GET["id"]);
	$sql = "SELECT * FROM budget WHERE id = '$budgetID'";
	$query = $db->prepare($sql);
	$query->execute();
	$data = $query->fetch(PDO::FETCH_OBJ);

	if ($_SERVER["REQUEST_METHOD"] == "POST") {
		$title = filter($_POST["title"]);
		$description = filter($_POST["description"]);
		$date = filter($_POST["date"]);
		$amount = filter($_POST["amount"]);
		if ($title != "" && $date != "" && $amount != "") {
			$sql = "UPDATE budget SET title = '$title', description = '$description', date = '$date', amount = '$amount' WHERE user_id = $user_id AND id = '$budgetID'";
			$query = $db->prepare($sql);
			if ($query->execute()) {
				header("location: budget.php?id=$budgetID");
			}
		}
	}
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Edit Budget || Outgoings</title>
	<link rel="stylesheet" type="text/css" href="lib/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="lib/style.css">
	<script src="lib/jquery.min.js"></script>
</head>
<body>
	<div class="container">
		<div class="jumbotron">
			<h1>Edit Budget</h1>
		</div>
		<form method="post">
			<table class="table">
				<tr>
					<th class="form-title">Title</th>
					<td>
						<input class="form-control" type="text" name="title" value="<?php echo $data->title;?>">
					</td>
				</tr>
				<tr>
					<th class="form-title">Description</th>
					<td>
						<textarea class="form-control" rows="5" name="description"><?php echo $data->description;?></textarea>
					</td>
				</tr>
				<tr>
					<th class="form-title">Issue Date</th>
					<td>
						<input class="form-control" type="date" name="date" value="<?php echo $data->date;?>">
					</td>
				</tr>
				<tr>
					<th class="form-title">Amount</th>
					<td>
						<input class="form-control" type="number" name="amount"
						value="<?php echo $data->amount;?>">
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input class="btn btn-success btn-inp" type="submit" value="Update">
						<a class="btn btn-inp btn-danger" href="delete_budget.php?id=<?php echo $budgetID;?>">Delete</a>
						<a class="btn btn-inp btn-warning" href="budget.php?id=<?php echo $budgetID;?>">Cancel</a>
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