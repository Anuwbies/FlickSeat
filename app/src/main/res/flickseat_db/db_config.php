<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "flickseat_db";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {

    header("Content-Type: application/json");
    echo json_encode(["status" => "error", "message" => "Connection failed: " . $conn->connect_error]);
    exit;
}
?>
