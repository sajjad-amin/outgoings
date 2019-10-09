<?php
header('Content-Type: application/json; charset=UTF-8');
function filter($data){
	$data = trim($data);
	$data = stripcslashes($data);
	$data = htmlspecialchars($data);
	return $data;
}
try {
	$db = new PDO("sqlite:database/database.db");
} catch (PDOException $e) {
	echo "Connection error ".$e->getMessage();
}
//signup
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "signup") {
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
	if ($user["username"] != $username) {
		if ($firstname != "" && $lastname != "" && $email != "" && $phone != "" && $username != "" && $password != "") {
			$sql = "INSERT INTO user (firstname, lastname, email, phone, reg_date, permission, username, password) VALUES ('$firstname', '$lastname', '$email', '$phone', '$date', 'allow', '$username', '$password')";
			$query = $db->prepare($sql);
			if ($query->execute()) {
				echo "success";
			}
		}
	}else{
		echo "usernameexists";
	}
}
//login
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "login") {
	$username = filter($_POST["username"]);
	$password = md5($_POST["password"]);
	$sql = "SELECT * FROM user WHERE username = '$username'";
	$query = $db->prepare($sql);
	$query->execute();
	$data = $query->fetch();
	$dataSet = array();
	if ($data["username"] == $username && $data["password"] == $password) {
		$arrayData["id"] = $data["id"];
		$arrayData["firstname"] = $data["firstname"];
		$arrayData["lastname"] = $data["lastname"];
		$arrayData["email"] = $data["email"];
		$arrayData["phone"] = $data["phone"];
		$arrayData["reg_date"] = $data["reg_date"];
		array_push($dataSet, $arrayData);
		echo json_encode($dataSet);
	}else{
		echo "failed";
	}
}
//fetch budget data
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "fetch_budget") {
	$user_id = $_POST["user_id"];
	$sql = "SELECT * FROM budget WHERE user_id = $user_id ORDER BY date DESC";
	$query = $db->prepare($sql);
	$query->execute();
	$dataSet = array();
	while ($data = $query->fetch(PDO::FETCH_OBJ)) {
		$arrayData["id"] = $data->id;
		$arrayData["title"] = $data->title;
		$arrayData["description"] = $data->description;
		$arrayData["date"] = $data->date;
		$arrayData["amount"] = $data->amount;
		array_push($dataSet, $arrayData);
	}
	echo json_encode($dataSet);
}
//fetch data
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "fetch_data") {
	$user_id = $_POST["user_id"];
	$budget_id = $_POST["budget_id"];
	$sql = "SELECT * FROM data WHERE user_id = $user_id AND budget_id = '$budget_id' ORDER BY date DESC";
	$query = $db->prepare($sql);
	$query->execute();
	$dataSet = array();
	while ($data = $query->fetch(PDO::FETCH_OBJ)) {
		$arrayData["id"] = $data->id;
		$arrayData["date"] = $data->date;
		$arrayData["amount"] = $data->amount;
		$arrayData["description"] = $data->description;
		array_push($dataSet, $arrayData);
	}
	echo json_encode($dataSet);
}
//search data
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "search_data") {
	$user_id = $_POST["user_id"];
	$budget_id = $_POST["budget_id"];
	$keyword = $_POST["keyword"];
	$sql = "SELECT * FROM data WHERE user_id = $user_id AND budget_id = '$budget_id' AND (date LIKE '%$keyword%' OR amount LIKE '%$keyword%' OR description LIKE '%$keyword%') ORDER BY date DESC";
	$query = $db->prepare($sql);
	$query->execute();
	$dataSet = array();
	while ($data = $query->fetch(PDO::FETCH_OBJ)) {
		$arrayData["id"] = $data->id;
		$arrayData["date"] = $data->date;
		$arrayData["amount"] = $data->amount;
		$arrayData["description"] = $data->description;
		array_push($dataSet, $arrayData);
	}
	echo json_encode($dataSet);
}
//create budget
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "create_budget") {
	$user_id = filter($_POST["user_id"]);
	$password = md5($_POST["password"]);
	$title = filter($_POST["title"]);
	$description = filter($_POST["description"]);
	$date = filter($_POST["date"]);
	$amount = filter($_POST["amount"]);
	$sql = "SELECT password FROM user WHERE id = $user_id";
	$query = $db->prepare($sql);
	$query->execute();
	$data = $query->fetch();
	$token = $data["password"];
	if ($title != "" && $date != "" && $amount != "" && $token == $password) {
		$sql = "INSERT INTO budget(user_id, title, description, date, amount) VALUES ('$user_id', '$title', '$description', '$date', '$amount')";
		$query = $db->prepare($sql);
		if ($query->execute()) {
			echo "success";
		}
	}
}
//create data
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "create_data") {
	$user_id = filter($_POST["user_id"]);
	$budget_id = filter($_POST["budget_id"]);
	$password = md5($_POST["password"]);
	$date = filter($_POST["date"]);
	$amount = filter($_POST["amount"]);
	$description = filter($_POST["description"]);
	$sql = "SELECT password FROM user WHERE id = $user_id";
	$query = $db->prepare($sql);
	$query->execute();
	$data = $query->fetch();
	$token = $data["password"];
	if ($user_id != "" && $budget_id != "" && $date != "" && $amount != "" && $description != "" && $token == $password) {
		$sql = "INSERT INTO data(user_id, budget_id, date, amount, description) VALUES ('$user_id', '$budget_id', '$date', '$amount', '$description')";
		$query = $db->prepare($sql);
		if ($query->execute()) {
			echo "success";
		}
	}
}
//update profile
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "update_profile") {
	$user_id = filter($_POST["user_id"]);
	$password = md5($_POST["password"]);
	$firstname = filter($_POST["firstname"]);
	$lastname = filter($_POST["lastname"]);
	$email = filter($_POST["email"]);
	$phone = filter($_POST["phone"]);
	$new_password = md5($_POST["new_password"]);
	$sql = "SELECT password FROM user WHERE id = $user_id";
	$query = $db->prepare($sql);
	$query->execute();
	$data = $query->fetch();
	$token = $data["password"];
	if ($firstname != "" && $lastname != "" && $email != "" && $phone != "" && $new_password != "" && $token == $password) {
		$sql = "UPDATE user SET firstname = '$firstname', lastname = '$lastname', email = '$email', phone = '$phone', password = '$new_password' WHERE id = $user_id";
		$query = $db->prepare($sql);
		if ($query->execute()) {
			echo "success";
		}
	}
}
//update budget
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "update_budget") {
	$user_id = filter($_POST["user_id"]);
	$budget_id = filter($_POST["budget_id"]);
	$password = md5($_POST["password"]);
	$title = filter($_POST["title"]);
	$description = filter($_POST["description"]);
	$date = filter($_POST["date"]);
	$amount = filter($_POST["amount"]);
	$sql = "SELECT password FROM user WHERE id = $user_id";
	$query = $db->prepare($sql);
	$query->execute();
	$data = $query->fetch();
	$token = $data["password"];
	if ($title != "" && $date != "" && $amount != "" && $token == $password) {
		$sql = "UPDATE budget SET title = '$title', description = '$description', date = '$date', amount = '$amount' WHERE user_id = $user_id AND id = '$budget_id'";
		$query = $db->prepare($sql);
		if ($query->execute()) {
			echo "success";
		}
	}
}
//update data
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "update_data") {
	$user_id = filter($_POST["user_id"]);
	$data_id = filter($_POST["data_id"]);
	$password = md5($_POST["password"]);
	$date = filter($_POST["date"]);
	$amount = filter($_POST["amount"]);
	$description = filter($_POST["description"]);
	$sql = "SELECT password FROM user WHERE id = $user_id";
	$query = $db->prepare($sql);
	$query->execute();
	$data = $query->fetch();
	$token = $data["password"];
	if ($user_id != "" && $date != "" && $amount != "" && $description != "" && $token == $password) {
		$sql = "UPDATE data SET date = '$date', amount = '$amount', description = '$description' WHERE id = '$data_id'";
		$query = $db->prepare($sql);
		if ($query->execute()) {
			echo "success";
		}
	}
}
//delete user
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "delete_profile") {
	$user_id = filter($_POST["user_id"]);
	$password = md5($_POST["password"]);
	$sql = "SELECT password FROM user WHERE id = $user_id";
	$query = $db->prepare($sql);
	$query->execute();
	$data = $query->fetch();
	$token = $data["password"];
	if ($token == $password) {
		$sql = "DELETE FROM user WHERE id = $user_id";
		$query = $db->prepare($sql);
		if ($query->execute()) {
			$sql = "DELETE FROM budget WHERE user_id = $user_id";
			$query = $db->prepare($sql);
			if ($query->execute()) {
				$sql = "DELETE FROM data WHERE user_id = $user_id";
				$query = $db->prepare($sql);
				if ($query->execute()) {
					echo "success";
				}
			}
		}
	}
}
//delete budget
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "delete_budget") {
	$user_id = filter($_POST["user_id"]);
	$budget_id = filter($_POST["budget_id"]);
	$password = md5($_POST["password"]);
	$sql = "SELECT password FROM user WHERE id = $user_id";
	$query = $db->prepare($sql);
	$query->execute();
	$data = $query->fetch();
	$token = $data["password"];
	if ($token == $password) {
		$sql = "DELETE FROM budget WHERE id = '$budget_id'";
		$query = $db->prepare($sql);
		if ($query->execute()) {
			$sql = "DELETE FROM data WHERE budget_id = '$budget_id'";
			$query = $db->prepare($sql);
			if ($query->execute()) {
				echo "success";
			}
		}
	}
}
//delete data
if ($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["action"] == "delete_data") {
	$user_id = filter($_POST["user_id"]);
	$data_id = filter($_POST["data_id"]);
	$password = md5($_POST["password"]);
	$sql = "SELECT password FROM user WHERE id = $user_id";
	$query = $db->prepare($sql);
	$query->execute();
	$data = $query->fetch();
	$token = $data["password"];
	if ($token == $password) {
		$sql = "DELETE FROM data WHERE id = '$data_id'";
		$query = $db->prepare($sql);
		if ($query->execute()) {
			echo "success";
		}
	}
}
?>