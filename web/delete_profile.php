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
	$sql = "DELETE FROM user WHERE id = $user_id AND id = $id";
	$query = $db->prepare($sql);
	$query->execute();
	$sql = "DELETE FROM budget WHERE user_id = $user_id AND user_id = $id";
	$query = $db->prepare($sql);
	$query->execute();
	$sql = "DELETE FROM data WHERE user_id = $user_id AND user_id = $id";
	$query = $db->prepare($sql);
	$query->execute();
	header("location: logout.php");
}
?>