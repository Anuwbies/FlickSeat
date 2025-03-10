<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

error_reporting(E_ALL);
ini_set('display_errors', 1);

include 'db_config.php';

$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

if (empty($email) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "Please fill in all fields."]);
    exit;
}

$sql = "SELECT * FROM users WHERE email = ?";
$stmt = $conn->prepare($sql);
if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Database prepare failed: " . $conn->error]);
    exit;
}
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();  

if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();
    if (password_verify($password, $user['password'])) {
        echo json_encode([
            "status" => "success",
            "message" => "Login successful.",
            "user" => [
                "user_id" => $user['user_id'], 
                "email" => $user['email'],
                "username" => $user['username']
            ]
        ]);
    } else {
        echo json_encode(["status" => "error", "message" => "Incorrect email or password."]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Incorrect email or password."]);
}

$stmt->close();
$conn->close();
?>
