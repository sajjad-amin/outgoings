<?php
session_start();
if (!isset($_SESSION["username"])) {
	header("location: login.php");
}
?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Outgoings</title>
	<link rel="stylesheet" type="text/css" href="lib/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="lib/style.css">
</head>
<body>
	<div class="container">
		<div class="jumbotron">
			<h1>OUTGOINGS</h1>
			<?php
				if(isset($_SESSION["username"])){
				    $user_id = $_SESSION["user_id"];
				    try {
				    	$db = new PDO("sqlite:database/database.db");
				    } catch (PDOException $e) {
				    	echo "Connection error ".$e->getMessage();
				    }
				    $sql = "SELECT * FROM user WHERE id = $user_id";
				    $query = $db->prepare($sql);
				    $query->execute();
				    $user = $query->fetch();
				    echo "<p><i>Welcome </i>".$user["firstname"]." ".$user["lastname"]."</p>";
				}
			?>
			<a href="new_budget.php" class="btn btn-primary">+ Create Budget</a>
			<a href="logout.php" class="btn btn-danger btn-inp">Logout</a>
			<a href="edit_profile.php" class="btn btn-warning btn-inp">Edit Profile</a>
		</div>
		<ul class="list-group">
		<?php
			if(isset($_SESSION["username"])){
			    try {
			    	$db = new PDO("sqlite:database/database.db");
			    } catch (PDOException $e) {
			    	echo "Connection error ".$e->getMessage();
			    }
			    $sql = "SELECT * FROM budget WHERE user_id = $user_id ORDER BY date DESC";
			    $query = $db->prepare($sql);
			    $query->execute();
			    while ($data = $query->fetch(PDO::FETCH_OBJ)) {
			    	echo "<a class='list-group-item' href='budget.php?id=$data->id'>
			    	<strong>$data->title</strong>
			    	<span class='badge'>$data->amount</span>
			    	<div class='list-date'>
			    		$data->date
			    	</div>
			    	<div class='list-detales'>
			    		$data->description
			    	</div>
			    	</a>";
			    }
			}
		?>
		</ul>
	</div>
</body>
</html>