<?php
session_start();
$user_id = $_SESSION["user_id"];
try {
	$db = new PDO("sqlite:database/database.db");
} catch (PDOException $e) {
	echo "Connection error ".$e->getMessage();
}
if ($_SERVER["REQUEST_METHOD"] == "GET") {
	$id = $_GET["id"];
	$budget_id = $_GET["budget_id"];
	$sql = "DELETE FROM data WHERE user_id = $user_id AND id = '$id'";
	$query = $db->prepare($sql);
	if ($query->execute()) {
		header("location: budget.php?id=$budget_id");
	}
}
?>