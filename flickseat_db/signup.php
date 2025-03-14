<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

error_reporting(E_ALL);
ini_set('display_errors', 1);

include 'db_config.php';

$email = $_POST['email'] ?? '';
$username = $_POST['username'] ?? '';
$password = $_POST['password'] ?? '';

if (empty($email) || empty($username) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "Please fill in all fields."]);
    exit;
}

// Find the first available user_id, including 1 if missing
$missingIdQuery = "SELECT MIN(t1.user_id + 1) AS available_id 
                   FROM users t1 
                   LEFT JOIN users t2 ON t1.user_id + 1 = t2.user_id 
                   WHERE t2.user_id IS NULL 
                   AND t1.user_id + 1 > 1";

$missingIdResult = $conn->query($missingIdQuery);
$availableUserId = null;

// If there's a gap in the sequence, use the first missing ID
if ($missingIdResult && $missingIdResult->num_rows > 0) {
    $row = $missingIdResult->fetch_assoc();
    $availableUserId = $row['available_id'];
}

// Check if user ID 1 is available
$checkId1Query = "SELECT user_id FROM users WHERE user_id = 1";
$checkId1Result = $conn->query($checkId1Query);
if ($checkId1Result->num_rows == 0) {
    $availableUserId = 1; // Assign user_id = 1 if missing
}

// If no gaps were found, get the next highest ID
if ($availableUserId === null) {
    $maxIdQuery = "SELECT IFNULL(MAX(user_id), 0) + 1 AS next_id FROM users";
    $maxIdResult = $conn->query($maxIdQuery);
    $maxIdRow = $maxIdResult->fetch_assoc();
    $availableUserId = $maxIdRow['next_id'];
}

// Check if username exists first
$checkUsernameQuery = "SELECT username FROM users WHERE username = ?";
$stmt = $conn->prepare($checkUsernameQuery);
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(["status" => "error", "message" => "Username already exists."]);
    exit;
}

// Check if email exists
$checkEmailQuery = "SELECT email FROM users WHERE email = ?";
$stmt = $conn->prepare($checkEmailQuery);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(["status" => "error", "message" => "Email already exists."]);
    exit;
}

// Hash the password
$hashedPassword = password_hash($password, PASSWORD_DEFAULT);

// Insert new user with the available user_id
$sql = "INSERT INTO users (user_id, email, username, password) VALUES (?, ?, ?, ?)";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Database prepare failed: " . $conn->error]);
    exit;
}

$stmt->bind_param("isss", $availableUserId, $email, $username, $hashedPassword);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "User registered successfully.", "user_id" => $availableUserId]);
} else {
    echo json_encode(["status" => "error", "message" => "User registration failed: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
