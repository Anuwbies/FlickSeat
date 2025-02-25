<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

error_reporting(E_ALL);
ini_set('display_errors', 1);

include 'db_config.php';

$sql = "SELECT * FROM movie WHERE status = 'coming soon'";
$result = $conn->query($sql);

$movies = array();
if ($result && $result->num_rows > 0) {
    while($row = $result->fetch_assoc()){
        $movies[] = $row;
    }
    echo json_encode(["status" => "success", "movies" => $movies]);
} else {
    echo json_encode(["status" => "error", "message" => "No coming soon movies found."]);
}

$conn->close();
?>
