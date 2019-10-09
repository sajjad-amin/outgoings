<?php
	require 'filter.php';
	session_start();
	$user_id = $_SESSION["user_id"];
	try {
		$db = new PDO("sqlite:database/database.db");
	} catch (PDOException $e) {
		echo "Connection error ".$e->getMessage();
	}
	$id = filter($_GET["id"]);
	$budget_id = filter($_GET["budget_id"]);
	$sql = "SELECT * FROM data WHERE id = '$id'";
	$query = $db->prepare($sql);
	$query->execute();
	$data = $query->fetch(PDO::FETCH_OBJ);

	if ($_SERVER["REQUEST_METHOD"] == "POST") {
		$date = filter($_POST["date"]);
		$amount = filter($_POST["amount"]);
		$description = filter($_POST["description"]);
		if ($date != "" && $amount != "" && $description != "") {
			$sql = "UPDATE data SET date = '$date', amount = '$amount', date = '$date', description = '$description' WHERE user_id = $user_id AND id = '$id'";
			$query = $db->prepare($sql);
			if ($query->execute()) {
				header("location: edit_data.php?id=$id&budget_id=$budget_id");
			}
		}
	}
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Edit Data || Outgoings</title>
	<link rel="stylesheet" type="text/css" href="lib/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="lib/style.css">
	<script src="lib/jquery.min.js"></script>
</head>
<body>
	<div class="container">
		<div class="jumbotron">
			<h1>Edit Data</h1>
		</div>
		<form method="post">
			<table class="table">
				<tr>
					<th class="form-title">Date</th>
					<td>
						<input class="form-control" type="date" name="date" value="<?php echo $data->date;?>">
					</td>
				</tr>
				<tr>
					<th class="form-title">Amount</th>
					<td>
						<input class="form-control" type="number" name="amount" value="<?php echo $data->amount;?>">
					</td>
				</tr>
				<tr>
					<th class="form-title">Description</th>
					<td>
						<textarea class="form-control" rows="5" name="description"><?php echo $data->description;?></textarea>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input class="btn btn-success btn-inp" type="submit" value="Update">
						<a class="btn btn-danger btn-inp" href="delete_data.php?id=<?php echo $id;?>&budget_id=<?php echo $budget_id;?>">Delete</a>
						<a class="btn btn-warning btn-inp" href="budget.php?id=<?php echo $budget_id;?>">Back</a>
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