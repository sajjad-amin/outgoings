<?php
session_start();
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Budget || Outgoings</title>
	<link rel="stylesheet" type="text/css" href="lib/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="lib/style.css">
	<script src="lib/jquery.min.js"></script>
</head>
<body>
	<div class="container">
		<div class="jumbotron">
			<?php
			require 'filter.php';
			$user_id = $_SESSION["user_id"];
			try {
				$db = new PDO("sqlite:database/database.db");
			} catch (PDOException $e) {
				echo "Connection error ".$e->getMessage();
			}
			$page_id = $_GET["id"];
			$sql = "SELECT * FROM budget WHERE user_id = $user_id AND id = '$page_id' ";
			$query = $db->prepare($sql);
			$query->execute();
			$budgetData = $query->fetch(PDO::FETCH_OBJ);
			echo "<h1>$budgetData->title</h1>
			<p><strong>Date of issue : </strong>$budgetData->date</p>
			$budgetData->description<br><br>";
			if ($_SERVER["REQUEST_METHOD"] == "POST") {
				$date = filter($_POST["date"]);
				$amount = filter($_POST["amount"]);
				$description = filter($_POST["description"]);
				if ($date != "" && $amount != "" && $description != "") {
					$sql = "INSERT INTO data(user_id, budget_id, date, amount, description) VALUES ('$user_id', '$page_id', '$date', '$amount', '$description')";
					$query = $db->prepare($sql);
					$query->execute();
				}
			}
			?>
			<p><strong>Budget : </strong><span id="total-budget"></span><br><strong>Remaining : </strong><span id="remaining"></span></p>
			<div class="action-budget-panel">
				<a class="btn btn-default" href="index.php">Home</a>
				<a class="btn btn-info" href="edit_budget.php?id=<?php echo $budgetData->id;?>">Edit Budget</a>
			</div>
			<button class="btn btn-primary toggle-btn">OPEN DATA ENTRY PANEL</button>
			<br><br>
			<div class="data-entry-panel">
				<form method="post">
					<table class="table">
						<tr>
							<th class="form-title">Date</th>
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
							<th class="form-title">Description</th>
							<td>
								<textarea class="form-control" rows="5" name="description"></textarea>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<input class="btn btn-success btn-inp" type="submit" value="+ Add">
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
		<table class="table table-bordered">
			<tr class="danger">
				<th><center>Date</center></th>
				<th><center>Amount</center></th>
				<th><center>Description</center></th>
				<th><center>Action</center></th>
			</tr>
			<?php
				$sql = "SELECT * FROM data WHERE user_id = $user_id AND budget_id = '$page_id' ORDER BY date DESC";
				$query = $db->prepare($sql);
				$query->execute();
				while ($data = $query->fetch(PDO::FETCH_OBJ)) {
					echo "<tr>
					<td><center>$data->date</center></td>
					<td><center>$data->amount</center></td>
					<td>$data->description</td>
					<td><center>
					<a class='btn btn-danger btn-xs' href='edit_data.php?id=$data->id&budget_id=$page_id'>Edit</a>
					</center></td>
					</tr>";
				}
			?>
		</table>
	</div>
	<?php
		$sql = "SELECT SUM(amount) FROM data WHERE user_id = $user_id AND budget_id = '$page_id'";
		$query = $db->prepare($sql);
		$query->execute();
		$sumOfSpend = $query->fetchColumn();
	?>
	<script>
		$(".toggle-btn").click(function(){
	        $(".data-entry-panel").toggle("fast");
	        if($(this).text() == "OPEN DATA ENTRY PANEL"){
	            $(this).text("CLOSE DATA ENTRY PANEL");
	        }else{
	            $(this).text("OPEN DATA ENTRY PANEL");
	        }
	    });
	    $("#total-budget").text("<?php echo $budgetData->amount;?>");
	    $("#remaining").text("<?php echo $budgetData->amount-$sumOfSpend;?>");
	    var tabledata = $(".table-bordered").html();
		$(".table-bordered").html(tabledata.replace(/,/g, '<br>'));
	</script>
</body>
</html>