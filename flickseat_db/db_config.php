<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

$servername = "localhost";
$username = "root";      // default XAMPP MySQL username
$password = "";          // default XAMPP MySQL password is blank
$dbname = "flickseat_db";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    // Return a JSON error response and exit if connection fails.
    header("Content-Type: application/json");
    echo json_encode(["status" => "error", "message" => "Connection failed: " . $conn->connect_error]);
    exit;
}
?>
